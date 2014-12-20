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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalBooleanTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("trueProperty", "true");
        properties.setProperty("falseProperty", "false");
        properties.setProperty("trueWithSpaces", "   true   ");
        properties.setProperty("invalidBooleanValue", "xxx");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "true");
        configuration = new Configuration(properties);
    }


    @Test
    public void trueValue() {
        Boolean result = configuration.getOptionalBoolean("trueProperty");
        assertTrue(result);
    }

    @Test
    public void falseValue() {
        Boolean result = configuration.getOptionalBoolean("falseProperty");
        assertFalse(result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidBooleanValue() {
        configuration.getOptionalBoolean("invalidBooleanValue");
    }

    @Test
    public void valueIsTrimmed() {
        Boolean result = configuration.getOptionalBoolean("trueWithSpaces");
        assertTrue(result);
    }

    @Test
    public void nullWhenNotFound() {
        Boolean result = configuration.getOptionalBoolean("xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        Boolean result = configuration.getOptionalBoolean("empty");
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        Boolean result = configuration.getOptionalBoolean("propertyWithClassifiers", "a", "b");
        assertTrue(result);
    }
}
