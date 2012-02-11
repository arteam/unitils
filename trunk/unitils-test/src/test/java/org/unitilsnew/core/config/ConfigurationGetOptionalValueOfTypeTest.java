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

import static org.junit.Assert.*;
import static org.unitilsnew.core.config.ConfigurationGetOptionalValueOfTypeTest.TestEnum.VALUE;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalValueOfTypeTest {

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
        configuration = new Configuration(properties);
    }


    @Test
    public void string() {
        String result = configuration.getOptionalValueOfType(String.class, "string");
        assertEquals("value", result);
    }

    @Test
    public void booleanSimpleType() {
        boolean result = configuration.getOptionalValueOfType(Boolean.class, "boolean");
        assertTrue(result);
    }

    @Test
    public void booleanWrapperType() {
        Boolean result = configuration.getOptionalValueOfType(Boolean.class, "boolean");
        assertTrue(result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidBoolean() {
        configuration.getOptionalValueOfType(Boolean.class, "invalid");
    }

    @Test
    public void integerSimpleType() {
        int result = configuration.getOptionalValueOfType(Integer.class, "integer");
        assertEquals(5, result);
    }

    @Test
    public void integerWrapperType() {
        Integer result = configuration.getOptionalValueOfType(Integer.class, "integer");
        assertEquals(new Integer(5), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidInteger() {
        configuration.getOptionalValueOfType(Integer.class, "invalid");
    }

    @Test
    public void longSimpleType() {
        long result = configuration.getOptionalValueOfType(Long.class, "long");
        assertEquals(5, result);
    }

    @Test
    public void longWrapperType() {
        Long result = configuration.getOptionalValueOfType(Long.class, "long");
        assertEquals(new Long(5), result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidLong() {
        configuration.getOptionalValueOfType(Long.class, "invalid");
    }

    @Test
    public void enumType() {
        TestEnum result = configuration.getOptionalValueOfType(TestEnum.class, "enum");
        assertEquals(VALUE, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidEnum() {
        configuration.getOptionalValueOfType(TestEnum.class, "invalid");
    }

    @Test
    public void objectType() {
        TestInterface result = configuration.getOptionalValueOfType(TestInterface.class, "object");
        assertTrue(result instanceof TestClass);
    }

    @Test(expected = UnitilsException.class)
    public void invalidObject() {
        configuration.getOptionalValueOfType(List.class, "object");
    }

    @Test
    public void nullWhenNotFound() {
        Integer result = configuration.getOptionalValueOfType(Integer.class, "xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        Integer result = configuration.getOptionalValueOfType(Integer.class, "empty");
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        String result = configuration.getOptionalValueOfType(String.class, "propertyWithClassifiers", "a", "b");
        assertEquals("value", result);
    }


    private static interface TestInterface {
    }

    private static class TestClass implements TestInterface {
    }

    public static enum TestEnum {

        VALUE
    }
}

