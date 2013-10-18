/*
 * Copyright 2011, Unitils.org
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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfigurationGetStringListTest {

    /* Tested object */
    private UnitilsConfiguration unitilsConfiguration;


    @Before
    public void initialize() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("stringListProperty", "test1, test2, test3");
        properties.setProperty("propertyWithSpaces", "   test1  , test2 ");
        properties.setProperty("propertyWithEmptyValues", "test1, , test2, , ");
        properties.setProperty("propertyWithOnlyEmptyValues", ", ,, , ");
        unitilsConfiguration = new UnitilsConfiguration(properties);
    }


    @Test
    public void found() {
        List<String> result = unitilsConfiguration.getStringList("stringListProperty");
        assertLenientEquals(asList("test1", "test2", "test3"), result);
    }

    @Test
    public void notRequiredFound() {
        List<String> result = unitilsConfiguration.getStringList("stringListProperty", false);
        assertLenientEquals(asList("test1", "test2", "test3"), result);
    }

    @Test
    public void notRequiredNotFound() {
        List<String> result = unitilsConfiguration.getStringList("xxx", false);
        assertTrue(result.isEmpty());
    }

    @Test
    public void requiredNotFound() {
        try {
            unitilsConfiguration.getStringList("xxx", true);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }

    @Test
    public void valuesAreTrimmed() {
        List<String> result = unitilsConfiguration.getStringList("propertyWithSpaces");
        assertLenientEquals(asList("test1", "test2"), result);
    }

    @Test
    public void emptyValuesAreIgnored() {
        List<String> result = unitilsConfiguration.getStringList("propertyWithEmptyValues");
        assertLenientEquals(asList("test1", "test2"), result);
    }

    @Test
    public void notRequiredOnlyEmptyValues() {
        List<String> result = unitilsConfiguration.getStringList("propertyWithOnlyEmptyValues");
        assertTrue(result.isEmpty());
    }

    @Test
    public void requiredOnlyEmptyValues() {
        try {
            unitilsConfiguration.getStringList("propertyWithOnlyEmptyValues", true);
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            //expected
        }
    }
}
