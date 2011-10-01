/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.core;

import org.apache.commons.logging.Log;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import static org.unitils.core.ConfigurationLoader.PROPKEY_CUSTOM_CONFIGURATION;
import static org.unitils.core.ConfigurationLoader.PROPKEY_LOCAL_CONFIGURATION;
import org.unitils.core.util.PropertiesReader;
import org.unitils.inject.annotation.*;
import org.unitils.mock.Mock;

import java.util.Properties;

/**
 * Tests how the configuration loader deals with loading, overriding and expanding property values.
 *
 * @author Tim Ducheyne
 * @author Fabian Krueger
 * @author Filip Neven
 */
public class ConfigurationLoaderPropertyTest extends UnitilsJUnit4 {

    /* System under Test */
    @TestedObject
    private ConfigurationLoader configurationLoader;

    /* PropertiesReader used by sut */
    @InjectIntoByType
    private Mock<PropertiesReader> propertiesReader;

    /* Logger used by sut */
    @InjectIntoStaticByType(target = ConfigurationLoader.class)
    private Mock<Log> usedLogger;

    /* Faked default Properties (unitils.properties) */
    private Properties unitilsDefaultProperties;

    /* Faked custom Properties */
    private Properties customProperties;

    /* Faked local Properties from user.home */
    private Properties localProperties;


    @Before
    public void setUp() {
        configurationLoader = new ConfigurationLoader();

        localProperties = new Properties();
        customProperties = new Properties();
        unitilsDefaultProperties = new Properties();

        unitilsDefaultProperties.put(PROPKEY_CUSTOM_CONFIGURATION, "unitils.properties");
        unitilsDefaultProperties.put(PROPKEY_LOCAL_CONFIGURATION, "unitils-local.properties");
    }

    @Before
    public void cleanup() {
        System.clearProperty("xxx");
        System.clearProperty("property");
    }


    @Test
    public void onlyDefaultProperty() {
        unitilsDefaultProperties.put("xxx", "default");
        customProperties.put("yyy", "custom");
        localProperties.put("yyy", "local");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("default", result.get("xxx"));
    }


    @Test
    public void overriddenByCustom() {
        unitilsDefaultProperties.put("xxx", "default");
        customProperties.put("xxx", "custom");
        localProperties.put("yyy", "local");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("custom", result.get("xxx"));
    }


    @Test
    public void overriddenByLocal() {
        unitilsDefaultProperties.put("xxx", "default");
        customProperties.put("yyy", "custom");
        localProperties.put("xxx", "local");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("local", result.get("xxx"));
    }


    @Test
    public void overriddenByCustomAndLocal() {
        unitilsDefaultProperties.put("xxx", "default");
        customProperties.put("xxx", "custom");
        localProperties.put("xxx", "local");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("local", result.get("xxx"));
    }


    @Test
    public void overriddenByEnvironment() {
        unitilsDefaultProperties.put("xxx", "default");
        customProperties.put("xxx", "custom");
        localProperties.put("xxx", "local");
        System.setProperty("xxx", "system");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("system", result.get("xxx"));
    }


    @Test
    public void expanded() {
        customProperties.put("xxx", "${property}");
        customProperties.put("property", "value");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("value", result.get("xxx"));
    }


    @Test
    public void expandedTwice() {
        customProperties.put("xxx", "${property1}");
        customProperties.put("property1", "${property2}");
        customProperties.put("property2", "value");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("value", result.get("xxx"));
    }


    @Test
    public void customExpandedByLocal() {
        customProperties.put("xxx", "${property}");
        localProperties.put("property", "localValue");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("localValue", result.get("xxx"));
    }


    @Test
    public void customExpandedBySystemOverridingLocal() {
        customProperties.put("xxx", "${property}");
        localProperties.put("property", "localValue");
        System.setProperty("property", "systemValue");
        setProperties();

        Properties result = configurationLoader.loadConfiguration();
        assertEquals("systemValue", result.get("xxx"));
    }


    @Test(expected = UnitilsException.class)
    public void cyclicExpansion() {
        customProperties.put("xxx", "${yyy}");
        customProperties.put("yyy", "${xxx}");
        setProperties();

        configurationLoader.loadConfiguration();
    }


    private void setProperties() {
        propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath("unitils.properties");
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath("unitils-default.properties");
        propertiesReader.returns(localProperties).loadPropertiesFileFromUserHome("unitils-local.properties");
    }
}