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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.unitilsnew.core.config.UserPropertiesFactory.DEFAULT_LOCAL_PROPERTIES_NAME;
import static org.unitilsnew.core.config.UserPropertiesFactory.DEFAULT_UNITILS_PROPERTIES_NAME;

/**
 * @author Tim Ducheyne
 * @author Fabian Krueger
 * @author Filip Neven
 */
public class UserPropertiesFactoryPropertyTest extends UnitilsJUnit4 {

    /* Tested object */
    private UserPropertiesFactory userPropertiesFactory;

    private Mock<PropertiesReader> propertiesReader;

    private Properties unitilsProperties;
    private Properties localProperties;


    @Before
    public void initialize() {
        userPropertiesFactory = new UserPropertiesFactory(propertiesReader.getMock());

        localProperties = new Properties();
        unitilsProperties = new Properties();

        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);
    }

    @Before
    public void cleanup() {
        System.clearProperty("key");
        System.clearProperty("property");
    }


    @Test
    public void unitilsPropertiesAreOverriddenByLocalProperties() {
        unitilsProperties.put("key", "unitils");
        localProperties.put("key", "local");

        Properties result = userPropertiesFactory.create();
        assertEquals("local", result.get("key"));
    }

    @Test
    public void unitilsPropertiesAreOverriddenBySystemProperties() {
        System.setProperty("key", "system");
        unitilsProperties.put("key", "unitils");

        Properties result = userPropertiesFactory.create();
        assertEquals("system", result.get("key"));
    }

    @Test
    public void localPropertiesAreOverriddenBySystemProperties() {
        System.setProperty("key", "system");
        localProperties.put("key", "local");

        Properties result = userPropertiesFactory.create();
        assertEquals("system", result.get("key"));
    }

    @Test
    public void propertiesAreExpanded() {
        unitilsProperties.put("key", "${property}");
        unitilsProperties.put("property", "value");

        Properties result = userPropertiesFactory.create();
        assertEquals("value", result.get("key"));
    }

    @Test
    public void expandedTwice() {
        unitilsProperties.put("key", "${property1}");
        unitilsProperties.put("property1", "${property2}");
        unitilsProperties.put("property2", "value");

        Properties result = userPropertiesFactory.create();
        assertEquals("value", result.get("key"));
    }

    @Test
    public void expandedAfterAllPropertiesAreLoaded() {
        unitilsProperties.put("key", "${property}");
        localProperties.put("property", "local");
        System.setProperty("property", "system");

        Properties result = userPropertiesFactory.create();
        assertEquals("system", result.get("key"));
    }

    @Test(expected = UnitilsException.class)
    public void cyclicExpansion() {
        unitilsProperties.put("xxx", "${yyy}");
        unitilsProperties.put("yyy", "${xxx}");

        userPropertiesFactory.create();
    }
}