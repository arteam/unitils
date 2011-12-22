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
public class UnitilsConfigurationGetBooleanTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("trueProperty", "true");
        properties.setProperty("falseProperty", "false");
        properties.setProperty("trueWithSpaces", "   true   ");
        properties.setProperty("invalidBooleanValue", "xxx");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }


    @Test
    public void trueWithoutDefault() {
        boolean result = unitilsConfiguration.getBoolean("trueProperty");
        assertTrue(result);
    }

    @Test
    public void falseWithoutDefault() {
        boolean result = unitilsConfiguration.getBoolean("falseProperty");
        assertFalse(result);
    }

    @Test
    public void trueWithDefault() {
        boolean result = unitilsConfiguration.getBoolean("trueProperty", false);
        assertTrue(result);
    }

    @Test
    public void falseWithDefault() {
        boolean result = unitilsConfiguration.getBoolean("falseProperty", true);
        assertFalse(result);
    }

    @Test
    public void trimmedWithoutDefault() {
        boolean result = unitilsConfiguration.getBoolean("trueWithSpaces");
        assertTrue(result);
    }

    @Test
    public void trimmedWithDefault() {
        boolean result = unitilsConfiguration.getBoolean("trueWithSpaces", false);
        assertTrue(result);
    }

    @Test
    public void notFoundNoDefault() {
        try {
            unitilsConfiguration.getBoolean("xxx");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void notFoundWithDefault() {
        boolean result = unitilsConfiguration.getBoolean("xxx", true);
        assertTrue(result);
    }

    @Test
    public void invalidBooleanValue() {
        try {
            unitilsConfiguration.getBoolean("invalidBooleanValue");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }
}
