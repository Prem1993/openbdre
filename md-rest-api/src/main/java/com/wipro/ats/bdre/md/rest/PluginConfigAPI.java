/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.MDConfig;
import com.wipro.ats.bdre.exception.MetadataException;
import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.PluginConfig;
import com.wipro.ats.bdre.md.dao.InstalledPluginsDAO;
import com.wipro.ats.bdre.md.dao.PluginConfigDAO;
import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfigId;
import com.wipro.ats.bdre.md.dao.jpa.Process;
import com.wipro.ats.bdre.md.plugins.PluginStore;
import com.wipro.ats.bdre.md.plugins.PluginStoreJson;
import com.wipro.ats.bdre.md.plugins.PluginValue;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/pluginconfig")


public class PluginConfigAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(PluginConfigAPI.class);
    private static final String WRITE="write";

    @Autowired
    private PluginConfigDAO pluginConfigDAO;
    @Autowired
    private InstalledPluginsDAO installedPluginsDAO;


    /**
     * This method calls proc ListProperty and fetches a list of instances of Properties.
     *
     * @param
     * @return restWrapper It contains a list of instances of Properties.
     */
   /* @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=pluginConfigDAO.totalRecordCount();
            List<PluginConfig> getPluginConfigs = new ArrayList<PluginConfig>();
            for (String pluginUniqueId : pluginConfigDAO.list(startPage, pageSize)) {
                com.wipro.ats.bdre.md.beans.table.PluginConfig returnPluginConfig = new com.wipro.ats.bdre.md.beans.table.PluginConfig();
                returnPluginConfig.setPluginUniqueId(pluginUniqueId);
                returnPluginConfig.setCounter(counter);
                getPluginConfigs.add(returnPluginConfig);
            }


            restWrapper = new RestWrapper(getPluginConfigs, RestWrapper.OK);
            LOGGER.info("All records listed from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }*/

    /**
     * This method calls proc ListProperties and fetches a list of records from properties table corresponding to
     * processId passed.
     *
     * @param
     * @return restWrapper It contains list of instances of properties corresponding to processId passed.
     */
    @RequestMapping(value = {"","/"}, method = RequestMethod.GET)
    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                            @RequestParam(value = "size", defaultValue = "10") int pageSize,
                            @ModelAttribute("pluginconfig") @Valid PluginConfig tablePluginConfig, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            if(tablePluginConfig.getPluginUniqueId()!=null) {
                List<PluginConfig> getPluginConfigs = new ArrayList<PluginConfig>();
                List<com.wipro.ats.bdre.md.dao.jpa.PluginConfig> pluginConfigList = new ArrayList<com.wipro.ats.bdre.md.dao.jpa.PluginConfig>();
                LOGGER.info(tablePluginConfig.getPluginUniqueId());
                pluginConfigList = pluginConfigDAO.getConfigForPlugin(tablePluginConfig.getPluginUniqueId(), startPage, pageSize);
                LOGGER.info("size of returned object is " + pluginConfigList.size());
                Integer counter = pluginConfigList.size();
                for (com.wipro.ats.bdre.md.dao.jpa.PluginConfig pluginConfig : pluginConfigList) {
                    com.wipro.ats.bdre.md.beans.table.PluginConfig returnPluginConfig = new com.wipro.ats.bdre.md.beans.table.PluginConfig();
                    returnPluginConfig.setPluginUniqueId(pluginConfig.getId().getPluginUniqueId());
                    returnPluginConfig.setConfigGroup(pluginConfig.getId().getConfigGroup());
                    returnPluginConfig.setPluginKey(pluginConfig.getId().getPluginKey());
                    returnPluginConfig.setPluginValue(pluginConfig.getId().getPluginValue());
                    returnPluginConfig.setCounter(counter);
                    getPluginConfigs.add(returnPluginConfig);
                }

                restWrapper = new RestWrapper(getPluginConfigs, RestWrapper.OK);
                LOGGER.info("Record with ID:" + tablePluginConfig.getPluginUniqueId() + "selected from Plugin Config by User:" + principal.getName());
            }
            else{
                Integer counter=pluginConfigDAO.totalRecordCount();
                List<PluginConfig> getPluginConfigs = new ArrayList<PluginConfig>();
                for (String pluginUniqueId : pluginConfigDAO.list(startPage, pageSize)) {
                    com.wipro.ats.bdre.md.beans.table.PluginConfig returnPluginConfig = new com.wipro.ats.bdre.md.beans.table.PluginConfig();
                    returnPluginConfig.setPluginUniqueId(pluginUniqueId);
                    returnPluginConfig.setCounter(counter);
                    getPluginConfigs.add(returnPluginConfig);
                }


                restWrapper = new RestWrapper(getPluginConfigs, RestWrapper.OK);
                LOGGER.info("All records listed from Properties by User:" + principal.getName());
            }

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteProperties and deletes  records from Properties table
     * corresponding to processId passed.
     *
     * @param pluginUniqueId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(@PathVariable("id") String pluginUniqueId, Principal principal) {
        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.Process process = new Process();
            pluginConfigDAO.deleteByPluginId(pluginUniqueId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + pluginUniqueId + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc DeleteProperty and deletes an entry corresponding to  particular processId and key passed.
     *
     * @param pluginUniqueId
     * @param key
     * @return nothing.
     */
    @RequestMapping(value = "/{id}/{k}/", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(
            @PathVariable("id") String pluginUniqueId,
            @PathVariable("k") String key, Principal principal) {

        RestWrapper restWrapper = null;
        try {

            com.wipro.ats.bdre.md.dao.jpa.PluginConfigId pluginConfigId = new com.wipro.ats.bdre.md.dao.jpa.PluginConfigId();
            pluginConfigId.setPluginKey(key);
            pluginConfigId.setPluginUniqueId(pluginUniqueId);
            pluginConfigDAO.delete(pluginConfigId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + pluginUniqueId + "," + key + " deleted from Properties by User:" + principal.getName());

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    /**
     * This method calls proc UpdateProperties and updates the values passed. It also validates the values passed.
     *
     * @param pluginConfig    Instance of Properties.
     * @param bindingResult
     * @return restWrapper It contains updated instance of Properties.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper update(@ModelAttribute("pluginconfig")
                              @Valid PluginConfig pluginConfig, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.PluginConfig updatePluginConfig = new com.wipro.ats.bdre.md.dao.jpa.PluginConfig();
            PluginConfigId pluginConfigId = new PluginConfigId();
            pluginConfigId.setPluginKey(pluginConfig.getPluginKey());
            pluginConfigId.setPluginUniqueId(pluginConfig.getPluginUniqueId());
            pluginConfigId.setPluginValue(pluginConfig.getPluginValue());
            pluginConfigId.setConfigGroup(pluginConfig.getConfigGroup());
            updatePluginConfig.setId(pluginConfigId);
            InstalledPlugins installedPlugins=new InstalledPlugins();
            installedPlugins.setPluginUniqueId(pluginConfig.getPluginUniqueId());
            updatePluginConfig.setInstalledPlugins(installedPlugins);

            pluginConfigDAO.update(updatePluginConfig);
            restWrapper = new RestWrapper(pluginConfig, RestWrapper.OK);
            LOGGER.info("Record with ID:" + pluginConfig.getPluginUniqueId() + " updated in Properties by User:" + principal.getName() + pluginConfig);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc InsertProperties and adds a record in properties table. It also validates the
     * values passed.
     *
     * @param pluginConfig    Instance of properties.
     * @param bindingResult
     * @return restWrapper It contains instance of Properties passed.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insert(@ModelAttribute("pluginconfig")
                              @Valid PluginConfig pluginConfig, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.PluginConfig insertPluginConfig = new com.wipro.ats.bdre.md.dao.jpa.PluginConfig();
            PluginConfigId pluginConfigId = new PluginConfigId();
            pluginConfigId.setPluginKey(pluginConfig.getPluginKey());
            pluginConfigId.setPluginUniqueId(pluginConfig.getPluginUniqueId());
            pluginConfigId.setPluginValue(pluginConfig.getPluginValue());
            pluginConfigId.setConfigGroup(pluginConfig.getConfigGroup());
            insertPluginConfig.setId(pluginConfigId);
            pluginConfigDAO.insert(insertPluginConfig);
            restWrapper = new RestWrapper(pluginConfig, RestWrapper.OK);
            LOGGER.info("Record with ID:" + pluginConfig.getPluginUniqueId() + " inserted in Properties by User:" + principal.getName() + pluginConfig);

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        catch (SecurityException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @RequestMapping(value = {"/install", "/install/"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper importData(@ModelAttribute("fileString")
                                  @Valid String gitUrl,
                                  @ModelAttribute("id") @Valid String pluginUniqueId, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            LOGGER.info("git hub url and plugin unique id are "+gitUrl+"  "+pluginUniqueId);
            int  plugin_name_st_index=gitUrl.lastIndexOf('/');
            int  plugin_name_last_index=gitUrl.lastIndexOf('.');
            String plugin_name=gitUrl.substring(plugin_name_st_index+1,plugin_name_last_index);
            LOGGER.info("Name of the plugin is "+plugin_name);
            ProcessBuilder pb = new ProcessBuilder(MDConfig.getProperty("deploy.script-path") + "/install-plugin.sh", gitUrl,plugin_name);
            java.lang.Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            int exitVal=9;
            while ((line = reader.readLine()) != null)
            {
                LOGGER.info(line);
            }
            reader.close();
            try {
                exitVal = p.waitFor();
                LOGGER.info("Exit Value: " + exitVal);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (exitVal==0) {
                String temp;
                BufferedReader br = null;
                String jsonfile = "";
                String homeDir = System.getProperty("user.home");
                br = new BufferedReader(new FileReader(homeDir + "/pluginappstore/store.json"));
                while ((temp = br.readLine()) != null) {
                    jsonfile = jsonfile + temp;
                }
                LOGGER.info("Plugin store is " + jsonfile);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                PluginStore pluginStore = mapper.readValue(jsonfile, PluginStore.class);
                LOGGER.info("size of app catagory is " + pluginStore.getApplicationList().size());
                PluginValue installedPlugin = new PluginValue();
                for (PluginStoreJson pluginStoreJson : pluginStore.getApplicationList()) {
                    if (pluginStoreJson.getId().equals("available")) {
                        LOGGER.info("no of available plugins which is not installed yet  " + pluginStoreJson.getColumns().size());
                        // for (PluginValue pluginValue:pluginStoreJson.getColumns())
                        Iterator<PluginValue> iterator = pluginStoreJson.getColumns().iterator();
                        //for (Integer i=0;i<pluginStoreJson.getColumns().size();i++)
                        while (iterator.hasNext()) {
                            PluginValue pluginValue = iterator.next();
                            if (pluginValue.getPlugin_unique_id().equals(pluginUniqueId)) {
                                installedPlugin = pluginValue;
                                iterator.remove();
                            }
                        }
                        LOGGER.info("no of available plugins which is not installed yet after removing from plugin store  " + pluginStoreJson.getColumns().size());
                    }
                }

                if (installedPlugin != null) {
                    for (PluginStoreJson pluginStoreJson : pluginStore.getApplicationList()) {
                        if (pluginStoreJson.getId().equals("installed")) {
                            LOGGER.info("no of installed plugins before current plugin is installed " + pluginStoreJson.getColumns().size());
                            pluginStoreJson.getColumns().add(installedPlugin);
                            LOGGER.info("no of installed plugins after current plugin is installed " + pluginStoreJson.getColumns().size());
                        }
                    }
                }
                ObjectMapper mapper1 = new ObjectMapper();
                FileWriter fileOut = new FileWriter(homeDir + "/pluginappstore/store.json");
                LOGGER.info(fileOut);
                mapper1.writeValue(new File(homeDir + "/pluginappstore/store.json"), pluginStore);
                restWrapper = new RestWrapper(null, RestWrapper.OK);
            }  else
            {
                restWrapper = new RestWrapper("error in uninstallation", RestWrapper.ERROR);
            }
        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (IOException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @RequestMapping(value = {"/uninstall", "/uninstall/"}, method = RequestMethod.POST)
    @ResponseBody
    public RestWrapper unInstallPlugin(@ModelAttribute("fileString")
                                  @Valid String pluginUniqueId, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            //llUninstallPluginMain uninstallPluginMain=new UninstallPluginMain("");
            LOGGER.info("plugin unique id  is "+pluginUniqueId);
            ProcessBuilder pb = new ProcessBuilder(MDConfig.getProperty("deploy.script-path") + "/uninstall-plugin.sh", pluginUniqueId);
            java.lang.Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            int exitVal=9;
            while ((line = reader.readLine()) != null)
            {
                System.out.println(line);
            }
            reader.close();
            try {
                exitVal = p.waitFor();
                LOGGER.info("Exit Value: " + exitVal);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           if(exitVal==0) {
               String temp;
               BufferedReader br = null;
               String jsonfile = "";
               String homeDir = System.getProperty("user.home");
               br = new BufferedReader(new FileReader(homeDir + "/pluginappstore/store.json"));
               while ((temp = br.readLine()) != null) {
                   jsonfile = jsonfile + temp;
               }
               LOGGER.info("Plugin store is " + jsonfile);
               ObjectMapper mapper = new ObjectMapper();
               mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
               PluginStore pluginStore = mapper.readValue(jsonfile, PluginStore.class);
               LOGGER.info("size of app catagory is " + pluginStore.getApplicationList().size());
               PluginValue unInstalledPlugin = new PluginValue();
               for (PluginStoreJson pluginStoreJson : pluginStore.getApplicationList()) {
                   if (pluginStoreJson.getId().equals("installed")) {
                       LOGGER.info("no of installed plugins which is not installed yet  " + pluginStoreJson.getColumns().size());
                       // for (PluginValue pluginValue:pluginStoreJson.getColumns())
                       Iterator<PluginValue> iterator = pluginStoreJson.getColumns().iterator();
                       //for (Integer i=0;i<pluginStoreJson.getColumns().size();i++)
                       while (iterator.hasNext()) {
                           PluginValue pluginValue = iterator.next();
                           if (pluginValue.getPlugin_unique_id().equals(pluginUniqueId)) {
                               unInstalledPlugin = pluginValue;
                               iterator.remove();
                           }
                       }
                       LOGGER.info("no of available plugins which is  installed yet after removing from installed plugin store  " + pluginStoreJson.getColumns().size());
                   }
               }

               if (unInstalledPlugin != null) {
                   for (PluginStoreJson pluginStoreJson : pluginStore.getApplicationList()) {
                       if (pluginStoreJson.getId().equals("available")) {
                           LOGGER.info("no of available plugins before current plugin is uninstalled " + pluginStoreJson.getColumns().size());
                           pluginStoreJson.getColumns().add(unInstalledPlugin);
                           LOGGER.info("no of available plugins after current plugin is uninstalled " + pluginStoreJson.getColumns().size());
                       }
                   }
               }
               ObjectMapper mapper1 = new ObjectMapper();
               FileWriter fileOut = new FileWriter(homeDir + "/pluginappstore/store.json");
               LOGGER.info(fileOut);
               mapper1.writeValue(new File(homeDir + "/pluginappstore/store.json"), pluginStore);
               restWrapper = new RestWrapper(null, RestWrapper.OK);
           }
            else
           {
               restWrapper = new RestWrapper("error in uninstallation", RestWrapper.ERROR);
           }

        } catch (MetadataException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }catch (IOException e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }


    @Override
    public Object execute(String[] params) {
        return null;
    }
}
