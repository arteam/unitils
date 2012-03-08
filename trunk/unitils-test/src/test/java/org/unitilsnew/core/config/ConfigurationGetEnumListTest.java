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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitilsnew.core.config.ConfigurationGetEnumListTest.TestEnum.*;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetEnumListTest {

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
        List<TestEnum> result = configuration.getEnumList(TestEnum.class, "enumListProperty");
        assertReflectionEquals(asList(VALUE1, VALUE2, VALUE3), result);
    }

    @Test
    public void valuesAreTrimmed() {
        List<TestEnum> result = configuration.getEnumList(TestEnum.class, "propertyWithSpaces");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<TestEnum> result = configuration.getEnumList(TestEnum.class, "propertyWithEmptyValues");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getEnumList(TestEnum.class, "xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenOnlyEmptyValues() {
        configuration.getEnumList(TestEnum.class, "propertyWithOnlyEmptyValues");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getEnumList(TestEnum.class, "empty");
    }

    @Test
    public void valueWithClassifiers() {
        List<TestEnum> result = configuration.getEnumList(TestEnum.class, "propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList(VALUE1), result);
    }

    public static enum TestEnum {

        VALUE1, VALUE2, VALUE3
    }
}
