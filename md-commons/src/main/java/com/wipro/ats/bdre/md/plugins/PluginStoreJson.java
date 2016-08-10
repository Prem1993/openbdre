package com.wipro.ats.bdre.md.plugins;

import java.util.List;

/**
 * Created by cloudera on 8/10/16.
 */
public class PluginStoreJson {
    private String name;
    private String id;
    private List<PluginValue> columns;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PluginValue> getColumns() {
        return columns;
    }

    public void setColumns(List<PluginValue> columns) {
        this.columns = columns;
    }
}
