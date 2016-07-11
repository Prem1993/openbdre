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
import com.wipro.ats.bdre.md.dao.jpa.PluginDependency;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

import static org.junit.Assert.assertNotNull;



/**
 * Created by sh324337 on 5/27/16.
 */


public class PluginDependencyDAOTest {

    private static final Logger LOGGER = Logger.getLogger(PluginDependencyDAOTest.class);

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
        AutowireCapableBeanFactory acbFactory = context.getAutowireCapableBeanFactory();
        acbFactory.autowireBean(this);
    }

    @Autowired
    InstalledPluginsDAO installedPluginsDAO;
    @Autowired
    PluginDependencyDAO pluginDependencyDAO;

    @Test
    public void testInsertUpdateAndDelete() throws Exception {
        InstalledPlugins installedPlugins = new InstalledPlugins();
        installedPlugins.setPluginUniqueId("test4-Test-plugin");
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

        InstalledPlugins installedPluginsSecond = new InstalledPlugins();
        installedPluginsSecond.setPluginUniqueId("test4-Test-plugin2");
        installedPluginsSecond.setPluginId("Test");
        installedPluginsSecond.setName("test name");
        installedPluginsSecond.setDescription("Test Description");
        installedPluginsSecond.setPluginVersion("2");
        installedPluginsSecond.setAuthor("Test Author");
        installedPluginsSecond.setAddTs(new Date());
        installedPluginsSecond.setPlugin("TestPlugin");
        installedPluginsSecond.setUninstallable(true);
        String installedPluginIdSecond = installedPluginsDAO.insert(installedPluginsSecond);
        LOGGER.info("InstalledPlugin is added with Id:" + installedPluginIdSecond);


        PluginDependency pluginDependency = new PluginDependency();
        pluginDependency.setInstalledPluginsByPluginUniqueId(installedPlugins);
        pluginDependency.setInstalledPluginsByDependentPluginUniqueId(installedPluginsSecond);
        Integer pluginDependencyId = pluginDependencyDAO.insert(pluginDependency);
        LOGGER.info("PluginDependency is added with Id:" + pluginDependencyId);
        pluginDependency = pluginDependencyDAO.get(pluginDependencyId);
        assertNotNull(pluginDependencyId);
        assertNotNull(pluginDependencyDAO.list(0, 10));

        pluginDependencyDAO.delete(pluginDependencyId);
        LOGGER.info("Deleted PluginDependency Entry with ID" + pluginDependencyId);
        LOGGER.info("Size of PluginDependency is:" + pluginDependencyDAO.totalRecordCount());
        installedPluginsDAO.delete(installedPluginId);
        installedPluginsDAO.delete(installedPluginIdSecond);
        LOGGER.info("Deleted InstalledPlugin Entry with ID" + installedPluginId);
        LOGGER.info("Size of installedPlugins is:" + installedPluginsDAO.totalRecordCount());




    }
}