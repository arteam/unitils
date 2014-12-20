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

package org.unitils.core.context;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.core.config.Configuration;
import org.unitils.core.config.PropertiesReader;
import org.unitils.mock.Mock;

import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.context.UnitilsContextFactory.LISTENERS_PROPERTY;
import static org.unitils.core.context.UnitilsContextFactory.MODULE_PROPERTIES_NAME;

/**
 * @author Tim Ducheyne
 */
public class UnitilsContextFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsContextFactory unitilsContextFactory;

    private Mock<PropertiesReader> propertiesReader;

    private Properties userProperties = new Properties();
    private Properties moduleProperties1 = new Properties();
    private Properties moduleProperties2 = new Properties();


    @Before
    public void initialize() {
        unitilsContextFactory = new UnitilsContextFactory(userProperties, propertiesReader.getMock());

        propertiesReader.returnsAll(moduleProperties1, moduleProperties2).loadAllPropertiesFromClasspath(MODULE_PROPERTIES_NAME);

        moduleProperties1.put("module1", "value1");
        moduleProperties2.put("module2", "value2");
        moduleProperties1.put("property", "module-value");
        userProperties.put("property", "user-value");
    }


    @Test
    public void allModulePropertiesAreLoadedAndMerged() {
        UnitilsContext result = unitilsContextFactory.create();

        Configuration configuration = result.getConfiguration();
        assertEquals("value1", configuration.getOptionalString("module1"));
        assertEquals("value2", configuration.getOptionalString("module2"));
    }

    @Test
    public void userPropertiesOverrideModuleProperties() {
        UnitilsContext result = unitilsContextFactory.create();

        Configuration configuration = result.getConfiguration();
        assertEquals("user-value", configuration.getOptionalString("property"));
    }

    @Test
    public void noModulesFound() {
        propertiesReader.onceReturnsAll().loadAllPropertiesFromClasspath(MODULE_PROPERTIES_NAME);

        UnitilsContext result = unitilsContextFactory.create();

        assertEquals("user-value", result.getConfiguration().getOptionalString("property"));
    }

    @Test(expected = UnitilsException.class)
    public void modulePropertiesCouldNotBeLoaded() {
        propertiesReader.onceRaises(UnitilsException.class).loadAllPropertiesFromClasspath(MODULE_PROPERTIES_NAME);
        unitilsContextFactory.create();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testListenerTypesAreLoadedForAllModules() {
        moduleProperties1.put(LISTENERS_PROPERTY, MyTestListener1.class.getName() + "," + MyTestListener2.class.getName());
        moduleProperties2.put(LISTENERS_PROPERTY, MyTestListener3.class.getName());

        UnitilsContext result = unitilsContextFactory.create();

        assertEquals(asList(MyTestListener1.class, MyTestListener2.class, MyTestListener3.class), result.getTestListenerTypes());
    }

    @Test
    public void noTestListenersFound() {
        UnitilsContext result = unitilsContextFactory.create();
        assertTrue(result.getTestListenerTypes().isEmpty());
    }

    @Test
    public void emptyTestListeners() {
        moduleProperties1.put(LISTENERS_PROPERTY, "");

        UnitilsContext result = unitilsContextFactory.create();
        assertTrue(result.getTestListenerTypes().isEmpty());
    }

    @Test(expected = UnitilsException.class)
    public void configuredTestListenerIsNotATestListener() {
        moduleProperties1.put(LISTENERS_PROPERTY, Map.class.getName());
        unitilsContextFactory.create();
    }

    @Test(expected = UnitilsException.class)
    public void invalidTestListener() {
        moduleProperties1.put(LISTENERS_PROPERTY, "xxx");
        unitilsContextFactory.create();
    }


    private static class MyTestListener1 extends TestListener {
    }

    private static class MyTestListener2 extends TestListener {
    }

    private static class MyTestListener3 extends TestListener {
    }
}
