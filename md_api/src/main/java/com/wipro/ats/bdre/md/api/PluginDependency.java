package com.wipro.ats.bdre.md.api;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.dao.PluginDependencyDAO;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloudera on 6/6/16.
 */

public class PluginDependency extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ProcessLog.class);

    @Autowired
    PluginDependencyDAO pluginDependencyDAO;
    @Autowired
    InstalledPluginsDAO installedPluginsDAO;

    public PluginDependency() {
        AutowireCapableBeanFactory acbFactory = getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    public Object execute(String[] params) {
        return null;
    }

    public void insert(Plugin plugin){
        for (com.wipro.ats.bdre.md.pm.beans.PluginDependency pluginDependency : plugin.getPluginDependency()){
            com.wipro.ats.bdre.md.dao.jpa.PluginDependency pluginDependencyJPA = new com.wipro.ats.bdre.md.dao.jpa.PluginDependency();
            com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPluginsJPAInstalling = installedPluginsDAO.get(plugin.getPluginDetails().getPluginId() + "-" + plugin.getPluginDetails().getVersion());
            pluginDependencyJPA.setInstalledPluginsByPluginUniqueId(installedPluginsJPAInstalling);
            com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPluginsJPAInstalled = installedPluginsDAO.get(pluginDependency.getPluginId() + "-" + pluginDependency.getVersion());
            pluginDependencyJPA.setInstalledPluginsByDependentPluginUniqueId(installedPluginsJPAInstalled);
            int pluginDependencyId = pluginDependencyDAO.insert(pluginDependencyJPA);
            pluginDependencyJPA.setDependencyId(pluginDependencyId);
        }
    }

    public List<com.wipro.ats.bdre.md.pm.beans.PluginDependency> getDependencies(String pluginUniqueId){
        List<com.wipro.ats.bdre.md.pm.beans.PluginDependency> pluginDependencies = new ArrayList<com.wipro.ats.bdre.md.pm.beans.PluginDependency>();
        return pluginDependencies;
    }

    public List<String> dependencyCheckUnInstallPlugin(String pluginUniqueId){
        List<String> pluginDependencies=pluginDependencyDAO.dependencyCheckUnintallPlugin(pluginUniqueId);
        return pluginDependencies;
    }
    public void deleteByPluginUniqueId(String pluginUniqueId)
    {

    }
}