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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalBooleanListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("booleanListProperty", "true, false, true");
        properties.setProperty("propertyWithSpaces", "   true  , false ");
        properties.setProperty("propertyWithEmptyValues", "true, , false, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", "  ");
        properties.setProperty("propertyWithClassifiers.a.b", "true");
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        List<Boolean> result = configuration.getOptionalBooleanList("booleanListProperty");
        assertReflectionEquals(asList(true, false, true), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<Boolean> result = configuration.getOptionalBooleanList("propertyWithSpaces");
        assertReflectionEquals(asList(true, false), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<Boolean> result = configuration.getOptionalBooleanList("propertyWithEmptyValues");
        assertReflectionEquals(asList(true, false), result);
    }

    @Test
    public void emptyWhenNotFound() {
        List<Boolean> result = configuration.getOptionalBooleanList("xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        List<Boolean> result = configuration.getOptionalBooleanList("propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<Boolean> result = configuration.getOptionalBooleanList("empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        List<Boolean> result = configuration.getOptionalBooleanList("propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(true), result);
    }
}
