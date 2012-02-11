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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalInstanceOfTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        configuration.getProperties().setProperty(TestInterface.class.getName(), TestClass.class.getName());

        TestInterface result = configuration.getOptionalInstanceOf(TestInterface.class);
        assertTrue(result instanceof TestClass);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getProperties().setProperty(TestInterface.class.getName(), "xxx");

        configuration.getOptionalInstanceOf(TestInterface.class);
    }

    @Test(expected = UnitilsException.class)
    public void invalidType() {
        configuration.getProperties().setProperty(TestInterface.class.getName(), ArrayList.class.getName());

        configuration.getOptionalInstanceOf(TestInterface.class);
    }

    @Test
    public void valueIsTrimmed() {
        configuration.getProperties().setProperty(TestInterface.class.getName(), "  " + TestClass.class.getName() + "  ");

        TestInterface result = configuration.getOptionalInstanceOf(TestInterface.class);
        assertTrue(result instanceof TestClass);
    }

    @Test
    public void nullWhenNotFound() {
        List result = configuration.getOptionalInstanceOf(List.class);
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        configuration.getProperties().setProperty(TestInterface.class.getName(), "  ");

        TestInterface result = configuration.getOptionalInstanceOf(TestInterface.class);
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        configuration.getProperties().setProperty(TestInterface.class.getName() + ".a.b", TestClass.class.getName());

        TestInterface result = configuration.getOptionalInstanceOf(TestInterface.class, "a", "b");
        assertTrue(result instanceof TestClass);
    }

    @Test
    public void factory() {
        configuration.getProperties().setProperty(Map.class.getName(), FactoryClass.class.getName());

        Map result = configuration.getOptionalInstanceOf(Map.class);
        assertTrue(result instanceof Properties);
    }

    @Test(expected = UnitilsException.class)
    public void factoryReturnsWrongType() {
        configuration.getProperties().setProperty(List.class.getName(), FactoryClass.class.getName());

        configuration.getOptionalInstanceOf(List.class);
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


}
