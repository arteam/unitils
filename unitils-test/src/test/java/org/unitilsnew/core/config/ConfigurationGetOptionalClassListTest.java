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

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalClassListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("classListProperty", Map.class.getName() + ", " + List.class.getName() + ", " + Set.class.getName());
        properties.setProperty("propertyWithSpaces", "   " + Map.class.getName() + "  , " + List.class.getName() + " ");
        properties.setProperty("propertyWithEmptyValues", Map.class.getName() + ", , " + List.class.getName() + ", , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", Map.class.getName());
        configuration = new Configuration(properties);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void valid() {
        List<Class<?>> result = configuration.getOptionalClassList("classListProperty");
        assertReflectionEquals(asList(Map.class, List.class, Set.class), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void valuesAreTrimmed() {
        List<Class<?>> result = configuration.getOptionalClassList("propertyWithSpaces");
        assertReflectionEquals(asList(Map.class, List.class), result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyValuesAreIgnored() {
        List<Class<?>> result = configuration.getOptionalClassList("propertyWithEmptyValues");
        assertReflectionEquals(asList(Map.class, List.class), result);
    }

    @Test
    public void emptyWhenNotFound() {
        List<Class<?>> result = configuration.getOptionalClassList("xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        List<Class<?>> result = configuration.getOptionalClassList("propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<Class<?>> result = configuration.getOptionalClassList("empty");
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void valueWithClassifiers() {
        List<Class<?>> result = configuration.getOptionalClassList("propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(Map.class), result);
    }
}
