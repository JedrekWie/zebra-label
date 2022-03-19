package io.sharptree.maximo.app.label;

import psdi.mbo.MboConstants;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;

import java.rmi.RemoteException;

@SuppressWarnings("unused")
public class FldSiteID extends psdi.app.site.FldSiteID {
    public FldSiteID(MboValue mbv) throws MXException {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        if (!getMboValue().isNull()) {
            getMboValue("LOCATION").setFlag(MboConstants.READONLY, false);

            if (!getMboValue("LOCATION").isNull() && !getMboValue().getMbo().getString("LOCATIONS.SITEID").equals(getMboValue().getString())) {
                getMboValue("LOCATION").setValueNull();
            }
        } else {
            getMboValue("LOCATION").setFlag(MboConstants.READONLY, true);
        }
        super.action();
    }

    @Override
    public void setValueFromLookup(MboRemote sourceMbo) throws MXException, RemoteException {
        if(sourceMbo!=null && sourceMbo.isBasedOn("SITE")){
            getMboValue("ORGID").setValue( sourceMbo.getString("ORGID"), MboConstants.NOACCESSCHECK|MboConstants.NOVALIDATION);
        }
        super.setValueFromLookup(sourceMbo);
    }
}
