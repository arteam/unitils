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


import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.unitils.util.FileUtils.writeStringToFile;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempServiceCreateTempDirTest {

    /* Tested object */
    private DefaultTempService defaultTempService;

    private File rootTempDir;


    @Before
    public void initialize() throws IOException {
        rootTempDir = new File("target/" + DefaultTempServiceCreateTempDirTest.class.getSimpleName());

        defaultTempService = new DefaultTempService(rootTempDir);
        defaultTempService.deleteTempFileOrDir(rootTempDir);
        rootTempDir.mkdirs();
    }


    @Test
    public void createTempDir() {
        File result = defaultTempService.createTempDir("tempDir");
        File expected = new File(rootTempDir, "tempDir");

        assertEquals(expected, result);
        assertTrue(result.exists());
        assertTrue(result.isDirectory());
    }

    @Test
    public void rootTempDirIsCreatedWhenItDoesNotExist() {
        rootTempDir.delete();

        File result = defaultTempService.createTempDir("tempDir");
        assertTrue(result.exists());
    }

    @Test
    public void directoryIsDeletedIfItAlreadyExists() throws Exception {
        File existingDir = defaultTempService.createTempDir("tempDir");
        File existingFile = new File(existingDir, "file.tmp");
        writeStringToFile(existingFile, "test");

        File result = defaultTempService.createTempDir("tempDir");
        assertTrue(result.exists());
        assertEquals(0, result.listFiles().length);
    }

    @Test(expected = UnitilsException.class)
    public void invalidDirName() {
        defaultTempService.createTempDir("x::://\\^@,.?@#");
    }

    @Test(expected = UnitilsException.class)
    public void existingDirInUse() throws Exception {
        File existingDir = defaultTempService.createTempDir("tempDir");
        File existingFile = new File(existingDir, "file.tmp");
        FileOutputStream out = new FileOutputStream(existingFile);
        try {
            defaultTempService.createTempDir("tempDir");
        } finally {
            out.close();
        }
    }

}
