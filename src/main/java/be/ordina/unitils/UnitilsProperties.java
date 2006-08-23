package be.ordina.unitils;

import be.ordina.unitils.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

/**
 * Utility class for loading Unitils' general properties file
 */
public class UnitilsProperties {

    /* Name of the properties file */
    private static final String PROPERTIES_FILE_NAME = "unittest.properties";

    public static Properties loadProperties() {
        String userHomeFileName = System.getProperty("user.home") + '/' + PROPERTIES_FILE_NAME;
        if (new File(userHomeFileName).exists()) {
            return PropertiesUtils.loadPropertiesFromFile(userHomeFileName);
        } else {
            return PropertiesUtils.loadPropertiesFromClasspath(PROPERTIES_FILE_NAME);
        }
    }
}
