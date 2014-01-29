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

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalClassTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("classProperty", Map.class.getName());
        properties.setProperty("valueWithSpaces", "  " + Map.class.getName() + "  ");
        properties.setProperty("invalidClassValue", "xxx");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", Map.class.getName());
        configuration = new Configuration(properties);
    }


    @Test
    public void validValue() {
        Class<?> result = configuration.getOptionalClass("classProperty");
        assertEquals(Map.class, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getOptionalClass("invalidClassValue");
    }

    @Test
    public void valueIsTrimmed() {
        Class<?> result = configuration.getOptionalClass("valueWithSpaces");
        assertEquals(Map.class, result);
    }

    @Test
    public void nullWhenNotFound() {
        Class<?> result = configuration.getOptionalClass("xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        Class<?> result = configuration.getOptionalClass("empty");
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        Class<?> result = configuration.getOptionalClass("propertyWithClassifiers", "a", "b");
        assertEquals(Map.class, result);
    }
}
