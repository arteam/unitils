/*
 * Copyright 2010,  Unitils.org
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

package org.unitils.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.io.IOUnitils.createTempDir;
import static org.unitils.util.FileUtils.writeStringToFile;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class IOUnitilsCreateTempDirTest {

    private String dirName;

    @Before
    public void initialize() {
        dirName = IOUnitilsCreateTempDirTest.class.getName();
        File result = new File(System.getProperty("java.io.tmpdir"), dirName);
        result.delete();
    }

    @Test
    public void newTempDir() {
        File result = createTempDir(dirName);

        assertTrue(result.isDirectory());
        assertEquals(dirName, result.getName());
    }

    @Test
    public void dirAlreadyExists() throws Exception {
        File dir = createTempDir(dirName);
        File file = new File(dir, "file.tmp");
        writeStringToFile(file, "test");

        File result = createTempDir(dirName);

        assertEquals(0, result.list().length);
    }

}
