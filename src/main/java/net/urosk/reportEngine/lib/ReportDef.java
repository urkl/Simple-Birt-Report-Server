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

import java.util.HashMap;
import java.util.Map;


public class ReportDef {

    private String designFileName;

    private OutputType outputType;

    private Map<String, Object> parameters = new HashMap<String, Object>();

    public String getDesignFileName() {
        return designFileName;
    }

    public void setDesignFileName(String designFileName) {
        this.designFileName = designFileName;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

}
