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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitilsnew.core.config.ConfigurationGetOptionalValueListOfTypeTest.TestEnum.VALUE1;
import static org.unitilsnew.core.config.ConfigurationGetOptionalValueListOfTypeTest.TestEnum.VALUE2;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalValueListOfTypeTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("strings", "value1, value2");
        properties.setProperty("booleans", "true, false");
        properties.setProperty("integers", "5, 6");
        properties.setProperty("longs", "5, 6");
        properties.setProperty("enums", "VALUE1, VALUE2");
        properties.setProperty("objects", TestClass1.class.getName() + ", " + TestClass2.class.getName());
        properties.setProperty("invalid", "xxx");
        properties.setProperty("empty", "");
        properties.setProperty("propertyWithClassifiers.a.b", "value");
        configuration = new Configuration(properties);
    }


    @Test
    public void string() {
        List<String> result = configuration.getOptionalValueListOfType(String.class, "strings");
        assertReflectionEquals(asList("value1", "value2"), result);
    }

    @Test
    public void booleans() {
        List<Boolean> result = configuration.getOptionalValueListOfType(Boolean.class, "booleans");
        assertReflectionEquals(asList(true, false), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidBoolean() {
        configuration.getOptionalValueListOfType(Boolean.class, "invalid");
    }

    @Test
    public void integers() {
        List<Integer> result = configuration.getOptionalValueListOfType(Integer.class, "integers");
        assertReflectionEquals(asList(5, 6), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidInteger() {
        configuration.getOptionalValueListOfType(Integer.class, "invalid");
    }

    @Test
    public void longs() {
        List<Long> result = configuration.getOptionalValueListOfType(Long.class, "longs");
        assertReflectionEquals(asList(5, 6), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidLong() {
        configuration.getOptionalValueListOfType(Long.class, "invalid");
    }

    @Test
    public void enums() {
        List<TestEnum> result = configuration.getOptionalValueListOfType(TestEnum.class, "enums");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidEnum() {
        configuration.getOptionalValueListOfType(TestEnum.class, "invalid");
    }

    @Test
    public void objectTypes() {
        List<TestInterface> result = configuration.getOptionalValueListOfType(TestInterface.class, "objects");
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
        assertTrue(result.get(1) instanceof TestClass2);
    }

    @Test(expected = UnitilsException.class)
    public void invalidObject() {
        configuration.getOptionalValueListOfType(List.class, "objects");
    }

    @Test
    public void emptyWhenNotFound() {
        List<Integer> result = configuration.getOptionalValueListOfType(Integer.class, "xxx");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        List<Integer> result = configuration.getOptionalValueListOfType(Integer.class, "empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        List<String> result = configuration.getOptionalValueListOfType(String.class, "propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList("value"), result);
    }


    private static interface TestInterface {
    }

    private static class TestClass1 implements TestInterface {
    }

    private static class TestClass2 implements TestInterface {
    }

    public static enum TestEnum {

        VALUE1, VALUE2
    }
}

