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

import junitparams.JUnitParamsRunner;
import net.urosk.reportEngine.lib.OutputType;
import net.urosk.reportEngine.lib.ReportDef;
import net.urosk.reportEngine.lib.ReportEngineException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kristan_uros on 21.5.2015.
 */

@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(locations = {"classpath:reportEngineTest.xml"})
public class ReportEngineTest implements ApplicationContextAware {

    static Logger logger = Logger.getLogger(ReportEngineTest.class);

    ApplicationContext context;

    private TestContextManager testContextManager;

    private
    @Value("${testReport}")
    String testReport;


    @Autowired
    private BirtReportEngine birtReportEngine;

    @Autowired
    private BirtConfigs birtConfigs;


    @Before
    public void setUpSpringContext() throws Exception {


        this.testContextManager = new TestContextManager(getClass());
        this.testContextManager.prepareTestInstance(this);


        if (!new File(birtConfigs.getReportDesignHome() + testReport).exists()) {
            throw new Exception("Test report doesnt exists!! " + birtConfigs.getReportDesignHome() + testReport);
        }

    }

    @After
    public void afterTest() throws Exception {


        logger.info("################ ENDE #############################");
    }

    public void setApplicationContext(ApplicationContext context)
            throws BeansException {

        this.context = context;
    }


    @Test
    public void reportXLSTest() throws ReportEngineException {


        ReportDef reportDef = new ReportDef();
        reportDef.setDesignFileName(testReport);
        reportDef.setOutputType(OutputType.XLS);


        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ds", "razvoj");

        reportDef.setParameters(parameters)
        ;


        String generatedReportFile = birtReportEngine.createReport(reportDef);

        File file = new File(generatedReportFile);


        Assert.assertTrue("Generated report doesnt exists! " + generatedReportFile, file.exists());


        Assert.assertTrue("File size is zero!", file.length() > 0);


    }

    @Test
    public void reportTest() throws ReportEngineException {

        for (int i = 0; i < 5; i++) {

            ReportDef reportDef = new ReportDef();
            reportDef.setDesignFileName(testReport);
            reportDef.setOutputType(OutputType.PDF);


            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("ds", "razvoj");

            reportDef.setParameters(parameters)
            ;


            String generatedReportFile = birtReportEngine.createReport(reportDef);


            File file = new File(generatedReportFile);


            Assert.assertTrue("Generated report doesnt exists! " + generatedReportFile, file.exists());


            Assert.assertTrue("File size is zero!", file.length() > 0);


            file = null;
        }


    }
}
