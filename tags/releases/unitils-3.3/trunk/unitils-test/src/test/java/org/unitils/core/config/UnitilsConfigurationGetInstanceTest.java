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

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetInstanceTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("instanceProperty", "java.lang.StringBuffer");
        properties.setProperty("propertyWithSpaces", "    java.lang.StringBuffer   ");
        properties.setProperty("invalidClassNameProperty", "xxx");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }


    @Test
    public void foundWithoutDefault() {
        StringBuffer result = unitilsConfiguration.getInstance("instanceProperty");
        assertNotNull(result);
    }

    @Test
    public void foundWithDefault() {
        StringBuffer defaultInstance = new StringBuffer();
        StringBuffer result = unitilsConfiguration.getInstance("instanceProperty", defaultInstance);
        assertNotNull(result);
        assertNotSame(defaultInstance, result);
    }

    @Test
    public void trimmedWithoutDefault() {
        StringBuffer result = unitilsConfiguration.getInstance("propertyWithSpaces");
        assertNotNull(result);
    }

    @Test
    public void trimmedWithDefault() {
        StringBuffer result = unitilsConfiguration.getInstance("propertyWithSpaces", new StringBuffer());
        assertNotNull(result);
    }

    @Test
    public void notFoundNoDefault() {
        try {
            unitilsConfiguration.getInstance("xxx");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void notFoundWithDefault() {
        StringBuffer defaultInstance = new StringBuffer();
        StringBuffer result = unitilsConfiguration.getInstance("xxx", defaultInstance);
        assertSame(defaultInstance, result);
    }

    @Test
    public void nullDefault() {
        String result = unitilsConfiguration.getInstance("xxx", null);
        assertNull(result);
    }

    @Test
    public void invalidClassNameWithoutDefault() {
        try {
            unitilsConfiguration.getInstance("invalidClassNameProperty");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void invalidClassNameWithDefault() {
        try {
            unitilsConfiguration.getInstance("invalidClassNameProperty", new StringBuffer());
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void initCalledForConfigurable() {
        Properties properties = unitilsConfiguration.getProperties();
        properties.setProperty(TestInterface.class.getName() + ".implClassName", TestClass.class.getName());

        TestInterface result = unitilsConfiguration.getInstanceOf(TestInterface.class);
        assertSame(properties, ((TestClass) result).configuration);
    }


    public static interface TestInterface {
    }

    public static class TestClass implements TestInterface, Configurable {

        public Properties configuration;

        public void init(Properties configuration) {
            this.configuration = configuration;
        }
    }
}
