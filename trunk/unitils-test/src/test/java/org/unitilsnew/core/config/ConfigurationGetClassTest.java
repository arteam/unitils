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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetClassTest {

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
        Class<?> result = configuration.getClass("classProperty");
        assertEquals(Map.class, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getClass("invalidLongValue");
    }

    @Test
    public void valueIsTrimmed() {
        Class<?> result = configuration.getClass("valueWithSpaces");
        assertEquals(Map.class, result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getClass("xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getClass("empty");
    }

    @Test
    public void valueWithClassifiers() {
        Class<?> result = configuration.getClass("propertyWithClassifiers", "a", "b");
        assertEquals(Map.class, result);
    }
}
