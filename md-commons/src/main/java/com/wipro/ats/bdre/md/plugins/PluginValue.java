package com.wipro.ats.bdre.md.plugins;

/**
 * Created by cloudera on 8/10/16.
 */
public class PluginValue {
    private String category;
    private String plugin_unique_id;
    private String description;
    private String location;
    private String name;
    private String icon;

    public String getPlugin_unique_id() {
        return plugin_unique_id;
    }

    public void setPlugin_unique_id(String plugin_unique_id) {
        this.plugin_unique_id = plugin_unique_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
