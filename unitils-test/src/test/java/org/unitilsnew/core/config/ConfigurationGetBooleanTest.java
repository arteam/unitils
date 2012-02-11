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

package org.unitilsnew.core.config;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetBooleanTest {

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
        Boolean result = configuration.getBoolean("trueProperty");
        assertTrue(result);
    }

    @Test
    public void falseValue() {
        Boolean result = configuration.getBoolean("falseProperty");
        assertFalse(result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getBoolean("invalidBooleanValue");
    }

    @Test
    public void valueIsTrimmed() {
        Boolean result = configuration.getBoolean("trueWithSpaces");
        assertTrue(result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getBoolean("xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getBoolean("empty");
    }

    @Test
    public void valueWithClassifiers() {
        Boolean result = configuration.getBoolean("propertyWithClassifiers", "a", "b");
        assertTrue(result);
    }
}
