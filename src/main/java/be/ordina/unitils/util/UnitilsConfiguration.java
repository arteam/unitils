/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.util;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Properties;

/**
 * Utility for keeping all configuration settings of unitils.
 * <p/>
 * Unitils settings can be defined in 3 files:<ul>
 * <li><b>unitils-default.configuration</b> - a fixed file packaged in the unitils jar that contains all predefined defaults.
 * This file should typically not be modified.</li>
 * <li><b>unitils.configuration</b> - a file somewhere in the classpath or user.home dir that contains all custom configuration
 * settings. Settings in this file will override the unitil default settings. This is where you should put your project
 * specific configuration</li>
 * <li><b>unitils-local.propeties</b> - a file somewhere in the classpath or user.home that contains machine/user local
 * configuration. Eg the database schema specific to the local user could be defined here. Settings in this file
 * will override the unitil default and custom settings.</li>
 * </ul>
 * The name of the custom settings file (unitils.configuration) is defined by the {@link #PROPERTY_CUSTOM_CONFIGURATION}
 * property in the default settings. The name of the local settings file (unitils-local.propeties) is defined
 * by the {@link #PROPERTY_LOCAL_CONFIGURATION} in the custom or default settings. If these configuration are set to
 * null or empty, the corresponding property file will not be loaded.
 * <p/>
 * A runtime exception is thrown when the default settings cannot be loaded.
 * A warning is logged when the custom propreties cannot be loaded.
 * A debug message is logged when the local configuration cannot be loaded.
 */
public class UnitilsConfiguration {

    //TODO REMOVE properties

    /* Name of the properties file */
    public static Properties loadProperties(String propertiesFileName) {
        String userHomeFileName = System.getProperty("user.home") + '/' + propertiesFileName;
        if (new File(userHomeFileName).exists()) {
            return PropertiesUtils.loadPropertiesFromFile(userHomeFileName);
        } else {
            return PropertiesUtils.loadPropertiesFromClasspath(propertiesFileName);
        }
    }

    /**
     * Name of the fixed configuration file that contains all defaults
     */
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "unitils-default.configuration";

    /**
     * Property in the defaults configuration file that contains the name of the custom configuration file
     */
    public static final String PROPERTY_CUSTOM_CONFIGURATION = "unitils.configuration.customFileName";

    /**
     * Property in the defaults and/or custom configuration file that contains the name of
     * the user local configuration file
     */
    public static final String PROPERTY_LOCAL_CONFIGURATION = "unitils.configuration.localFileName";


    /* The logger instance for this class */
    private static final Logger logger = Logger.getLogger(UnitilsConfiguration.class);

    /* The configuration settings */
    private static Configuration configuration;


    /**
     * Gets the configuration settings.
     *
     * @return the settings, not null
     */
    public static synchronized Configuration getInstance() {

        if (configuration == null) {
            configuration = createConfiguration();
        }
        return configuration;
    }


    /**
     * Creates and loads all configuration settings.
     *
     * @return the settings, not null
     */
    private static Configuration createConfiguration() {

        Configuration defaultConfiguration = createDefaultConfiguration();
        Configuration customConfiguration = createCustomConfiguration(defaultConfiguration);
        Configuration localConfiguration = createLocalConfiguration(defaultConfiguration, customConfiguration);

        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        compositeConfiguration.setThrowExceptionOnMissing(true);

        if (localConfiguration != null) {
            compositeConfiguration.addConfiguration(localConfiguration);
        }
        if (customConfiguration != null) {
            compositeConfiguration.addConfiguration(customConfiguration);
        }
        compositeConfiguration.addConfiguration(defaultConfiguration);
        return compositeConfiguration;
    }


    /**
     * Creates and loads the default configuration settings from the {@link #DEFAULT_PROPERTIES_FILE_NAME} file.
     *
     * @return the defaults, not null
     * @throws RuntimeException if the file cannot be found or loaded
     */
    private static Configuration createDefaultConfiguration() {
        try {
            return new PropertiesConfiguration(DEFAULT_PROPERTIES_FILE_NAME);

        } catch (ConfigurationException e) {

            throw new RuntimeException("Initialisation error. Unable to load main configuration file: " + DEFAULT_PROPERTIES_FILE_NAME, e);
        }
    }


    /**
     * Creates and loads the custom configuration settings. The name of the settings file is defined by the
     * {@link #PROPERTY_CUSTOM_CONFIGURATION} property in the given default configuration.
     *
     * @param defaultConfiguration the default settings, not null
     * @return the custom settings, null if not found or not loaded
     */
    private static Configuration createCustomConfiguration(Configuration defaultConfiguration) {

        String customConfigurationFileName = defaultConfiguration.getString(PROPERTY_CUSTOM_CONFIGURATION);
        if (StringUtils.isEmpty(customConfigurationFileName)) {
            // loading of local settings disabled
            return null;
        }

        try {
            return new PropertiesConfiguration(customConfigurationFileName);

        } catch (ConfigurationException e) {
            logger.warn("Unable to load custom configuration file: " + customConfigurationFileName);
            return null;
        }
    }


    /**
     * Creates and loads the local configuration settings. The name of the settings file is defined by the
     * {@link #PROPERTY_LOCAL_CONFIGURATION} property in the given custom or default configuration.
     *
     * @param defaultConfiguration the default settings, not null
     * @param customConfiguration  the custom settings, can be null
     * @return the local settings, null if not found or not loaded
     */
    private static Configuration createLocalConfiguration(Configuration defaultConfiguration, Configuration customConfiguration) {

        String localConfigurationFileName = null;
        if (customConfiguration != null) {
            // try custom settings
            localConfigurationFileName = customConfiguration.getString(PROPERTY_LOCAL_CONFIGURATION);
        }
        if (StringUtils.isEmpty(localConfigurationFileName)) {
            // not found in custom settings, try defaults
            localConfigurationFileName = defaultConfiguration.getString(PROPERTY_LOCAL_CONFIGURATION);
        }
        if (StringUtils.isEmpty(localConfigurationFileName)) {
            // loading of local settings disabled
            return null;
        }

        try {
            return new PropertiesConfiguration(localConfigurationFileName);

        } catch (ConfigurationException e) {
            logger.debug("Unable to load local configuration file: " + localConfigurationFileName);
            return null;
        }
    }

}
