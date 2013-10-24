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
package org.unitils.core.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.copyFileToDirectory;

/**
 * @author Fabian Krueger
 * @author Tim Ducheyne
 */
public class PropertiesReaderLoadPropertiesFromUserHomeTest {

    /* Tested object */
    private PropertiesReader propertiesReader = new PropertiesReader();

    private static final String TEST_FILE = "propertiesReaderTest.properties";


    @Before
    public void initialize() throws Exception {
        copyDummyPropertiesFileToUserHome();
    }

    @After
    public void cleanup() {
        deleteDummyPropertiesFileFromUserHome();
    }


    @Test
    public void fileFound() throws Exception {
        Properties result = propertiesReader.loadPropertiesFromUserHome(TEST_FILE);
        assertEquals("some value", result.getProperty("testprop"));
    }

    @Test
    public void nullWhenFileNotFound() {
        Properties result = propertiesReader.loadPropertiesFromUserHome("xxx");
        assertNull(result);
    }

    @Test
    public void unableToLoadFile() {
        try {
            propertiesReader.loadPropertiesFromUserHome(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to load properties from user home with file name null", e.getMessage());
        }
    }


    private void copyDummyPropertiesFileToUserHome() throws Exception {
        String userHome = System.getProperty("user.home");
        URL url = this.getClass().getResource("/" + TEST_FILE);
        copyFileToDirectory(new File(url.toURI()), new File(userHome));
    }

    private void deleteDummyPropertiesFileFromUserHome() {
        String userHome = System.getProperty("user.home");
        File targetFile = new File(userHome + "/" + TEST_FILE);
        targetFile.delete();
    }

}
