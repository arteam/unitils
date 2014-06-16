/*
 * Copyright 2011, Unitils.org
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
import org.unitils.core.util.Configurable;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetInstanceOfTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;

    private Properties properties;

    @Before
    public void initialize() throws Exception {
        properties = new Properties();
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }

    @Test
    public void foundWithoutDiscriminators() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassA.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class);
        assertTrue(result instanceof TestClassA);
    }

    @Test
    public void twoLevelDiscriminator() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassA.class.getName());
        properties.setProperty(TestInterface.class.getName() + ".implClassName.level1", TestClassB.class.getName());
        properties.setProperty(TestInterface.class.getName() + ".implClassName.level1.level2", TestClassC.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class, "level1", "level2");
        System.out.println("result = " + result);
        assertTrue(result instanceof TestClassC);
    }

    @Test
    public void oneLevelDiscriminator() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassA.class.getName());
        properties.setProperty(TestInterface.class.getName() + ".implClassName.level1", TestClassB.class.getName());
        properties.setProperty(TestInterface.class.getName() + ".implClassName.level1.level2", TestClassC.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class, "level1");
        assertTrue(result instanceof TestClassB);
    }

    @Test
    public void noValueForLevel2DiscriminatorFound() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassA.class.getName());
        properties.setProperty(TestInterface.class.getName() + ".implClassName.level1", TestClassB.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class, "level1", "level2");
        assertTrue(result instanceof TestClassB);
    }

    @Test
    public void noValuesForDiscriminatorsFound() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassA.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class, "level1", "level2");
        assertTrue(result instanceof TestClassA);
    }

    @Test
    public void notFound() {
        try {
            unitilsConfiguration.getInstanceOf(List.class);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void initCalledForConfigurable() {
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClassD.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class);
        assertSame(properties, ((TestClassD) result).configuration);
    }


    public static interface TestInterface {
    }

    public static class TestClassA implements TestInterface {
    }

    public static class TestClassB implements TestInterface {
    }

    public static class TestClassC implements TestInterface {
    }

    public static class TestClassD implements TestInterface, Configurable {

        public Properties configuration;

        public void init(Properties configuration) {
            this.configuration = configuration;
        }
    }
}
