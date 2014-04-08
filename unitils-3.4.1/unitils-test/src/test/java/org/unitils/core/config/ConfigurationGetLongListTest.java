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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetLongListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("longListProperty", "1, 2, 3");
        properties.setProperty("propertyWithSpaces", "   1  , 2 ");
        properties.setProperty("propertyWithEmptyValues", "1, , 2, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "1");
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        List<Long> result = configuration.getLongList("longListProperty");
        assertReflectionEquals(asList(1, 2, 3), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<Long> result = configuration.getLongList("propertyWithSpaces");
        assertReflectionEquals(asList(1, 2), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<Long> result = configuration.getLongList("propertyWithEmptyValues");
        assertReflectionEquals(asList(1, 2), result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getLongList("xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenOnlyEmptyValues() {
        configuration.getLongList("propertyWithOnlyEmptyValues");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getLongList("empty");
    }

    @Test
    public void valueWithClassifiers() {
        List<Long> result = configuration.getLongList("propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(1), result);
    }
}
