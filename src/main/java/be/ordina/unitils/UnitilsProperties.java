package be.ordina.unitils;

import be.ordina.unitils.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

/**
 * Utility class for loading Unitils' general properties file
 */
public class UnitilsProperties {

    /* Name of the properties file */
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "unitils.properties";

    public static Properties loadProperties(String propertiesFileName) {
        String userHomeFileName = System.getProperty("user.home") + '/' + propertiesFileName;
        if (new File(userHomeFileName).exists()) {
            return PropertiesUtils.loadPropertiesFromFile(userHomeFileName);
        } else {
            return PropertiesUtils.loadPropertiesFromClasspath(propertiesFileName);
        }
    }

}
