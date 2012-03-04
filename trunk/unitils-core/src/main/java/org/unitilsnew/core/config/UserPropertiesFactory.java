/*
 * Copyright 2012,  Unitils.org
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitilsnew.core.Factory;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class UserPropertiesFactory implements Factory<Properties> {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UserPropertiesFactory.class);


    public static final String DEFAULT_UNITILS_PROPERTIES_NAME = "unitils.properties";
    public static final String DEFAULT_LOCAL_PROPERTIES_NAME = "unitils-local.properties";
    /**
     * Property in the defaults configuration file that contains the name of the custom configuration file
     */
    public static final String UNITILS_PROPERTIES_NAME_PROPERTY = "unitils.configuration.customFileName";
    /**
     * Property in the defaults and/or custom configuration file that contains the name of the user local configuration file
     */
    public static final String LOCAL_PROPERTIES_NAME_PROPERTY = "unitils.configuration.localFileName";


    protected PropertiesReader propertiesReader;


    public UserPropertiesFactory(PropertiesReader propertiesReader) {
        this.propertiesReader = propertiesReader;
    }


    public Properties create() {
        try {
            Properties properties = new Properties();
            addUnitilsProperties(properties);
            addUnitilsLocalProperties(properties);
            addSystemProperties(properties);
            expandPropertyValues(properties);

            return properties;
        } catch (Exception e) {
            throw new UnitilsException("Unable to load user properties.", e);
        }
    }


    /**
     * Load the custom project level configuration file (unitils.properties)
     *
     * @param properties The instance to add to loaded properties to, not null
     */
    protected void addUnitilsProperties(Properties properties) {
        String customConfigurationFileName = getPropertiesFileName(UNITILS_PROPERTIES_NAME_PROPERTY, DEFAULT_UNITILS_PROPERTIES_NAME, properties);
        Properties customProperties = propertiesReader.loadPropertiesFromClasspath(customConfigurationFileName);
        if (customProperties == null) {
            logger.info("No properties found in classpath with name " + customConfigurationFileName);
        } else {
            properties.putAll(customProperties);
        }
    }

    /**
     * Load the local configuration file from the user home, or from the classpath
     *
     * @param properties The instance to add to loaded properties to, not null
     */
    protected void addUnitilsLocalProperties(Properties properties) {
        String localConfigurationFileName = getPropertiesFileName(UNITILS_PROPERTIES_NAME_PROPERTY, DEFAULT_LOCAL_PROPERTIES_NAME, properties);
        Properties localProperties = propertiesReader.loadPropertiesFromUserHome(localConfigurationFileName);
        if (localProperties == null) {
            localProperties = propertiesReader.loadPropertiesFromClasspath(localConfigurationFileName);
        }
        if (localProperties == null) {
            logger.info("No properties found in user home or classpath with name " + localConfigurationFileName);
        } else {
            properties.putAll(localProperties);
        }
    }

    /**
     * Load the environment properties.
     *
     * @param properties The instance to add to loaded properties to, not null
     */
    protected void addSystemProperties(Properties properties) {
        properties.putAll(System.getProperties());
    }


    /**
     * Expands all property place holders to actual values. For example
     * suppose you have a property defined as follows: root.dir=/usr/home
     * Expanding following ${root.dir}/someSubDir
     * will then give following result: /usr/home/someSubDir
     *
     * @param properties The properties, not null
     */
    protected void expandPropertyValues(Properties properties) {
        for (Object key : properties.keySet()) {
            Object value = properties.get(key);
            try {
                String expandedValue = StrSubstitutor.replace(value, properties);
                properties.put(key, expandedValue);
            } catch (Exception e) {
                throw new UnitilsException("Could not expand property value for key " + key + " and value " + value, e);
            }
        }

    }

    /**
     * Gets the configuration file name from the system properties or if not defined, from the given loaded properties.
     * An exception is raised if no value is defined.
     *
     * @param fileNameProperty The name of the property that defines the local/custom file name, not null
     * @param defaultFileName  The default value to use, when no system property was found for the file name, not null
     * @param properties       The properties that were already loaded, not null
     * @return The property value, not null
     */
    protected String getPropertiesFileName(String fileNameProperty, String defaultFileName, Properties properties) {
        String configurationFileName = properties.getProperty(fileNameProperty);
        if (StringUtils.isBlank(configurationFileName)) {
            return defaultFileName;
        }
        return configurationFileName;
    }
}
