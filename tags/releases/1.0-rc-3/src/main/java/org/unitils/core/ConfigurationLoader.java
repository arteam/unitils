/*
 * Copyright 2006 the original author or authors.
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
import org.apache.commons.logging.LogFactory;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.PropertyUtils.getString;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


/**
 * Utility that loads the configuration of unitils.
 * <p/>
 * Unitils settings can be defined in 3 files:<ul>
 * <li><b>unitils-default.properties</b> - a fixed file packaged in the unitils jar that contains all predefined defaults.
 * This file should normally not be modified.</li>
 * <li><b>unitils.properties</b> - a file somewhere in the classpath or user.home dir that contains all custom configuration
 * settings. Settings in this file will override the default settings. This is where you should put your project
 * specific configuration</li>
 * <li><b>unitils-local.properties</b> - a file somewhere in the classpath or user.home that contains machine/user local
 * configuration. Eg the database schema specific to the local user could be defined here. Settings in this file
 * will override the unitil default and custom settings.</li>
 * </ul>
 * The name of the custom settings file (unitils.properties) is defined by the {@link #PROPKEY_CUSTOM_CONFIGURATION}
 * property in the default settings. The name of the local settings file (unitils-local.propeties) is defined
 * by the {@link #PROPKEY_LOCAL_CONFIGURATION} in the custom or default settings. If these properties are set to
 * null or empty, the corresponding property file will not be loaded.
 * <p/>
 * A runtime exception is thrown when the default properties cannot be loaded.
 * A warning is logged when the custom propreties cannot be loaded.
 * A debug message is logged when the local properties cannot be loaded.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ConfigurationLoader {

    /**
     * Name of the fixed configuration file that contains all defaults
     */
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "unitils-default.properties";

    /**
     * Property in the defaults configuration file that contains the name of the custom configuration file
     */
    public static final String PROPKEY_CUSTOM_CONFIGURATION = "unitils.configuration.customFileName";

    /**
     * Property in the defaults and/or custom configuration file that contains the name of
     * the user local configuration file
     */
    public static final String PROPKEY_LOCAL_CONFIGURATION = "unitils.configuration.localFileName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(ConfigurationLoader.class);

    
    /**
     * Creates and loads all configuration settings.
     *
     * @return the settings, not null
     */
    public Properties loadConfiguration() {
        return loadConfiguration(null);
    }
    

    /**
     * Creates and loads all configuration settings.
     * 
     * @param customConfigurationFileName The name of the custom configuration file. 
     *        May be null: if so, the fileName is retrieved from the default properties. 
     *
     * @return the settings, not null
     */
    public Properties loadConfiguration(String customConfigurationFileName) {
        Properties defaultProperties = createDefaultProperties();
        Properties customProperties = createCustomProperties(defaultProperties, customConfigurationFileName);
        Properties localProperties = createLocalProperties(defaultProperties, customProperties);

        Properties result = new Properties();
        result.putAll(defaultProperties);
        if (customProperties != null) {
            result.putAll(customProperties);
        }
        if (localProperties != null) {
            result.putAll(localProperties);
        }
        return result;
    }


    /**
     * Creates and loads the default configuration settings from the {@link #DEFAULT_PROPERTIES_FILE_NAME} file.
     *
     * @return the defaults, not null
     * @throws RuntimeException if the file cannot be found or loaded
     */
    protected Properties createDefaultProperties() {
        InputStream inputStream = null;
        try {
            Properties properties = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE_NAME);
            if (inputStream == null) {
                throw new UnitilsException("Main configuration file: " + DEFAULT_PROPERTIES_FILE_NAME + " not found.");
            }
            properties.load(inputStream);
            logger.info("Loaded main configuration file " + DEFAULT_PROPERTIES_FILE_NAME + " from classpath.");
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load main configuration file: " + DEFAULT_PROPERTIES_FILE_NAME, e);
        } finally {
            closeQuietly(inputStream);
        }
    }


    /**
     * Creates and loads the custom configuration settings. The name of the settings file is defined by the
     * {@link #PROPKEY_CUSTOM_CONFIGURATION} property in the given default configuration.
     *
     * @param defaultProperties the default settings, not null
     * @param customConfigurationFileName TODO
     * @return the custom settings, null if not found or not loaded
     */
    protected Properties createCustomProperties(Properties defaultProperties, String customConfigurationFileName) {
        if (customConfigurationFileName == null) {
            customConfigurationFileName = getString(PROPKEY_CUSTOM_CONFIGURATION, null, defaultProperties);
        }
        if (customConfigurationFileName == null) {
            // loading of local settings disabled
            return null;
        }

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(customConfigurationFileName);
            if (inputStream == null) {
                logger.info("No custom configuration file " + customConfigurationFileName + " found.");
                return null;
            }
            properties.load(inputStream);
            logger.info("Loaded custom configuration file " + customConfigurationFileName + " from classpath.");
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load custom configuration file: " + customConfigurationFileName, e);
        } finally {
            closeQuietly(inputStream);
        }
    }


    /**
     * Creates and loads the local configuration settings. The name of the settings file is defined by the
     * {@link #PROPKEY_LOCAL_CONFIGURATION} property in the given custom or default configuration.
     *
     * @param defaultProperties the default properties, not null
     * @param customProperties  the custom properties, can be null
     * @return the local properties, null if not found or not loaded
     */
    protected Properties createLocalProperties(Properties defaultProperties, Properties customProperties) {
        String localPropertiesFileName = null;
        if (customProperties != null) {
            // try custom settings
            localPropertiesFileName = getString(PROPKEY_LOCAL_CONFIGURATION, null, customProperties);
        }
        if (localPropertiesFileName == null) {
            // not found in custom settings, try defaults
            localPropertiesFileName = getString(PROPKEY_LOCAL_CONFIGURATION, null, defaultProperties);
        }
        if (localPropertiesFileName == null) {
            // loading of local settings disabled
            return null;
        }

        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            // try loading from the user home folder
            String userHomeDir = System.getProperty("user.home");
            File localPropertiesFile = new File(userHomeDir, localPropertiesFileName);
            if (localPropertiesFile.exists()) {
                inputStream = new FileInputStream(localPropertiesFile);
                properties.load(inputStream);
                logger.info("Loaded local configuration file " + localPropertiesFileName + " from " + localPropertiesFile);
                return properties;
            }

            // try loading from classpath
            inputStream = getClass().getClassLoader().getResourceAsStream(localPropertiesFileName);
            if (inputStream == null) {
                logger.info("No local configuration file " + localPropertiesFileName + " found.");
                return null;
            }
            properties.load(inputStream);
            logger.info("Loaded local configuration file " + localPropertiesFileName + " from classpath.");
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load local configuration file: " + localPropertiesFileName, e);
        } finally {
            closeQuietly(inputStream);
        }
    }


    

}
