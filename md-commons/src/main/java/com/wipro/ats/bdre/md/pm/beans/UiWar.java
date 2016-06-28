package com.wipro.ats.bdre.md.pm.beans;

import java.util.List;

/**
 * Created by cloudera on 6/13/16.
 */
public class UiWar {


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    private String localizationFile;

    public String getLocalizationFile() {
        return localizationFile;
    }

    public void setLocalizationFile(String localizationFile) {
        this.localizationFile = localizationFile;
    }
}
