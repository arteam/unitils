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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalStringListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("stringListProperty", "test1, test2, test3");
        properties.setProperty("propertyWithSpaces", "   test1  , test2 ");
        properties.setProperty("propertyWithEmptyValues", "test1, , test2, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", "  ");
        properties.setProperty("propertyWithClassifiers.a.b", "value");
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        List<String> result = configuration.getOptionalStringList("stringListProperty");
        assertLenientEquals(asList("test1", "test2", "test3"), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<String> result = configuration.getOptionalStringList("propertyWithSpaces");
        assertLenientEquals(asList("test1", "test2"), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<String> result = configuration.getOptionalStringList("propertyWithEmptyValues");
        assertLenientEquals(asList("test1", "test2"), result);
    }

    @Test
    public void emptyWhenNotFound() {
        List<String> result = configuration.getOptionalStringList("xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        List<String> result = configuration.getOptionalStringList("propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<String> result = configuration.getOptionalStringList("empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        List<String> result = configuration.getOptionalStringList("propertyWithClassifiers", "a", "b");
        assertLenientEquals(asList("value"), result);
    }
}
