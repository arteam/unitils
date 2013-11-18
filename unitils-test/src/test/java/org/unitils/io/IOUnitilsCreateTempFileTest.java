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
package org.unitils.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.io.IOUnitils.createTempFile;
import static org.unitils.util.FileUtils.writeStringToFile;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class IOUnitilsCreateTempFileTest {

    private String fileName;

    @Before
    public void initialize() {
        fileName = IOUnitilsCreateTempFileTest.class.getName() + ".tmp";
        File result = new File(System.getProperty("java.io.tmpdir"), fileName);
        result.delete();
    }

    @Test
    public void newTempFile() {
        File result = createTempFile(fileName);

        assertTrue(result.isFile());
        assertEquals(fileName, result.getName());
    }

    @Test
    public void fileAlreadyExists() throws Exception {
        File file = createTempFile(fileName);
        writeStringToFile(file, "test");

        File result = createTempFile(fileName);

        assertEquals(0, result.length());
    }

    @Test
    public void constructionForCoverage() {
        new IOUnitils();
    }
}
