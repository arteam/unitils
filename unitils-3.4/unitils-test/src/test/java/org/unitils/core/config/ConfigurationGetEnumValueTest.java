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
import org.unitils.core.UnitilsException;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.unitils.core.config.ConfigurationGetEnumValueTest.TestEnum.VALUE;

/**
 * @author Tim Ducheyne
 */
public class ConfigurationGetEnumValueTest {

    /* Tested object */
    private Configuration configuration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("enumProperty", "VALUE");
        properties.setProperty("valueWithSpaces", "    VALUE   ");
        properties.setProperty("invalidEnumValue", "xxx");
        properties.setProperty("empty", " ");
        properties.setProperty("propertyWithClassifiers.a.b", "VALUE");
        configuration = new Configuration(properties);
    }


    @Test
    public void validValue() {
        TestEnum result = configuration.getEnumValue(TestEnum.class, "enumProperty");
        assertEquals(VALUE, result);
    }

    @Test(expected = UnitilsException.class)
    public void invalidValue() {
        configuration.getEnumValue(TestEnum.class, "invalidEnumValue");
    }

    @Test
    public void valueIsTrimmed() {
        TestEnum result = configuration.getEnumValue(TestEnum.class, "valueWithSpaces");
        assertEquals(VALUE, result);
    }

    @Test(expected = UnitilsException.class)
    public void notFound() {
        configuration.getEnumValue(TestEnum.class, "xxx");
    }

    @Test(expected = UnitilsException.class)
    public void notFoundWhenEmpty() {
        configuration.getEnumValue(TestEnum.class, "empty");
    }

    @Test
    public void valueWithClassifiers() {
        TestEnum result = configuration.getEnumValue(TestEnum.class, "propertyWithClassifiers", "a", "b");
        assertEquals(VALUE, result);
    }

    public static enum TestEnum {

        VALUE
    }
}
