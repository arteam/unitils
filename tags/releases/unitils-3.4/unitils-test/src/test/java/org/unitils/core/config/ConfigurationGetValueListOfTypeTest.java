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
import org.unitils.core.Factory;
import org.unitils.core.UnitilsException;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.config.ConfigurationGetValueListOfTypeTest.TestEnum.VALUE1;
import static org.unitils.core.config.ConfigurationGetValueListOfTypeTest.TestEnum.VALUE2;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetValueListOfTypeTest {

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
        properties.setProperty("factory", FactoryClass.class.getName());
        configuration = new Configuration(properties);
    }


    @Test
    public void string() {
        List<String> result = configuration.getValueListOfType(String.class, "strings");
        assertReflectionEquals(asList("value1", "value2"), result);
    }

    @Test
    public void booleans() {
        List<Boolean> result = configuration.getValueListOfType(Boolean.class, "booleans");
        assertReflectionEquals(asList(true, false), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidBoolean() {
        configuration.getValueListOfType(Boolean.class, "invalid");
    }

    @Test
    public void integers() {
        List<Integer> result = configuration.getValueListOfType(Integer.class, "integers");
        assertReflectionEquals(asList(5, 6), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidInteger() {
        configuration.getValueListOfType(Integer.class, "invalid");
    }

    @Test
    public void longs() {
        List<Long> result = configuration.getValueListOfType(Long.class, "longs");
        assertReflectionEquals(asList(5, 6), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidLong() {
        configuration.getValueListOfType(Long.class, "invalid");
    }

    @Test
    public void enums() {
        List<TestEnum> result = configuration.getValueListOfType(TestEnum.class, "enums");
        assertReflectionEquals(asList(VALUE1, VALUE2), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidEnum() {
        configuration.getValueListOfType(TestEnum.class, "invalid");
    }

    @Test
    public void classTypes() {
        List<Class> result = configuration.getValueListOfType(Class.class, "objects");
        assertEquals(2, result.size());
        assertEquals(TestClass1.class, result.get(0));
        assertEquals(TestClass2.class, result.get(1));
    }

    @Test(expected = UnitilsException.class)
    public void invalidClassName() {
        configuration.getValueListOfType(Class.class, "invalid");
    }

    @Test
    public void objectTypes() {
        List<TestInterface> result = configuration.getValueListOfType(TestInterface.class, "objects");
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
        assertTrue(result.get(1) instanceof TestClass2);
    }

    @Test(expected = UnitilsException.class)
    public void invalidObjectType() {
        configuration.getValueListOfType(List.class, "objects");
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getValueListOfType(Integer.class, "xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getValueListOfType(Integer.class, "empty");
    }

    @Test
    public void valueWithClassifiers() {
        List<String> result = configuration.getValueListOfType(String.class, "propertyWithClassifiers", "a", "b");
        assertReflectionEquals(asList("value"), result);
    }

    @Test
    public void factory() {
        List<Map> result = configuration.getValueListOfType(Map.class, "factory");
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof Properties);
    }

    @Test(expected = UnitilsException.class)
    public void factoryReturnsWrongType() {
        configuration.getValueListOfType(List.class, "factory");
    }


    private static interface TestInterface {
    }

    private static class TestClass1 implements TestInterface {
    }

    private static class TestClass2 implements TestInterface {
    }

    private static class FactoryClass implements Factory<Map> {

        public Map create() {
            return new Properties();
        }
    }

    public static enum TestEnum {

        VALUE1, VALUE2
    }
}

