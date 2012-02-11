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
import org.unitilsnew.core.Factory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetInstanceTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("instanceProperty", "java.lang.StringBuffer");
        properties.setProperty("propertyWithSpaces", "    java.lang.StringBuffer   ");
        properties.setProperty("invalidClassNameProperty", "xxx");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "java.lang.StringBuffer");
        properties.setProperty("factory", FactoryClass.class.getName());
        configuration = new Configuration(properties);
    }


    @Test
    public void validValue() {
        StringBuffer result = configuration.getInstance("instanceProperty");
        assertNotNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getInstance("invalidClassNameProperty");
    }

    @Test(expected = ClassCastException.class)
    public void wrongType() {
        List result = configuration.getInstance("instanceProperty");
    }

    @Test
    public void valueIsTrimmed() {
        StringBuffer result = configuration.getInstance("propertyWithSpaces");
        assertNotNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getInstance("xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getInstance("empty");
    }

    @Test
    public void valueWithClassifiers() {
        StringBuffer result = configuration.getInstance("propertyWithClassifiers", "a", "b");
        assertNotNull(result);
    }

    @Test
    public void factory() {
        Map result = configuration.getInstance("factory");
        assertTrue(result instanceof Properties);
    }


    private static class FactoryClass implements Factory<Map> {

        public Map create() {
            return new Properties();
        }
    }
}
