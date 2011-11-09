/*
 * Copyright 2011,  Unitils.org
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

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.unitils.io.IOUnitils.readFileContent;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class IOUnitilsTest {


    @Test
    public void defaultFileNameWithStringContent() {
        String stringContent = readFileContent(String.class, this);
        assertEquals("testFile", stringContent);
    }

    @Test
    public void defaultFileNameWithPropertiesContent() {
        Properties propertiesContent = readFileContent(Properties.class, this);
        assertEquals("testFile", propertiesContent.getProperty("testFile"));
    }

    @Test
    public void defaultFileNameWithEncoding() {
        Properties propertiesContent = readFileContent(Properties.class, "UTF8", this);
        assertEquals("testFile", propertiesContent.getProperty("testFile"));
    }

    @Test
    public void specifiedFileName() {
        Properties propertiesContent = readFileContent("IOUnitilsTest.properties", Properties.class, this);
        assertEquals("testFile", propertiesContent.getProperty("testFile"));
    }

    @Test
    public void specifiedFileNameWithEncoding() {
        Properties propertiesContent = readFileContent("IOUnitilsTest.properties", Properties.class, "UTF8", this);
        assertEquals("testFile", propertiesContent.getProperty("testFile"));
    }

    @Test
    public void propertiesAsString() {
        String stringContent = readFileContent("IOUnitilsTest.properties", String.class, this);
        assertEquals("testFile=testFile", stringContent);
    }

}
