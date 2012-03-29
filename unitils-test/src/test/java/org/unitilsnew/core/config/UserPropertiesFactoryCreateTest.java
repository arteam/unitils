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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitilsnew.core.config.UserPropertiesFactory.*;

/**
 * @author Tim Ducheyne
 * @author Fabian Krueger
 * @author Filip Neven
 */
public class UserPropertiesFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private UserPropertiesFactory userPropertiesFactory;

    private Mock<PropertiesReader> propertiesReader;

    private Properties unitilsProperties;
    private Properties localProperties;


    @Before
    public void initialize() {
        userPropertiesFactory = new UserPropertiesFactory(propertiesReader.getMock());

        unitilsProperties = new Properties();
        unitilsProperties.put("unitils-properties", "value");
        localProperties = new Properties();
        localProperties.put("local-properties", "value");
    }


    @After
    public void cleanup() {
        System.clearProperty(UNITILS_PROPERTIES_NAME_PROPERTY);
        System.clearProperty(LOCAL_PROPERTIES_NAME_PROPERTY);
    }


    @Test
    public void allPropertiesFound() {
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("unitils-properties"));
        assertTrue(result.containsKey("local-properties"));
        assertTrue(result.containsKey("user.home"));
    }

    @Test
    public void ignoreWhenUnitilsPropertiesNotFound() {
        propertiesReader.returns(null).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);

        Properties result = userPropertiesFactory.create();

        assertFalse(result.containsKey("unitils-properties"));
        assertTrue(result.containsKey("local-properties"));
        assertTrue(result.containsKey("user.home"));
    }

    @Test
    public void tryClassPathWhenLocalPropertiesNotFoundInClasspath() {
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(null).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromClasspath(DEFAULT_LOCAL_PROPERTIES_NAME);

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("unitils-properties"));
        assertTrue(result.containsKey("local-properties"));
        assertTrue(result.containsKey("user.home"));
    }

    @Test
    public void ignoreWhenNoLocalPropertiesFound() {
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(null).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);
        propertiesReader.returns(null).loadPropertiesFromClasspath(DEFAULT_LOCAL_PROPERTIES_NAME);

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("unitils-properties"));
        assertFalse(result.containsKey("local-properties"));
        assertTrue(result.containsKey("user.home"));
    }

    @Test
    public void noPropertiesFound() {
        propertiesReader.returns(null).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(null).loadPropertiesFromUserHome(DEFAULT_LOCAL_PROPERTIES_NAME);
        propertiesReader.returns(null).loadPropertiesFromClasspath(DEFAULT_LOCAL_PROPERTIES_NAME);

        Properties result = userPropertiesFactory.create();

        assertFalse(result.containsKey("unitils-properties"));
        assertFalse(result.containsKey("local-properties"));
        assertTrue(result.containsKey("user.home"));
    }


    @Test
    public void unitilsPropertiesNameOverriddenBySystemProperty() {
        System.setProperty(UNITILS_PROPERTIES_NAME_PROPERTY, "override");
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath("override");

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("unitils-properties"));
    }

    @Test
    public void localPropertiesNameOverriddenBySystemProperty() {
        System.setProperty(LOCAL_PROPERTIES_NAME_PROPERTY, "override");
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome("override");

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("local-properties"));
    }

    @Test
    public void localPropertiesNameOverriddenByUnitilsProperty() {
        unitilsProperties.setProperty(LOCAL_PROPERTIES_NAME_PROPERTY, "override");
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome("override");

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("local-properties"));
    }

    @Test
    public void systemPropertyUsedWhenLocalPropertiesNameOverriddenByBothSystemAndUnitilsProperty() {
        System.setProperty(LOCAL_PROPERTIES_NAME_PROPERTY, "override-system");
        unitilsProperties.setProperty(LOCAL_PROPERTIES_NAME_PROPERTY, "override-unitils");
        propertiesReader.returns(unitilsProperties).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        propertiesReader.returns(localProperties).loadPropertiesFromUserHome("override-system");

        Properties result = userPropertiesFactory.create();

        assertTrue(result.containsKey("local-properties"));
    }

    @Test(expected = UnitilsException.class)
    public void loadException() {
        propertiesReader.raises(IOException.class).loadPropertiesFromClasspath(DEFAULT_UNITILS_PROPERTIES_NAME);
        userPropertiesFactory.create();
    }
}
