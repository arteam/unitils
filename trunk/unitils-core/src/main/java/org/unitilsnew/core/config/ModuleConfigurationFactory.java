/*
 * Copyright 2011,  Unitils.org
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

import org.unitils.core.UnitilsException;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Classifier;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class ModuleConfigurationFactory implements Factory<List<Configuration>> {

    public static final String MODULE_PROPERTIES_NAME = "unitils-module.properties";

    private Properties systemProperties;
    private Properties userProperties;

    public ModuleConfigurationFactory(@Classifier("system") Properties systemProperties,
                                      @Classifier("user") Properties userProperties) {
        this.systemProperties = systemProperties;
        this.userProperties = userProperties;
    }

    public List<Configuration> create() {
        List<Configuration> configurations = new ArrayList<Configuration>();
        try {
            Enumeration<URL> modulePropertiesURLs = getClass().getClassLoader().getResources(MODULE_PROPERTIES_NAME);
            while (modulePropertiesURLs.hasMoreElements()) {
                URL modulePropertiesURL = modulePropertiesURLs.nextElement();

                Configuration configuration = loadModuleConfiguration(modulePropertiesURL);
                configurations.add(configuration);
            }
        } catch (Exception e) {
            // todo
        }
        return configurations;
    }

    private Configuration loadModuleConfiguration(URL modulePropertiesURL) {
        try {
            InputStream modulePropertiesStream = modulePropertiesURL.openStream();

            Properties moduleProperties = new Properties();
            moduleProperties.load(modulePropertiesStream);
            moduleProperties.putAll(userProperties);
            moduleProperties.putAll(systemProperties);
            return new Configuration(moduleProperties);

        } catch (Exception e) {
            throw new UnitilsException("Unable to load module configuration from " + modulePropertiesURL, e);
        }
    }
}
