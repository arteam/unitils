/*
 * Copyright 2010,  Unitils.org
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
import org.unitilsnew.core.Context;
import org.unitilsnew.core.Factory;

import java.io.InputStream;
import java.util.Properties;

import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 */
public class BootstrapContextFactory implements Factory<Context> {

    public static final String UNITILS_CORE_PROPERTIES = "unitils-core.properties";


    public Context create() {
        Properties systemProperties = loadSystemProperties();
        Properties unitilsCoreProperties = loadUnitilsCoreProperties();

        Configuration bootstrapConfiguration = createBootstrapConfiguration(systemProperties, unitilsCoreProperties);
        Context context = new Context(bootstrapConfiguration);
        context.setInstanceOfType(Properties.class, systemProperties, "system");
        return context;
    }


    protected Configuration createBootstrapConfiguration(Properties systemProperties, Properties unitilsCoreProperties) {
        Properties properties = new Properties();
        properties.putAll(unitilsCoreProperties);
        properties.putAll(systemProperties);
        return new Configuration(properties);
    }


    protected Properties loadSystemProperties() {
        return System.getProperties();
    }

    protected Properties loadUnitilsCoreProperties() {
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream(UNITILS_CORE_PROPERTIES);

            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;

        } catch (Exception e) {
            throw new UnitilsException("Unable to load " + UNITILS_CORE_PROPERTIES, e);
        } finally {
            closeQuietly(inputStream);
        }
    }

}
