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

import net.urosk.reportEngine.lib.OutputType;
import net.urosk.reportEngine.lib.ReportDef;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Component("reportsServlet")
public class ReportsServlet implements HttpRequestHandler {

    private static Logger logger = Logger.getLogger(ReportsServlet.class);

    @Autowired
    private BirtReportEngine birtReportEngine;

    private boolean isEmpty(String in) {
        return (in == null || "".equals(in));
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String reportDesign = request.getParameter("__report");
        String type = request.getParameter("__format");
        String outputFilename = request.getParameter("__filename");
        String attachment = request.getParameter("attachment");

        //check parameters
        StringBuffer msg = new StringBuffer();

        // checkers
        if (isEmpty(reportDesign)) {
            msg.append("<BR>__report can not be empty");
        }

        try {
            OutputType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            msg.append("Undefined report __format: " + type + ". Set __format=PDF , __format=XLS , __format=HTML ");
        }

        // checkers
        if (isEmpty(outputFilename)) {
            msg.append("<BR>__filename can not be empty");
        }

        try {

            ServletOutputStream out = response.getOutputStream();
            ServletContext context = request.getSession().getServletContext();

            OutputType outputType = null;

            // output error
            if (msg.toString() != null) {
                out.print(msg.toString());
                return;
            }

            ReportDef def = new ReportDef();
            def.setDesignFileName(reportDesign);
            def.setOutputType(outputType);

            @SuppressWarnings("unchecked")
            Map<String, String[]> params = request.getParameterMap();
            Iterator<String> i = params.keySet().iterator();

            while (i.hasNext()) {
                String key = i.next();
                String value = params.get(key)[0];
                def.getParameters().put(key, value);
            }

            try {

                String createdFile = birtReportEngine.createReport(def);

                File file = new File(createdFile);

                String mimetype = context.getMimeType(file.getAbsolutePath());

                String inlineOrAttachment = (attachment != null) ? "attachment" : "inline";

                response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", inlineOrAttachment + "; filename=\"" + outputFilename + "\"");

                DataInputStream in = new DataInputStream(new FileInputStream(file));

                byte[] bbuf = new byte[1024];
                int length;
                while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                    out.write(bbuf, 0, length);
                }

                in.close();

            } catch (Exception e) {
                out.print(e.getMessage());
                logger.error(e, e);

            } finally {
                out.flush();
                out.close();
            }

            logger.info("Free memory: " + (Runtime.getRuntime().freeMemory() / 1024L * 1024L));

        } catch (Exception e) {

            logger.error(e, e);
        } finally {

        }

    }
}
