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

package org.unitilsnew.database.config;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.config.Configuration;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class DatabaseConfigurationsFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseConfigurationsFactory databaseConfigurationsFactory;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        databaseConfigurationsFactory = new DatabaseConfigurationsFactory(configurationMock.getMock());
    }


    @Test
    public void databaseNamesSpecified() {
        configurationMock.returns(asList("a", "b")).getOptionalStringList("database.names");
        configurationMock.returns("driverA").getOptionalString("database.driverClassName", "a");
        configurationMock.returns("urlA").getOptionalString("database.url", "a");
        configurationMock.returns("userA").getOptionalString("database.userName", "a");
        configurationMock.returns("passA").getOptionalString("database.password", "a");
        configurationMock.returns("dialectA").getOptionalString("database.dialect", "a");
        configurationMock.returns(asList("schema1", "schema2")).getOptionalStringList("database.schemaNames", "a");
        configurationMock.returns(false).getOptionalString("database.updateDisabled", "a");
        configurationMock.returns("driverB").getOptionalString("database.driverClassName", "b");
        configurationMock.returns("urlB").getOptionalString("database.url", "b");
        configurationMock.returns("userB").getOptionalString("database.userName", "b");
        configurationMock.returns("passB").getOptionalString("database.password", "b");
        configurationMock.returns("dialectB").getOptionalString("database.dialect", "b");
        configurationMock.returns(Collections.<String>emptyList()).getOptionalStringList("database.schemaNames", "b");
        configurationMock.returns(true).getOptionalBoolean("database.updateDisabled", "b");

        DatabaseConfigurations result = databaseConfigurationsFactory.create();

        assertLenientEquals(asList("a", "b"), result.getDatabaseNames());
        DatabaseConfiguration databaseConfigurationA = result.getDatabaseConfiguration("a");
        assertEquals("a", databaseConfigurationA.getDatabaseName());
        assertEquals("driverA", databaseConfigurationA.getDriverClassName());
        assertEquals("urlA", databaseConfigurationA.getUrl());
        assertEquals("userA", databaseConfigurationA.getUserName());
        assertEquals("passA", databaseConfigurationA.getPassword());
        assertEquals("dialectA", databaseConfigurationA.getDialect());
        assertEquals(asList("schema1", "schema2"), databaseConfigurationA.getSchemaNames());
        assertTrue(databaseConfigurationA.isDefaultDatabase());
        assertFalse(databaseConfigurationA.isDisabled());

        DatabaseConfiguration databaseConfigurationB = result.getDatabaseConfiguration("b");
        assertEquals("b", databaseConfigurationB.getDatabaseName());
        assertEquals("driverB", databaseConfigurationB.getDriverClassName());
        assertEquals("urlB", databaseConfigurationB.getUrl());
        assertEquals("userB", databaseConfigurationB.getUserName());
        assertEquals("passB", databaseConfigurationB.getPassword());
        assertEquals("dialectB", databaseConfigurationB.getDialect());
        assertTrue(databaseConfigurationB.getSchemaNames().isEmpty());
        assertFalse(databaseConfigurationB.isDefaultDatabase());
        assertTrue(databaseConfigurationB.isDisabled());
    }

    @Test
    public void firstNamedDatabaseIsDefaultDatabase() {
        configurationMock.returns(asList("a", "b")).getOptionalStringList("database.names");

        DatabaseConfigurations result = databaseConfigurationsFactory.create();

        DatabaseConfiguration databaseConfigurationA = result.getDatabaseConfiguration("a");
        assertTrue(databaseConfigurationA.isDefaultDatabase());
        assertEquals(databaseConfigurationA, result.getDatabaseConfiguration(null));

        DatabaseConfiguration databaseConfigurationB = result.getDatabaseConfiguration("b");
        assertFalse(databaseConfigurationB.isDefaultDatabase());
    }

    @Test
    public void noDatabaseNamesSpecified() {
        configurationMock.returns("driverA").getOptionalString("database.driverClassName");
        configurationMock.returns("urlA").getOptionalString("database.url");
        configurationMock.returns("userA").getOptionalString("database.userName");
        configurationMock.returns("passA").getOptionalString("database.password");
        configurationMock.returns("dialectA").getOptionalString("database.dialect");
        configurationMock.returns(asList("schema1", "schema2")).getOptionalStringList("database.schemaNames");
        configurationMock.returns(false).getOptionalString("database.updateDisabled");

        DatabaseConfigurations result = databaseConfigurationsFactory.create();

        assertTrue(result.getDatabaseNames().isEmpty());
        DatabaseConfiguration databaseConfigurationA = result.getDatabaseConfiguration(null);
        assertNull(databaseConfigurationA.getDatabaseName());
        assertEquals("driverA", databaseConfigurationA.getDriverClassName());
        assertEquals("urlA", databaseConfigurationA.getUrl());
        assertEquals("userA", databaseConfigurationA.getUserName());
        assertEquals("passA", databaseConfigurationA.getPassword());
        assertEquals("dialectA", databaseConfigurationA.getDialect());
        assertEquals(asList("schema1", "schema2"), databaseConfigurationA.getSchemaNames());
        assertTrue(databaseConfigurationA.isDefaultDatabase());
        assertFalse(databaseConfigurationA.isDisabled());
    }

    @Test
    public void exceptionWhenDefaultDatabaseIsDisabled() {
        configurationMock.returns(true).getOptionalBoolean("database.updateDisabled");
        try {
            databaseConfigurationsFactory.create();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create database configuration. Default database cannot be disabled.", e.getMessage());
        }
    }
}
