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
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationOverridingPropertiesTest {

    /* Tested object */
    private Configuration configuration;

    private Properties properties = new Properties();
    private Properties overridingProperties = new Properties();


    @Before
    public void initialize() {
        configuration = new Configuration(properties);
        configuration.setOverridingProperties(overridingProperties);
    }


    @Test
    public void overriddenProperty() {
        properties.put("key", "111");
        overridingProperties.put("key", "222");

        String result = configuration.getOptionalString("key");
        assertEquals("222", result);
    }

    @Test
    public void notOverriddenProperty() {
        properties.put("key A", "111");
        overridingProperties.put("key B", "222");

        String result = configuration.getOptionalString("key A");
        assertEquals("111", result);
    }

    @Test
    public void withClassifiers() {
        properties.put("key.a.b", "111");
        overridingProperties.put("key.a.b", "222");

        String result = configuration.getOptionalString("key", "a", "b");
        assertEquals("222", result);
    }

    @Test
    public void overridenPropertiesSetToNull() {
        configuration.setOverridingProperties(null);
        properties.put("key", "111");

        String result = configuration.getOptionalString("key");
        assertEquals("111", result);
    }

    @Test
    public void getAndSetOverridingProperties() {
        Properties result = configuration.getOverridingProperties();
        assertSame(overridingProperties, result);
    }
}
