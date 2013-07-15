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
package org.unitils.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 * @author Fabian Krueger
 */
public class PropertiesReader {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(PropertiesReader.class);


    /**
     * Loads the properties file with the given name, which is available in the user home folder. If no
     * file with the given name is found, null is returned.
     *
     * @param propertiesFileName The name of the properties file, not null
     * @return The Properties object, null if the properties file wasn't found.
     */
    public Properties loadPropertiesFromUserHome(String propertiesFileName) {
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
            logger.info("Loaded properties from user home with name " + propertiesFileName);
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load properties from user home with file name " + propertiesFileName, e);
        } finally {
            closeQuietly(inputStream);
        }
    }

    /**
     * Loads the properties file with the given name, which is available in the classpath. If no
     * file with the given name is found, null is returned.
     *
     * @param propertiesFileName The name of the properties file, not null
     * @return The Properties object, null if the properties file wasn't found.
     */
    public Properties loadPropertiesFromClasspath(String propertiesFileName) {
        try {
            URL propertiesURL = getClass().getClassLoader().getResource(propertiesFileName);
            return loadPropertiesFromURL(propertiesURL);
        } catch (Exception e) {
            throw new UnitilsException("Unable to load properties from classpath with file name " + propertiesFileName, e);
        }
    }

    /**
     * Loads the properties file with the given name, which is available in the classpath. If no
     * file with the given name is found, null is returned.
     *
     * @param propertiesFileName The name of the properties file, not null
     * @return The Properties object, null if the properties file wasn't found.
     */
    public List<Properties> loadAllPropertiesFromClasspath(String propertiesFileName) {
        try {
            List<Properties> result = new ArrayList<Properties>();
            Enumeration<URL> propertiesURLs = getClass().getClassLoader().getResources(propertiesFileName);
            while (propertiesURLs.hasMoreElements()) {
                URL propertiesURL = propertiesURLs.nextElement();
                Properties properties = loadPropertiesFromURL(propertiesURL);
                if (properties != null) {
                    result.add(properties);
                }
            }
            return result;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load properties from classpath with file name " + propertiesFileName, e);
        }
    }


    protected Properties loadPropertiesFromURL(URL propertiesURL) throws IOException {
        if (propertiesURL == null) {
            return null;
        }
        InputStream inputStream = null;
        try {
            Properties properties = new Properties();

            inputStream = propertiesURL.openStream();
            properties.load(inputStream);
            return properties;

        } finally {
            closeQuietly(inputStream);
        }
    }
}
