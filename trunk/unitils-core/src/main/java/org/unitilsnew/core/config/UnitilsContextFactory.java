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

import org.unitilsnew.core.Factory;
import org.unitilsnew.core.UnitilsContext;
import org.unitilsnew.core.listener.TestListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class UnitilsContextFactory implements Factory<UnitilsContext> {

    public static final String MODULE_PROPERTIES_NAME = "unitils-module.properties";

    protected Properties userProperties;
    protected PropertiesReader propertiesReader;


    public UnitilsContextFactory(Properties userProperties, PropertiesReader propertiesReader) {
        this.userProperties = userProperties;
        this.propertiesReader = propertiesReader;
    }


    public UnitilsContext create() {
        List<Properties> modulesProperties = loadModuleProperties();
        Configuration unitilsConfiguration = createUnitilsConfiguration(modulesProperties);
        List<Class<? extends TestListener>> testListenerTypes = getTestListenerTypes(modulesProperties);

        return new UnitilsContext(unitilsConfiguration, testListenerTypes);
    }


    protected Configuration createUnitilsConfiguration(List<Properties> modulesProperties) {
        Properties properties = new Properties();
        for (Properties moduleProperties : modulesProperties) {
            properties.putAll(moduleProperties);
        }
        Configuration configuration = new Configuration(properties);
        configuration.setOverridingProperties(userProperties);
        return configuration;
    }

    protected List<Class<? extends TestListener>> getTestListenerTypes(List<Properties> modulesProperties) {
        List<Class<? extends TestListener>> testListenerTypes = new ArrayList<Class<? extends TestListener>>();
        for (Properties moduleProperties : modulesProperties) {
            Configuration moduleConfiguration = new Configuration(moduleProperties);
            List<Class> types = moduleConfiguration.getOptionalValueListOfType(Class.class, TestListener.class.getName());
            for (Class<?> type : types) {
                // todo check type implements TestListener
                Class<? extends TestListener> testListenerType = (Class<TestListener>) type;
                testListenerTypes.add(testListenerType);
            }
        }
        return testListenerTypes;
    }

    protected List<Properties> loadModuleProperties() {
        return propertiesReader.loadAllPropertiesFromClasspath(MODULE_PROPERTIES_NAME);
    }
}
