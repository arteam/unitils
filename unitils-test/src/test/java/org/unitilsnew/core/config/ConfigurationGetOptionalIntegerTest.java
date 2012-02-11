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
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetOptionalIntegerTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("intProperty", "5");
        properties.setProperty("valueWithSpaces", "  5  ");
        properties.setProperty("invalidIntValue", "xxx");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "5");
        configuration = new Configuration(properties);
    }


    @Test
    public void validValue() {
        Integer result = configuration.getOptionalInteger("intProperty");
        assertReflectionEquals(5, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getOptionalInteger("invalidIntValue");
    }

    @Test
    public void valueIsTrimmed() {
        Integer result = configuration.getOptionalInteger("valueWithSpaces");
        assertReflectionEquals(5, result);
    }

    @Test
    public void nullWhenNotFound() {
        Integer result = configuration.getOptionalInteger("xxx");
        assertNull(result);
    }

    @Test
    public void nullWhenEmpty() {
        Integer result = configuration.getOptionalInteger("empty");
        assertNull(result);
    }

    @Test
    public void valueWithClassifiers() {
        Integer result = configuration.getOptionalInteger("propertyWithClassifiers", "a", "b");
        assertReflectionEquals(5, result);
    }
}
