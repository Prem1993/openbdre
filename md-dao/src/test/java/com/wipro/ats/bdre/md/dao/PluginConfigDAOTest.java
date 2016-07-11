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
package com.wipro.ats.bdre.md.dao;

import com.wipro.ats.bdre.md.dao.jpa.InstalledPlugins;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfig;
import com.wipro.ats.bdre.md.dao.jpa.PluginConfigId;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by cloudera on 5/27/16.
 */
public class PluginConfigDAOTest {

    private static final Logger LOGGER = Logger.getLogger(PluginConfigDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    InstalledPluginsDAO installedPluginsDAO;
    @Autowired
    PluginConfigDAO pluginConfigDAO;

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        InstalledPlugins installedPlugins = new InstalledPlugins();
        installedPlugins.setPluginUniqueId("test2-Test-plugin");
        installedPlugins.setPluginId("Test");
        installedPlugins.setName("test name");
        installedPlugins.setDescription("Test Description");
        installedPlugins.setPluginVersion("1");
        installedPlugins.setAuthor("Test Author");
        installedPlugins.setAddTs(new Date());
        installedPlugins.setPlugin("TestPlugin");
        installedPlugins.setUninstallable(true);
        String installedPluginId = installedPluginsDAO.insert(installedPlugins);
        LOGGER.info("InstalledPlugin is added with Id:" + installedPluginId);

        PluginConfig pluginConfig = new PluginConfig();
        PluginConfigId pluginConfigId=new PluginConfigId();
        pluginConfigId.setPluginKey("1");
        pluginConfigId.setPluginUniqueId("test2-Test-plugin");
        pluginConfigId.setConfigGroup("Test-config");
        pluginConfigId.setPluginValue("TestPluginValue");
        pluginConfig.setId(pluginConfigId);
        pluginConfig.setInstalledPlugins(installedPlugins);
        PluginConfigId pluginConfigIden = pluginConfigDAO.insert(pluginConfig);
        LOGGER.info("PluginConfig is added with Id:" + pluginConfigIden);
        pluginConfig.getId().setConfigGroup("Config");
        pluginConfigDAO.update(pluginConfig);

        pluginConfig = pluginConfigDAO.get(pluginConfigIden);
        assertEquals("Config",pluginConfigIden.getConfigGroup());
        assertNotNull(pluginConfigDAO.list(0, 10));

        //pluginConfigDAO.delete(pluginConfigIden);
        LOGGER.info("Deleted PluginConfig Entry with ID" + pluginConfigIden);
        LOGGER.info("Size of PluginConfig is:" + pluginConfigDAO.totalRecordCount());
        installedPluginsDAO.delete(installedPluginId);
        LOGGER.info("Deleted InstalledPlugin Entry with ID" + installedPluginId);
        LOGGER.info("Size of installedPlugins is:" + installedPluginsDAO.totalRecordCount());



    }

    @Test
    public void testDistinctPluginUniqueId() throws Exception {
        PluginConfigId pluginConfigId = new PluginConfigId();
        pluginConfigId.setPluginKey("Job Management");
        pluginConfigId.setPluginUniqueId("Test-1");
        PluginConfig pluginConfig=pluginConfigDAO.get(pluginConfigId);
       // List<String> pluginConfigList=pluginConfigDAO.distinctPluginConfig("wf-cont-nodes");
        LOGGER.info(pluginConfig);
        //pluginConfigDAO.getWithConfig("ssd","assd");
        //LOGGER.info(pluginConfigDAO.getConfigForPlugin("DQ-plugin-1.0.0",0,10).size());
        //LOGGER.info("size of jars to add "+pluginConfigDAO.getWithConfig("DQ-plugin-1.0.0","2"+".wf-gen").size());
        //LOGGER.info("size of jars to add "+pluginConfigDAO.getWithConfig("DQ-plugin-1.0.0","2"+".wf-cont-nodes").size());

    }

}
