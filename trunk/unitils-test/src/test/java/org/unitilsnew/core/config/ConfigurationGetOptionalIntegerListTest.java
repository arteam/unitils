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
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalIntegerListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("integerListProperty", "1, 2, 3");
        properties.setProperty("propertyWithSpaces", "   1  , 2 ");
        properties.setProperty("propertyWithEmptyValues", "1, , 2, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", "  ");
        properties.setProperty("propertyWithClassifiers.a.b", "1");
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        List<Integer> result = configuration.getOptionalIntegerList("integerListProperty");
        assertReflectionEquals(asList(1, 2, 3), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<Integer> result = configuration.getOptionalIntegerList("propertyWithSpaces");
        assertReflectionEquals(asList(1, 2), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<Integer> result = configuration.getOptionalIntegerList("propertyWithEmptyValues");
        assertReflectionEquals(asList(1, 2), result);
    }

    @Test
    public void emptyWhenNotFound() {
        List<Integer> result = configuration.getOptionalIntegerList("xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        List<Integer> result = configuration.getOptionalIntegerList("propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<Integer> result = configuration.getOptionalIntegerList("empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        List<Integer> result = configuration.getOptionalIntegerList("propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(1), result);
    }
}
