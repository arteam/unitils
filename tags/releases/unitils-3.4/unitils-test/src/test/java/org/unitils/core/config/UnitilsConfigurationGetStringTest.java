/*
 * Copyright 2011, Unitils.org
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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetStringTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("stringProperty", "stringValue");
        properties.setProperty("propertyWithSpaces", "    value   ");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }


    @Test
    public void foundWithoutDefault() {
        String result = unitilsConfiguration.getString("stringProperty");
        assertEquals("stringValue", result);
    }

    @Test
    public void foundWithDefault() {
        String result = unitilsConfiguration.getString("stringProperty", "defaultValue");
        assertEquals("stringValue", result);
    }

    @Test
    public void trimmedWithoutDefault() {
        String result = unitilsConfiguration.getString("propertyWithSpaces");
        assertEquals("value", result);
    }

    @Test
    public void trimmedWithDefault() {
        String result = unitilsConfiguration.getString("propertyWithSpaces", "defaultValue");
        assertEquals("value", result);
    }

    @Test
    public void notFoundNoDefault() {
        try {
            unitilsConfiguration.getString("xxx");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void notFoundWithDefault() {
        String result = unitilsConfiguration.getString("xxx", "defaultValue");
        assertEquals("defaultValue", result);
    }

    @Test
    public void nullDefault() {
        String result = unitilsConfiguration.getString("xxx", null);
        assertNull(result);
    }

    @Test
    public void defaultValueNotTrimmed() {
        String result = unitilsConfiguration.getString("xxx", "   value   ");
        assertEquals("   value   ", result);
    }
}
