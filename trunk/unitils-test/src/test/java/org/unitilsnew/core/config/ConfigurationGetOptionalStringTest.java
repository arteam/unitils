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

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalStringTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("stringProperty", "stringValue");
        properties.setProperty("propertyWithSpaces", "    value   ");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "value");
        configuration = new Configuration(properties);
    }


    @Test
    public void found() {
        String result = configuration.getOptionalString("stringProperty");
        assertEquals("stringValue", result);
    }

    @Test
    public void valueIsTrimmed() {
        String result = configuration.getOptionalString("propertyWithSpaces");
        assertEquals("value", result);
    }

    @Test
    public void nullWhenNotFound() {
        String result = configuration.getOptionalString("xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        String result = configuration.getOptionalString("empty");
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        String result = configuration.getOptionalString("propertyWithClassifiers", "a", "b");
        assertEquals("value", result);
    }
}
