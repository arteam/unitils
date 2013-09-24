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
package org.unitils.io.temp;


import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TempServiceFactoryTest {

    public static final String DIR_ONE = "target/test-classes/TempserviceFactoryTest/ONE";
    public static final String DIR_TWO = "target/test-classes/TempserviceFactoryTest/TWO";


    @Before
    public void setUp() {
        new File(DIR_ONE).delete();
        new File(DIR_TWO).delete();
    }


    @Test
    public void createWithSysDir() {
        TempServiceFactory factory = new TempServiceFactory(null, DIR_ONE);
        TempService result = factory.create();

        assertNotNull(result);
        assertTrue(new File(DIR_ONE).exists());
    }

    @Test
    public void createWithSysDirAndEmpty() {
        TempServiceFactory factory = new TempServiceFactory("", DIR_ONE);
        TempService result = factory.create();

        assertNotNull(result);
        assertTrue(new File(DIR_ONE).exists());
    }

    @Test
    public void createWithRootDir() {
        Properties properties = new Properties();

        TempServiceFactory factory = new TempServiceFactory(DIR_TWO, DIR_ONE);
        TempService result = factory.create();

        assertNotNull(result);
        assertTrue(new File(DIR_TWO).exists());
        assertFalse(new File(DIR_ONE).exists());
    }

    @Test
    public void createWithExistingRootDir() {
        new File(DIR_TWO).mkdirs();

        TempServiceFactory factory = new TempServiceFactory(DIR_TWO, DIR_ONE);
        TempService result = factory.create();

        assertNotNull(result);
        assertTrue(new File(DIR_TWO).exists());
    }

    @Test(expected = UnitilsException.class)
    public void createWithFileAsRootDir() throws IOException {

        FileUtils.writeStringToFile(new File(DIR_TWO), "empty");
        TempServiceFactory factory = new TempServiceFactory(DIR_TWO, DIR_ONE);
        TempService result = factory.create();

        assertNotNull(result);
        assertTrue(new File(DIR_TWO).exists());
        assertFalse(new File(DIR_ONE).exists());
    }
}
