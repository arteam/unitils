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

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetAllPropertiesTest {

    /* Tested object */
    private Configuration configuration;

    private Properties properties;
    private Properties overridingProperties;


    @Before
    public void initialize() {
        properties = new Properties();
        overridingProperties = new Properties();
        properties.put("a", "a");
        overridingProperties.put("b", "b");
    }


    @Test
    public void getAllProperties() {
        configuration = new Configuration(properties);
        configuration.setOverridingProperties(overridingProperties);

        Properties result = configuration.getAllProperties();
        assertEquals(2, result.size());
        assertTrue(result.containsKey("a"));
        assertTrue(result.containsKey("b"));
    }

    @Test
    public void noOverridingProperties() {
        configuration = new Configuration(properties);

        Properties result = configuration.getAllProperties();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("a"));
    }
}
