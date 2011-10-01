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
package org.unitils.core.util;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;

/**
 * @author Fabian Krueger
 *
 */
public class PropertiesReader {
	
    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PropertiesReader.class);

    /**
     * Loads the properties file with the given name, which is available in the user home folder. If no
     * file with the given name is found, null is returned.
     * 
     * @param propertiesFileName The name of the properties file
     * @return The Properties object, null if the properties file wasn't found.
     */
	public Properties loadPropertiesFileFromUserHome(
			String propertiesFileName) {
        InputStream inputStream = null;
        try {
        	if("".equals(propertiesFileName)){
        		throw new IllegalArgumentException("Properties Filename must be given.");
        	}
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

	/**
	 * Loads the properties file with the given name, which is available in the classpath. If no
	 * file with the given name is found, null is returned.
	 * 
	 * @param propertiesFileName The name of the properties file
	 * @return The Properties object, null if the properties file wasn't found.
	 */
	public Properties loadPropertiesFileFromClasspath(String propertiesFileName) {
        InputStream inputStream = null;
        try {
        	if("".equals(propertiesFileName)){
        		throw new IllegalArgumentException("Properties Filename must be given.");
        	}
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
}
