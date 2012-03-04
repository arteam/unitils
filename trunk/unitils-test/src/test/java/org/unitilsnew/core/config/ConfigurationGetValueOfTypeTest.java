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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitilsnew.core.config.ConfigurationGetValueOfTypeTest.TestEnum.VALUE;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetValueOfTypeTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("string", "value");
        properties.setProperty("boolean", "true");
        properties.setProperty("integer", "5");
        properties.setProperty("long", "5");
        properties.setProperty("enum", "VALUE");
        properties.setProperty("object", TestClass.class.getName());
        properties.setProperty("invalid", "xxx");
        properties.setProperty("empty", "");
        properties.setProperty("propertyWithClassifiers.a.b", "value");
        properties.setProperty("factory", FactoryClass.class.getName());
        configuration = new Configuration(properties);
    }


    @Test
    public void string() {
        String result = configuration.getValueOfType(String.class, "string");
        assertEquals("value", result);
    }

    @Test
    public void booleanSimpleType() {
        boolean result = configuration.getValueOfType(Boolean.class, "boolean");
        assertTrue(result);
    }

    @Test
    public void booleanWrapperType() {
        Boolean result = configuration.getValueOfType(Boolean.class, "boolean");
        assertTrue(result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidBoolean() {
        configuration.getValueOfType(Boolean.class, "invalid");
    }

    @Test
    public void integerSimpleType() {
        int result = configuration.getValueOfType(Integer.class, "integer");
        assertEquals(5, result);
    }

    @Test
    public void integerWrapperType() {
        Integer result = configuration.getValueOfType(Integer.class, "integer");
        assertEquals(new Integer(5), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidInteger() {
        configuration.getValueOfType(Integer.class, "invalid");
    }

    @Test
    public void longSimpleType() {
        long result = configuration.getValueOfType(Long.class, "long");
        assertEquals(5, result);
    }

    @Test
    public void longWrapperType() {
        Long result = configuration.getValueOfType(Long.class, "long");
        assertEquals(new Long(5), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidLong() {
        configuration.getValueOfType(Long.class, "invalid");
    }

    @Test
    public void enumType() {
        TestEnum result = configuration.getValueOfType(TestEnum.class, "enum");
        assertEquals(VALUE, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidEnum() {
        configuration.getValueOfType(TestEnum.class, "invalid");
    }

    @Test
    public void classType() {
        Class result = configuration.getValueOfType(Class.class, "object");
        assertEquals(TestClass.class, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidClassName() {
        configuration.getValueOfType(Class.class, "invalid");
    }

    @Test
    public void objectType() {
        TestInterface result = configuration.getValueOfType(TestInterface.class, "object");
        assertTrue(result instanceof TestClass);
    }

    @Test(expected = UnitilsException.class)
    public void invalidObjectType() {
        configuration.getValueOfType(List.class, "object");
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getValueOfType(Integer.class, "xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getValueOfType(Integer.class, "empty");
    }

    @Test
    public void valueWithClassifiers() {
        String result = configuration.getValueOfType(String.class, "propertyWithClassifiers", "a", "b");
        assertEquals("value", result);
    }

    @Test
    public void factory() {
        Map result = configuration.getValueOfType(Map.class, "factory");
        assertTrue(result instanceof Properties);
    }

    @Test(expected = UnitilsException.class)
    public void factoryReturnsWrongType() {
        configuration.getValueOfType(List.class, "factory");
    }

    private static interface TestInterface {
    }

    private static class TestClass implements TestInterface {
    }

    private static class FactoryClass implements Factory<Map> {

        public Map create() {
            return new Properties();
        }
    }

    public static enum TestEnum {

        VALUE
    }
}

