/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.util.PropertyUtils;


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
    	Properties properties = new Properties();
    	
    	// Load the default properties file, that is distributed with unitils (unitils-default.properties)
    	Properties defaultProperties = loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
    	if (defaultProperties == null) {
    		throw new UnitilsException("Configuration file: " + defaultProperties + " not found in classpath.");
    	}
    	properties.putAll(defaultProperties);
    	
    	// Load the custom project level configuration file (unitils.properties)
    	if (customConfigurationFileName == null) {
    		customConfigurationFileName = PropertyUtils.getString(PROPKEY_CUSTOM_CONFIGURATION, properties);
    	}
    	Properties customProperties = loadPropertiesFileFromClasspath(customConfigurationFileName);
    	if (customProperties == null) {
    		logger.warn("No custom configuration file " + customConfigurationFileName + " found.");
        } else {
        	properties.putAll(customProperties);
        }
    	
    	// Load the local configuration file from the user home, or from the classpath
    	String localConfigurationFileName = PropertyUtils.getString(PROPKEY_LOCAL_CONFIGURATION, properties);
    	Properties localProperties = loadPropertiesFileFromUserHome(localConfigurationFileName);
    	if (localProperties == null) {
    		localProperties = loadPropertiesFileFromClasspath(localConfigurationFileName);
    	}
    	if (localProperties == null) {
    		logger.info("No custom configuration file " + customConfigurationFileName + " found.");
        } else {
        	properties.putAll(localProperties);
        }
        
        return new SystemPropertiesCheckingPropertiesDecorator(properties);
    }


    /**
     * Creates and loads the default configuration settings from the {@link #DEFAULT_PROPERTIES_FILE_NAME} file.
     *
     * @return the defaults, not null
     * @throws RuntimeException if the file cannot be found or loaded
     */
    public Properties getDefaultConfiguration() {
        return loadPropertiesFileFromClasspath(DEFAULT_PROPERTIES_FILE_NAME);
    }


	protected Properties loadPropertiesFileFromClasspath(String propertiesFileName) {
		InputStream inputStream = null;
        try {
            Properties properties = new Properties();
            inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);
            if (inputStream == null) {
                return null;
            }
            properties.load(inputStream);
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load configuration file: " + propertiesFileName, e);
        } finally {
            closeQuietly(inputStream);
        }
	}
	
	
	protected Properties loadPropertiesFileFromUserHome(String propertiesFileName) {
		InputStream inputStream = null;
        try {
            Properties properties = new Properties();
            String userHomeDir = System.getProperty("user.home");
            File localPropertiesFile = new File(userHomeDir, propertiesFileName);
            if (!localPropertiesFile.exists()) {
            	return null;
            }
            inputStream = new FileInputStream(localPropertiesFile);
            properties.load(inputStream);
            logger.info("Loaded configuration file " + propertiesFileName + " from user home");
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load configuration file: " + propertiesFileName + " from user home", e);
        } finally {
            closeQuietly(inputStream);
        }
	}

}
