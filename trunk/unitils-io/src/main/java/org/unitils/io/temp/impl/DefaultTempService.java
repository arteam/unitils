/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.io.temp.impl;

import org.unitils.core.UnitilsException;
import org.unitils.io.temp.TempService;

import java.io.File;
import java.io.IOException;

import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDelete;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempService implements TempService {

    /* The directory to use as temporary dir */
    protected File rootTempDir;


    public DefaultTempService(File rootTempDir) {
        this.rootTempDir = rootTempDir;
    }


    public File createTempDir(String dirName) {
        File dir = new File(rootTempDir, dirName);
        deleteTempFileOrDir(dir);
        boolean success = dir.mkdirs();
        if (!success) {
            throw new UnitilsException("Unable to create temp dir " + dirName + " in folder " + rootTempDir.getAbsolutePath());
        }
        return dir;
    }

    public File createTempFile(String fileName) {
        try {
            File file = new File(rootTempDir, fileName);
            deleteTempFileOrDir(file);

            rootTempDir.mkdirs();
            file.createNewFile();
            return file;

        } catch (IOException e) {
            throw new UnitilsException("Unable to create temp file " + fileName + " in folder " + rootTempDir.getAbsolutePath(), e);
        }
    }

    public void deleteTempFileOrDir(File fileOrDir) {
        if (fileOrDir == null || !fileOrDir.exists()) {
            return;
        }
        try {
            forceDelete(fileOrDir);

        } catch (IOException e) {
            throw new UnitilsException("Unable to delete temp file/dir " + fileOrDir.getAbsolutePath());
        }
    }
}
