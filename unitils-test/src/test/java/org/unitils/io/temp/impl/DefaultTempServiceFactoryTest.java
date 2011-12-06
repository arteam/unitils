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
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.unitils.io.temp.impl.DefaultTempServiceFactory.ROOT_TEMP_DIR;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempServiceFactoryTest {

    /* Tested object */
    private DefaultTempServiceFactory defaultTempServiceFactory = new DefaultTempServiceFactory();

    private Properties properties;
    private String testFileName;


    @Before
    public void initialize() {
        properties = new Properties();

        testFileName = "target/" + DefaultTempServiceFactoryTest.class.getSimpleName() + "-file.tmp";
        File testFile = new File(testFileName);
        testFile.delete();
    }


    @Test
    public void useSystemTempFolderWhenNoRootTempDirSpecified() {
        properties.setProperty(ROOT_TEMP_DIR, "");
        DefaultTempService defaultTempService = (DefaultTempService) defaultTempServiceFactory.createTempService(properties);

        assertEquals(new File(System.getProperty("java.io.tmpdir")), defaultTempService.rootTempDir);
    }

    @Test
    public void specifiedRootTempDir() {
        properties.setProperty(ROOT_TEMP_DIR, "target/" + DefaultTempServiceFactoryTest.class.getSimpleName());
        DefaultTempService defaultTempService = (DefaultTempService) defaultTempServiceFactory.createTempService(properties);

        assertEquals(new File("target/" + DefaultTempServiceFactoryTest.class.getSimpleName()), defaultTempService.rootTempDir);
    }

    @Test(expected = UnitilsException.class)
    public void specifiedRootTempDirIsNotADirectory() throws Exception {
        File testFile = new File(testFileName);
        testFile.createNewFile();

        properties.setProperty(ROOT_TEMP_DIR, testFileName);
        defaultTempServiceFactory.createTempService(properties);
    }
}
