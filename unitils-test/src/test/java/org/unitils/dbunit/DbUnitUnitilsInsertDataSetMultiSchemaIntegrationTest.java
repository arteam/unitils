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
package org.unitils.dbunit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.unitils.database.SqlAssert.assertString;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitUnitilsInsertDataSetMultiSchemaIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() throws Exception {
        dropTestTables();
        createTestTables();
        resetDbUnitConnections();
    }

    @After
    public void tearDown() throws Exception {
        dropTestTables();
    }


    @Test
    public void multiSchema() throws Exception {
        DbUnitUnitils.insertDataSet(DbUnitUnitilsInsertDataSetMultiSchemaIntegrationTest.class, "DbUnitUnitilsInsertDataSetMultiSchemaIntegrationTest.xml");

        assertString("111", "select dataset from public.test");
        assertString("222", "select dataset from schema_a.test");
        assertString("333", "select dataset from schema_b.test");
    }

    @Test
    public void multiSchemaNoDefaultNamespace() throws Exception {
        DbUnitUnitils.insertDataSet(DbUnitUnitilsInsertDataSetMultiSchemaIntegrationTest.class, "DbUnitUnitilsInsertDataSetMultiSchemaIntegrationTest-noDefaultNamespace.xml");

        assertString("111", "select dataset from public.test");
        assertString("222", "select dataset from schema_a.test");
        assertString("333", "select dataset from schema_b.test");
    }


    private void createTestTables() {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST(dataset varchar(100))");
        // SCHEMA_A
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA");
        executeUpdate("create table SCHEMA_A.TEST(dataset varchar(100))");
        // SCHEMA_B
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA");
        executeUpdate("create table SCHEMA_B.TEST(dataset varchar(100))");
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST");
        executeUpdateQuietly("drop table SCHEMA_A.TEST");
        executeUpdateQuietly("drop schema SCHEMA_A");
        executeUpdateQuietly("drop table SCHEMA_B.TEST");
        executeUpdateQuietly("drop schema SCHEMA_B");
    }
}
