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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalInstanceOfListTest {

    /* Tested object */
    private Configuration configuration;

    private Properties properties;

    @Before
    public void initialize() throws Exception {
        properties = new Properties();
        configuration = new Configuration(properties);
    }


    @Test
    public void valid() {
        properties.setProperty(TestInterface.class.getName(), TestClass1.class.getName() + ", " + TestClass2.class.getName() + ", " + TestClass3.class.getName());

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class);
        assertEquals(3, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
        assertTrue(result.get(1) instanceof TestClass2);
        assertTrue(result.get(2) instanceof TestClass3);
    }

    @Test
    public void valuesAreTrimmed() {
        properties.setProperty(TestInterface.class.getName(), "   " + TestClass1.class.getName() + "  , " + TestClass2.class.getName() + " ");

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
        assertTrue(result.get(1) instanceof TestClass2);
    }

    @Test
    public void emptyValuesAreIgnored() {
        properties.setProperty(TestInterface.class.getName(), TestClass1.class.getName() + ", , " + TestClass2.class.getName() + ", , ");

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
        assertTrue(result.get(1) instanceof TestClass2);
    }

    @Test
    public void emptyWhenNotFound() {
        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenOnlyEmptyValues() {
        properties.setProperty(TestInterface.class.getName(), ", ,, , ");

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class, "propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenEmpty() {
        properties.setProperty(TestInterface.class.getName(), " ");

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class, "empty");
        assertTrue(result.isEmpty());
    }

    @Test
    public void valueWithClassifiers() {
        properties.setProperty(TestInterface.class.getName() + ".a.b", TestClass1.class.getName());

        List<TestInterface> result = configuration.getOptionalInstanceOfList(TestInterface.class, "a", "b");
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof TestClass1);
    }


    private static interface TestInterface {
    }

    private static class TestClass1 implements TestInterface {
    }

    private static class TestClass2 implements TestInterface {
    }

    private static class TestClass3 implements TestInterface {
    }

    private static class FactoryClass implements Factory<Map> {

        public Map create() {
            return new Properties();
        }
    }
}
