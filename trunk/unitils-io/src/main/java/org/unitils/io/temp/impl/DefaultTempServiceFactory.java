/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.unitils.io.temp.impl;

import org.unitils.core.UnitilsException;
import org.unitils.io.temp.TempService;
import org.unitils.io.temp.TempServiceFactory;

import java.io.File;
import java.util.Properties;

import static org.unitils.util.PropertyUtils.getString;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempServiceFactory implements TempServiceFactory {

    public static final String ROOT_TEMP_DIR = " IOModule.temp.rootTempDir";


    public TempService createTempService(Properties configuration) {
        File rootTempDir = getRootTempDir(configuration);
        return new DefaultTempService(rootTempDir);
    }


    protected File getRootTempDir(Properties configuration) {
        String systemTempDirName = System.getProperty("java.io.tmpdir");
        String rootTempDirName = getString(ROOT_TEMP_DIR, systemTempDirName, configuration);

        File rootTempDir = new File(rootTempDirName);
        if (rootTempDir.isFile()) {
            throw new UnitilsException("Root temp dir " + rootTempDirName + " is not a directory. Please fill in a directory for property " + ROOT_TEMP_DIR);
        }
        rootTempDir.mkdirs();
        return rootTempDir;
    }
}
