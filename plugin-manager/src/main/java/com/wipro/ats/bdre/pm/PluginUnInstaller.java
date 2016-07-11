package com.wipro.ats.bdre.pm;

import com.wipro.ats.bdre.md.api.Import;
import com.wipro.ats.bdre.md.api.PluginConfig;
import com.wipro.ats.bdre.md.pm.beans.DataList;
import com.wipro.ats.bdre.md.pm.beans.FS;
import com.wipro.ats.bdre.md.pm.beans.Plugin;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by cloudera on 7/11/16.
 */
public class PluginUnInstaller {
    private static final Logger LOGGER = Logger.getLogger(PluginManagerMain.class);
    public void unInstall(Plugin plugin, String pluginDescriptorJSON) throws IOException {

        for(FS fs : plugin.getInstall().getFs()){

            if("FILECOPY".equals(fs.getAction())){
                FSOperations fsOperations = new FSOperations();
                fsOperations.unInstallAction(fs,pluginDescriptorJSON);
            }else if("FILEMOVE".equals(fs.getAction())){
                FSOperations fsOperations = new FSOperations();
                fsOperations.unInstallAction(fs,pluginDescriptorJSON);
            }
        }

        for(DataList dataList : plugin.getInstall().getMetadata().getInsert()){
            MetadataActions metadataActions = new MetadataActions();
            metadataActions.unInstallAction(dataList);
        }
        String pluginUniqueId=plugin.getPluginDetails().getPluginId() + "-" + plugin.getPluginDetails().getVersion();
        PluginConfig pluginConfig=new PluginConfig();
        List<String> pluginFileLocations=pluginConfig.getWithConfig(pluginUniqueId,"file");
        for(String file:pluginFileLocations)
        {
            Path path= Paths.get(file);
            Files.deleteIfExists(path);
        }

        List<String> pluginDirLocations=pluginConfig.getWithConfig(pluginUniqueId,"directory");
        for(String dir:pluginDirLocations)
        {
            FileUtils.deleteDirectory(new File(dir));

        }
       /* Import impotObject = new Import();
        WarOperations warOperations = new WarOperations();
        if (!plugin.getInstall().getUiWar().getLocation().isEmpty()) {
            String warLocation = pluginDescriptorJSON + "/" + plugin.getInstall().getUiWar().getLocation();

            File folder = new File(warLocation.substring(0, warLocation.lastIndexOf(".")));
            if (!folder.exists()) {
                folder.mkdir();
            }
            impotObject.unZipIt(warLocation, warLocation.substring(0, warLocation.lastIndexOf(".")));

            warOperations.listOfFiles(folder, folder,"mdui",plugin.getInstall().getUiWar().getLocalizationFile());
        }
        if ( !plugin.getInstall().getRestWar().getLocation().isEmpty()){
            String restWarLocation = pluginDescriptorJSON + "/" + plugin.getInstall().getRestWar().getLocation();
            File folder = new File(restWarLocation.substring(0, restWarLocation.lastIndexOf(".")));
            if(!folder.exists()){
                folder.mkdir();
            }
            impotObject.unZipIt(restWarLocation, restWarLocation.substring(0, restWarLocation.lastIndexOf(".")));
            warOperations.listOfFiles(folder,folder,"mdrest","78909fdds444tr#@$%");

        }*/
    }
}
