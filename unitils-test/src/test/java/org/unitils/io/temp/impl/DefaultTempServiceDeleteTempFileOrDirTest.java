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

import static junit.framework.Assert.assertFalse;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempServiceDeleteTempFileOrDirTest {

    /* Tested object */
    private DefaultTempService defaultTempService;

    private File rootTempDir;


    @Before
    public void initialize() throws Exception {
        rootTempDir = new File("target/" + DefaultTempServiceDeleteTempFileOrDirTest.class.getSimpleName());

        defaultTempService = new DefaultTempService(rootTempDir);
        defaultTempService.deleteTempFileOrDir(rootTempDir);
        rootTempDir.mkdirs();
    }


    @Test
    public void deleteTempFile() throws Exception {
        File tempFile = defaultTempService.createTempFile("tempFile.txt");

        defaultTempService.deleteTempFileOrDir(tempFile);
        assertFalse(tempFile.exists());
    }

    @Test
    public void deleteTempDir() throws Exception {
        File tempDir = defaultTempService.createTempDir("tempDir");

        defaultTempService.deleteTempFileOrDir(tempDir);
        assertFalse(tempDir.exists());
    }

    @Test
    public void fileDoesNotExist() throws Exception {
        File nonExistingFile = new File(rootTempDir, "xxxx");

        defaultTempService.deleteTempFileOrDir(nonExistingFile);
        assertFalse(nonExistingFile.exists());
    }

    @Test
    public void nullIsIgnored() throws Exception {
        defaultTempService.deleteTempFileOrDir(null);
    }

    @Test
    public void dirDoesNotExist() throws Exception {
        File nonExistingDir = new File(rootTempDir, "xxxx");

        defaultTempService.deleteTempFileOrDir(nonExistingDir);
        assertFalse(nonExistingDir.exists());
    }

    @Test(expected = UnitilsException.class)
    public void fileInUse() throws Exception {
        File tempFile = defaultTempService.createTempFile("tempFile.tmp");
        FileOutputStream out = new FileOutputStream(tempFile);
        try {
            defaultTempService.deleteTempFileOrDir(tempFile);
        } finally {
            out.close();
        }
    }

    @Test(expected = UnitilsException.class)
    public void dirInUse() throws Exception {
        File tempDir = defaultTempService.createTempDir("tempDir");
        File inUseFile = new File(tempDir, "file.tmp");
        FileOutputStream out = new FileOutputStream(inUseFile);
        try {
            defaultTempService.deleteTempFileOrDir(tempDir);
        } finally {
            out.close();
        }
    }
}
