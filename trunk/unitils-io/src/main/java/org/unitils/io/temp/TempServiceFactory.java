/*
 * Copyright 2011,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.unitils.io.temp;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.io.temp.impl.DefaultTempService;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Property;

import java.io.File;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TempServiceFactory implements Factory<TempService> {

    public static final String ROOT_TEMP_DIR = " IOModule.temp.rootTempDir";

    protected String systemTempDirName;

    protected String rootTempDirName;

    public TempServiceFactory(@Property(value = ROOT_TEMP_DIR, optional = true) String rootTempDirName, @Property("java.io.tmpdir") String systemTempDirName)

    {
        this.systemTempDirName = systemTempDirName;
        this.rootTempDirName = rootTempDirName;
    }

    public TempService create() {
        File rootTempDir = getRootTempDir();
        return new DefaultTempService(rootTempDir);
    }

    protected File getRootTempDir() {

        String tempDir;

        if (StringUtils.isEmpty(rootTempDirName)) {
            tempDir = systemTempDirName;
        } else {
            tempDir = rootTempDirName;
        }

        File rootTempDir = new File(tempDir);
        if (rootTempDir.isFile()) {
            throw new UnitilsException("Root temp dir " + rootTempDirName + " is not a directory. Please fill in a directory for property " + ROOT_TEMP_DIR);
        }
        rootTempDir.mkdirs();
        return rootTempDir;
    }
}
