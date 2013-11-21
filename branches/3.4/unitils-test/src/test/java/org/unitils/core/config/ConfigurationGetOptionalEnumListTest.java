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
import static org.unitils.core.config.ConfigurationGetOptionalEnumListTest.TestEnum.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalEnumListTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("enumListProperty", "VALUE1, VALUE2, VALUE3");
        properties.setProperty("propertyWithSpaces", "   VALUE1  , VALUE2 ");
        properties.setProperty("propertyWithEmptyValues", "VALUE1, , VALUE2, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "VALUE1");
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "enumListProperty");
        assertReflectionEquals(asList(VALUE1, VALUE2, VALUE3), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "propertyWithSpaces");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "propertyWithEmptyValues");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test
    public void emptyWhenNotFound() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        List<TestEnum> result = configuration.getOptionalEnumList(TestEnum.class, "propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(VALUE1), result);
    }

    public static enum TestEnum {

        VALUE1, VALUE2, VALUE3
    }
}
