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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetIntTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("intProperty", "5");
        properties.setProperty("valueWithSpaces", "  5  ");
        properties.setProperty("invalidIntValue", "xxx");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }


    @Test
    public void foundWithoutDefault() {
        int result = unitilsConfiguration.getInt("intProperty");
        assertEquals(5, result);
    }

    @Test
    public void foundWithDefault() {
        int result = unitilsConfiguration.getInt("intProperty", 6);
        assertEquals(5, result);
    }

    @Test
    public void valueIsTrimmedWithoutDefault() {
        int result = unitilsConfiguration.getInt("valueWithSpaces");
        assertEquals(5, result);
    }

    @Test
    public void valueIsTrimmedWithDefault() {
        int result = unitilsConfiguration.getInt("valueWithSpaces", 6);
        assertEquals(5, result);
    }

    @Test
    public void notFoundNoDefault() {
        try {
            unitilsConfiguration.getInt("xxx");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void notFoundWithDefault() {
        int result = unitilsConfiguration.getInt("xxx", 6);
        assertEquals(6, result);
    }

    @Test
    public void invalidIntValueWithoutDefault() {
        try {
            unitilsConfiguration.getInt("invalidIntValue");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void invalidIntValueWithDefault() {
        try {
            unitilsConfiguration.getInt("invalidIntValue", 6);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }
}
