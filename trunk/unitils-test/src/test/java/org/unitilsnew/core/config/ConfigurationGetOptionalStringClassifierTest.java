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
public class ConfigurationGetOptionalStringClassifierTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("property", "value1");
        properties.setProperty("property.level1", "value2");
        properties.setProperty("property.level1.level2", "value3");
        configuration = new Configuration(properties);
    }


    @Test
    public void noClassifiers() {
        String result = configuration.getOptionalString("property");
        assertEquals("value1", result);
    }

    @Test
    public void nullClassifierSameAsNoClassifiers() {
        String classifier = null;
        String result = configuration.getOptionalString("property", classifier);
        assertEquals("value1", result);
    }

    @Test
    public void oneClassifier() {
        String result = configuration.getOptionalString("property", "level1");
        assertEquals("value2", result);
    }

    @Test
    public void twoClassifiers() {
        String result = configuration.getOptionalString("property", "level1", "level2");
        assertEquals("value3", result);
    }

    @Test
    public void noPropertyForSecondClassifierFallsBackToFirstClassifier() {
        configuration.getProperties().remove("property.level1.level2");

        String result = configuration.getOptionalString("property", "level1", "level2");
        assertEquals("value2", result);
    }

    @Test
    public void noFallBackForEmptyValue() {
        configuration.getProperties().setProperty("property.level1.level2", "  ");

        String result = configuration.getOptionalString("property", "level1", "level2");
        assertNull(result);
    }

    @Test
    public void noValuesForClassifiersFallsBackToNoClassifier() {
        configuration.getProperties().remove("property.level1");
        configuration.getProperties().remove("property.level1.level2");

        String result = configuration.getOptionalString("property", "level1", "level2");
        assertEquals("value1", result);
    }

    @Test
    public void noPropertiesFound() {
        configuration.getProperties().remove("property");
        configuration.getProperties().remove("property.level1");
        configuration.getProperties().remove("property.level1.level2");

        String result = configuration.getOptionalString("property", "level1", "level2");
        assertNull(result);
    }
}
