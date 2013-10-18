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

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.unitils.io.IOUnitils.*;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class IOUnitilsDeleteTempFileOrDirTest {


    @Test
    public void deleteFile() {
        File file = createTempFile(IOUnitilsDeleteTempFileOrDirTest.class.getName() + ".tmp");

        deleteTempFileOrDir(file);
        assertFalse(file.exists());
    }

    @Test
    public void deleteDir() {
        File dir = createTempDir(IOUnitilsDeleteTempFileOrDirTest.class.getName());

        deleteTempFileOrDir(dir);
        assertFalse(dir.exists());
    }

    @Test
    public void fileOrDirDoesNotExist() {
        deleteTempFileOrDir(new File("xxx"));
    }

    @Test
    public void ignoreNullFileOrDir() {
        deleteTempFileOrDir(null);
    }
}
