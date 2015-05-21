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

package net.urosk.reportEngine.lib;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;

/**
 * FilesDeletingThread deletes all files passed with constructor older than x days
 */
public class FilesDeletingTread implements Runnable {

    protected Logger logger = Logger.getLogger(FilesDeletingTread.class);
    private int daysOld;
    private String directory;
    private Thread runner = null;

    public FilesDeletingTread(String directory, int daysOld) {

        this.directory = directory;
        this.daysOld = daysOld;

        runner = new Thread(this);
        runner.start();

    }

    public void run() {

        try {

            if (directory == null || directory.equals(""))
                throw new Exception("Error! directory for deleting not set");

            long now = new Date().getTime();

            final long then = now - 86400000 * daysOld; // 5 days ago.

            File dir = new File(directory);

            String[] children = dir.list();

            for (String s : children) {

                File f = new File(directory + File.separator + s);

                if (f.isFile() && (f.lastModified() < then)) {

                    try {
                        f.delete();
                    } catch (Exception e) {

                    }

                }

            }
            logger.info("Deleted TMP files: " + directory);

        } catch (Exception e) {
            logger.error(e, e);
        }

    }
}
