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

import net.urosk.reportEngine.lib.*;
import org.apache.log4j.Logger;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Scope("prototype")
public class BirtReportEngine {

    private final static Logger logger = Logger.getLogger(BirtReportEngine.class);

    @Autowired
    private BirtConfigs birtConfig;

    @Autowired
    private ReportCounter reportCounter;

    @SuppressWarnings("unchecked")
    public String createReport(ReportDef reportDef) throws ReportEngineException {

        IReportRunnable design = null;
        IRunAndRenderTask task;

        String generatedReportFile;

        IReportEngine engine = birtConfig.getReportEngine();

        try {
            design = engine.openReportDesign(birtConfig.getReportDesignHome() + File.separator + reportDef.getDesignFileName());
        } catch (EngineException e) {
            throw new ReportEngineException(e.toString(), e);
        }

        task = engine.createRunAndRenderTask(design);

        IRenderOption options = Utils.getRenderOptions(reportDef.getOutputType());

        String uuid = UUID.randomUUID().toString();

        options.setOutputFileName(birtConfig.getOutputFolder() + File.separator + uuid + "." + options.getOutputFormat());

        task.setRenderOption(options);

        task.setParameterValues(reportDef.getParameters());

        try {

            task.run();

            generatedReportFile = task.getRenderOption().getOutputFileName();

            reportCounter.onReportcreated();

            logger.info("generatedReport: " + generatedReportFile + ", report count created after uptime: " + reportCounter.getReportsCreated());

            if (reportCounter.getReportsCreated() % 100 == 0) {
                try {
                    new FilesDeletingTread(birtConfig.getOutputFolder(), birtConfig.getDeleteTempFilesAfterDays());
                } catch (Exception e) {
                    logger.warn("Deleting thread has problems..", e);
                }

            }

        } catch (EngineException e) {

            logger.error(e, e);
            throw new ReportEngineException(e.toString(), e);

        } finally {

            design = null;
            task.cancel();
            task.close();
            task = null;

        }

        return generatedReportFile;

    }

}
