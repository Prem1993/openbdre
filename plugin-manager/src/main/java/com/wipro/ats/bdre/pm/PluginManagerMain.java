package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.exception.BDREException;
import com.wipro.ats.bdre.md.api.InstalledPlugins;
import com.wipro.ats.bdre.md.api.PluginDependency;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import com.wipro.ats.bdre.md.pm.beans.PluginConfig;
import org.apache.log4j.Logger;


/**
 * Created by cloudera on 5/31/16.
 */
public class PluginManagerMain {

    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public static void main(String[] args) throws Exception {
        String pluginDescriptorJSON = "";
        if(args.length == 0){
            LOGGER.error("Zip Path is not provided. Aborting...");
        } else {
            // unzipping the zip
            PluginExploder pluginExploder = new PluginExploder();
            pluginDescriptorJSON = pluginExploder.explode(args);
            PluginDescriptorReader pluginDescriptorReader = new PluginDescriptorReader();
            Plugin plugin = pluginDescriptorReader.jsonReader(pluginDescriptorJSON + "/plugin.json");
            PluginDependencyResolver pluginDependencyResolver = new PluginDependencyResolver();
            // checking whether plugin is installed or not
            InstalledPlugins installedPlugins = new InstalledPlugins();
            com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins installedPlugins1 = installedPlugins.get(plugin.getPluginDetails().getPluginId() + "-" + plugin.getPluginDetails().getVersion());
            if(installedPlugins1 != null){
                throw new BDREException("plugin already installed");
            }
            String pluginUniqueId;
            if(pluginDependencyResolver.dependencyCheck(plugin)){
               pluginUniqueId = installedPlugins.insert(plugin.getPluginDetails());
                PluginInstaller pluginInstaller = new PluginInstaller();
                pluginInstaller.install(plugin,pluginDescriptorJSON);
            }else{
                LOGGER.error("plugin dependency not met can't install your plugin");
                throw new Exception();
            }
            //adding plugin into INSTALLED_PLUGINS table to make an entry

            // creating entries in PLUGIN_DEPENDENCY table related to installed plugin
            PluginDependency pluginDependency = new PluginDependency();
            pluginDependency.insert(plugin);
            // adding configuaration for installed plugin in PLUGIN_CONFIG table
            PluginConfig pluginConfigBean=new PluginConfig();
            pluginConfigBean.setConfigGroup("uninstall");
            pluginConfigBean.setKey("expended jar location");
            pluginConfigBean.setValue(pluginDescriptorJSON);
            plugin.getPluginConfig().add(pluginConfigBean);
            com.wipro.ats.bdre.md.api.PluginConfig pluginConfig = new com.wipro.ats.bdre.md.api.PluginConfig();
            pluginConfig.insert(plugin.getPluginConfig(),pluginUniqueId);

        }

    }
}

