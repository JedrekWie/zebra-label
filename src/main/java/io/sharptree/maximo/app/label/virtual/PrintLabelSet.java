package io.sharptree.maximo.app.label.virtual;

import com.ibm.tivoli.maximo.script.ScriptAction;
import psdi.mbo.*;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.logging.FixedLoggers;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class PrintLabelSet extends NonPersistentMboSet {

    /**
     * Creates a new instance of PrintLabelSet.
     *
     * @param ms the owning mbo server.
     * @throws RemoteException thrown if a network error occurs.
     */
    public PrintLabelSet(MboServerInterface ms) throws RemoteException {
        super(ms);
    }

    /**
     * {@inerhitDoc}
     *
     * @see MboSet#getMboInstance(MboSet)
     */
    @Override
    protected Mbo getMboInstance(MboSet mboSet) throws MXException, RemoteException {
        return new PrintLabel(mboSet);
    }

    /**
     * {@inerhitDoc}
     *
     * @see NonPersistentMboSet#execute()
     */
    public void execute() throws MXException, RemoteException {
        MboRemote printLabel = getMbo(0);

        if(printLabel.getInt("COUNT")<1){
            throw new MXApplicationException("sharptree","countLessThanOne");
        }

        int maxCount = 10;

        try {
            Integer.parseInt(MXServer.getMXServer().getProperty("sharptree.zebralabel.maxcount"));
        }catch(Throwable t){
            FixedLoggers.APPLOGGER.error("Error parsing property sharptree.zebralabel.maxcount: " + t.getMessage());
        }

        if(printLabel.getInt("COUNT")> maxCount){
            throw new MXApplicationException("sharptree", "countGreaterThanMax", new String[]{printLabel.getString("COUNT"), String.valueOf(maxCount)});
        }

        try {
            for (int i = 0; i < printLabel.getInt("COUNT"); i++) {
                (new ScriptAction()).applyCustomAction(printLabel, new String[]{"STAUTOSCRIPT.ZEBRALABEL.PRINTLABEL"});
            }
        } catch (Exception e) {
            if (e instanceof MXException) {
                throw (MXException) e;
            } else if (e.getCause() instanceof MXException) {
                throw (MXException) e;
            }
            //TODO handle errors.
            e.printStackTrace();

        }

        // reset the MboSet so related sets such as the temporary domain used for the combo boxes isn't saved.
        reset();
    }
}
