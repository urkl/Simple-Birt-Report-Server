/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *  **************************************************************************
 *  * SBRS  - Simple Birt Report Server
 *  *
 *  *
 *  * @uthors: uros.kristan@gmail.com (Uroš Kristan ) Urosk.NET
 *  *         jernej.svigelj@gmail.com (Jernej Švigelj)
 *
 */

package net.urosk.reportEngine;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;

@Component
public class BirtConfigs {

    private static Logger logger = Logger.getLogger(BirtConfigs.class);
    @Value("${logFolder}")
    String logFolder;
    @Value("${workspaceFolder}")
    String workspaceFolder;
    private
    @Value("${birtEngineHome}")
    String birtEngineHome;
    private
    @Value("${deleteTempFilesAfterDays}")
    int deleteTempFilesAfterDays = 1;
    private
    @Value("${outputFolder}")
    String outputFolder;
    private
    @Value("${reportDesignHome}")
    String reportDesignHome;
    private
    @Value("${resourcesFolder}")
    String resourcesFolder;
    private IReportEngine reportEngine;
    @Value("${fontsConfig}")
    private String fontsConfig;

    public String getFontsConfig() {
        return fontsConfig;
    }

    public void setFontsConfig(String fontsConfig) {
        this.fontsConfig = fontsConfig;
    }

    public String getResourcesFolder() {
        return resourcesFolder;
    }

    public void setResourcesFolder(String resourcesFolder) {
        this.resourcesFolder = resourcesFolder;
    }

    @PreDestroy
    public void destroyEngine() {

        if (reportEngine != null)
            reportEngine.destroy();
    }

    public String getBirtEngineHome() {

        return birtEngineHome;
    }

    public void setBirtEngineHome(String birtEngineHome) {
        this.birtEngineHome = birtEngineHome;
    }

    public int getDeleteTempFilesAfterDays() {
        return deleteTempFilesAfterDays;
    }

    public void setDeleteTempFilesAfterDays(int deleteTempFilesAfterDays) {
        this.deleteTempFilesAfterDays = deleteTempFilesAfterDays;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getReportDesignHome() {
        return reportDesignHome;
    }

    public void setReportDesignHome(String reportDesignHome) {
        this.reportDesignHome = reportDesignHome;
    }

    public IReportEngine getReportEngine() {

        return reportEngine;
    }

    @PostConstruct
    public void init() throws Exception {

        System.setProperty("org.eclipse.datatools_workspacepath", workspaceFolder);

        printOutProperties();

        checkConfigs();

        EngineConfig config = new EngineConfig();

        config.setEngineHome(birtEngineHome);
        config.setResourcePath(resourcesFolder);
        config.setLogConfig(logFolder, Level.WARNING);

        if (StringUtils.isNotEmpty(fontsConfig))
            config.setFontConfig(new URL(fontsConfig));

        try {

            Platform.startup(config);
            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            reportEngine = factory.createReportEngine(config);

        } catch (BirtException e) {
            logger.error(e, e);
            throw e;
        }

    }

    public void printOutProperties() throws Exception {

        final BeanWrapper wrapper = new BeanWrapperImpl(this);
        for (final PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            logger.info(descriptor.getName() + ":" + descriptor.getReadMethod().invoke(this));
        }
    }

    public void checkConfigs() throws Exception {

        if (!new File(workspaceFolder).isDirectory()) {
            String msg = "Error path doesn't exists workspaceFolder: " + workspaceFolder;
            logger.error(msg);
            throw new Exception(msg);
        }

        if (!new File(logFolder).isDirectory()) {
            String msg = "Error path doesn't exists logFolder: " + logFolder;
            logger.error(msg);
            throw new Exception(msg);
        }

        if (!new File(birtEngineHome).isDirectory()) {
            String msg = "Error path doesn't exists birtEngineHome: " + birtEngineHome;
            logger.error(msg);
            throw new Exception(msg);
        }

        if (!new File(outputFolder).exists()) {
            String msg = "Error path doesn't exists outputFolder: " + outputFolder;
            logger.error(msg);
            throw new Exception(msg);
        }
        if (!new File(reportDesignHome).exists()) {
            String msg = "Error path doesn't exists reportDesignHome: " + reportDesignHome;
            logger.error(msg);
            throw new Exception(msg);
        }
        if (!new File(resourcesFolder).exists()) {
            String msg = "Error path doesn't exists resourcesFolder: " + resourcesFolder;
            logger.error(msg);
            throw new Exception(msg);
        }

    }

}

