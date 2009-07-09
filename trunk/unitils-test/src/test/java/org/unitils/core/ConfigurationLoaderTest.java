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
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import static org.unitils.core.ConfigurationLoader.*;
import org.unitils.core.util.PropertiesReader;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.InjectIntoStaticByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import java.util.Properties;

/**
 * Test for {@link ConfigurationLoader}.
 *
 * @author Fabian Krueger
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ConfigurationLoaderTest extends UnitilsJUnit4 {

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

    /* The default custom property filename */
    private final String CUSTOM_PROPERTIES_FILE_NAME = "unitils.properties";

    /* The default local property filename */
    private final String LOCAL_PROPERTIES_FILE_NAME = "unitils-local.properties";


    @Before
    public void setUp() {
        configurationLoader = new ConfigurationLoader();

        localProperties = new Properties();
        localProperties.put("local", "value");

        customProperties = new Properties();
        customProperties.put("custom", "value");

        unitilsDefaultProperties = new Properties();
        unitilsDefaultProperties.put("default", "value");
        unitilsDefaultProperties.put(PROPKEY_CUSTOM_CONFIGURATION, CUSTOM_PROPERTIES_FILE_NAME);
        unitilsDefaultProperties.put(PROPKEY_LOCAL_CONFIGURATION, LOCAL_PROPERTIES_FILE_NAME);
    }


    @After
    public void cleanup() {
        System.clearProperty(PROPKEY_CUSTOM_CONFIGURATION);
        System.clearProperty(PROPKEY_LOCAL_CONFIGURATION);
    }


    /**
     * Test scenario:
     * <ul>
     * <li>unitils.properties file not found</li>
     * <li>Exception thrown</li>
     * </ul>
     */
    @Test
    public void noDefaultConfigurationFound() {
        String expectedMessage = "Configuration file: " + DEFAULT_PROPERTIES_FILE_NAME + " not found in classpath.";
        try {
            propertiesReader.returns(null).loadPropertiesFileFromClasspath(null);
            configurationLoader.loadConfiguration();
            fail("Exception expected.");

        } catch (UnitilsException ue) {
            assertEquals(expectedMessage, ue.getMessage());
        }
    }

    /**
     * Test scenario:
     * <ul>
     * <li>unitils.properties file found in classpath</li>
     * <li>custom configuration file not found</li>
     * <li>local configuration file not found</li>
     * </ul>
     */
    @Test
    public void onlyDefaultConfigurationFound() {
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
        propertiesReader.returns(null).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
        propertiesReader.returns(null).loadPropertiesFileFromUserHome(LOCAL_PROPERTIES_FILE_NAME);
        propertiesReader.returns(null).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        assertDefaultPropertiesLoaded(returnedProperties);
        assertNoCustomConfigurationFound(CUSTOM_PROPERTIES_FILE_NAME);
        assertNoLocalConfigurationFound(LOCAL_PROPERTIES_FILE_NAME);
    }


    /**
     * Test scenario:
     * <ul>
     * <li>unitils.properties file found in classpath</li>
     * <li>custom configuration file found</li>
     * <li>local configuration file not found</li>
     * <li>returns properties from unitils.properties overwritten with properties from custom configuration</li>
     * </ul>
     */
    @Test
    public void defaultAndCustomConfigurationFound() {
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
        propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
        propertiesReader.returns(null).loadPropertiesFileFromUserHome(LOCAL_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        assertDefaultPropertiesLoaded(returnedProperties);
        assertCustomPropertiesLoaded(returnedProperties);
        assertNoLocalConfigurationFound(LOCAL_PROPERTIES_FILE_NAME);
    }


    /**
     * Test scenario:
     * <ul>
     * <li>no filename given</li>
     * <li>unitils.properties file found in classpath</li>
     * <li>custom configuration file found</li>
     * <li>local configuration file found in user home directory</li>
     * <li>returns properties from unitils.properties first overwritten with custom properties then with user properties</li>
     * </ul>
     */
    @Test
    public void allConfigurationsFoundWithUserConfigurationFromHomeDir() {
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
        propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFileFromUserHome(LOCAL_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        assertDefaultPropertiesLoaded(returnedProperties);
        assertCustomPropertiesLoaded(returnedProperties);
        assertLocalPropertiesLoaded(returnedProperties);
    }


    /**
     * Test scenario:
     * <ul>
     * <li>no filename given</li>
     * <li>unitils.properties file found in classpath</li>
     * <li>custom configuration file found</li>
     * <li>local configuration file not found in user home directory</li>
     * <li>local configuration file found in classpath</li>
     * <li>returns properties from unitils.properties first overwritten with custom properties then with user properties</li>
     * </ul>
     */
    @Test
    public void allConfigurationsFoundWithUserConfigurationFromClasspath() {
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
        propertiesReader.returns(customProperties).loadPropertiesFileFromClasspath(CUSTOM_PROPERTIES_FILE_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFileFromClasspath(LOCAL_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        assertDefaultPropertiesLoaded(returnedProperties);
        assertCustomPropertiesLoaded(returnedProperties);
        assertLocalPropertiesLoaded(returnedProperties);
    }


    @Test
    public void customConfigurationFileNameOverriddenBySystemProperty() {
        System.setProperty(PROPKEY_CUSTOM_CONFIGURATION, "custom-filename.properties");
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        propertiesReader.assertInvoked().loadPropertiesFileFromClasspath("custom-filename.properties");
    }


    @Test
    public void localConfigurationFileNameOverriddenBySystemProperty() {
        System.setProperty(PROPKEY_LOCAL_CONFIGURATION, "custom-local-filename.properties");
        propertiesReader.returns(unitilsDefaultProperties).loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);

        Properties returnedProperties = configurationLoader.loadConfiguration();

        propertiesReader.assertInvoked().loadPropertiesFileFromClasspath("custom-local-filename.properties");
    }


    private void assertNoCustomConfigurationFound(String fileName) {
        usedLogger.assertInvoked().warn("No custom configuration file " + fileName + " found.");
    }

    private void assertNoLocalConfigurationFound(String fileName) {
        usedLogger.assertInvoked().info("No local configuration file " + fileName + " found.");
    }

    private void assertDefaultPropertiesLoaded(Properties properties) {
        assertTrue("Expected default properties to be loaded.", properties.containsKey("default"));
    }

    private void assertCustomPropertiesLoaded(Properties properties) {
        assertTrue("Expected custom properties to be loaded.", properties.containsKey("custom"));
    }

    private void assertLocalPropertiesLoaded(Properties properties) {
        assertTrue("Expected local properties to be loaded.", properties.containsKey("local"));
    }
}
