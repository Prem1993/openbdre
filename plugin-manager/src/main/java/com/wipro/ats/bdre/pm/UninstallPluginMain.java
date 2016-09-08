package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.InstalledPlugins;
import com.wipro.ats.bdre.md.api.PluginDependency;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.apache.log4j.Logger;

/**
 * Created by cloudera on 7/11/16.
 */
public class UninstallPluginMain {

    private static final Logger LOGGER = Logger.getLogger(UninstallPluginMain.class);
    public static void main(String[] args) throws Exception {
        String pluginDescriptorJSON = "";
            String pluginUniqueID=args[0];
        // checking whether plugin is installed or not
          InstalledPlugins installedPlugins = new InstalledPlugins();
          com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPlugins1 = installedPlugins.get(pluginUniqueID);
        if(installedPlugins1 == null){
            throw new BDREException("plugin is not installed");
        }
        if (installedPlugins1.getUninstallable()==false)
        {
            throw new BDREException("plugin is not allowed to unInstall");
        }
            com.wipro.ats.bdre.md.api.PluginConfig pluginConfigAPI=new com.wipro.ats.bdre.md.api.PluginConfig();
            LOGGER.info("pluginUniqueid is :"+pluginUniqueID);
            pluginDescriptorJSON=pluginConfigAPI.getWithConfig(pluginUniqueID,"uninstall").get(0);
            PluginDescriptorReader pluginDescriptorReader = new PluginDescriptorReader();
            Plugin plugin = pluginDescriptorReader.jsonReader(pluginDescriptorJSON + "/plugin.json");
            PluginDependencyResolver pluginDependencyResolver = new PluginDependencyResolver();
             java.util.List<String> pluginDependencies = pluginDependencyResolver.dependencyCheckUnInstallPlugin(pluginUniqueID);
            if(pluginDependencies.size()!=0){
                throw new BDREException("plugin dependency not met can't uninstall your plugin plugins dependent on this plugins are "+pluginDependencies);
            }
            else
            {
                PluginUnInstaller pluginUnInstaller = new PluginUnInstaller();
                pluginUnInstaller.unInstall(plugin,pluginDescriptorJSON);
            }
            //delete plugin from PLUGIN_DEPENDENCY
            PluginDependency pluginDependency=new PluginDependency();
            pluginDependency.deleteByPluginUniqueId(pluginUniqueID);

           // Deleting configuaration for unInstalling plugin from PLUGIN_CONFIG table
           com.wipro.ats.bdre.md.api.PluginConfig pluginConfig = new com.wipro.ats.bdre.md.api.PluginConfig();
           pluginConfig.deleteByPluginUniqueId(pluginUniqueID);
            //deleting plugin from INSTALLED_PLUGINS table
            installedPlugins.delete(pluginUniqueID);
    }
}
