package com.wipro.ats.bdre.md.plugins;

import java.util.List;

/**
 * Created by cloudera on 8/10/16.
 */
public class PluginStore {
    List<PluginStoreJson> applicationList;
    public List<PluginStoreJson> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<PluginStoreJson> applicationList) {
        this.applicationList = applicationList;
    }
}
