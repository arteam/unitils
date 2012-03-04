/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core.config;

import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Fabian Krueger
 * @author Tim Ducheyne
 */
public class PropertiesReaderLoadAllPropertiesFromClasspathTest {

    /* Tested object */
    private PropertiesReader propertiesReader = new PropertiesReader();

    private static final String TEST_FILE = "propertiesReaderTest.properties";


    @Test
    public void fileFound() throws Exception {
        List<Properties> result = propertiesReader.loadAllPropertiesFromClasspath(TEST_FILE);
        assertEquals(1, result.size());
        assertEquals("some value", result.get(0).getProperty("testprop"));
    }

    @Test
    public void emptyWhenNoFileFound() {
        List<Properties> result = propertiesReader.loadAllPropertiesFromClasspath("xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void unableToLoadFile() {
        try {
            propertiesReader.loadAllPropertiesFromClasspath(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to load properties from classpath with file name null", e.getMessage());
        }
    }
}
