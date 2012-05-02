/*
 * Copyright 2008,  Unitils.org
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.util.PropertyUtils;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.unitils.database.SQLUnitils.*;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT;

/**
 * Test class for loading of data sets in mutliple database schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbUnitModuleDataSetMultiSchemaTest extends UnitilsJUnit4 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbUnitModuleDataSetMultiSchemaTest.class);

    /* Tested object */
    private DbUnitModule dbUnitModule;

    /* The dataSource */
    @TestDataSource
    private DataSource dataSource = null;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }
        dbUnitModule = new DbUnitModule();
        dbUnitModule.init(configuration);

        dropTestTables();
        createTestTables();
    }


    /**
     * Clean-up test database.
     */
    @After
    public void tearDown() throws Exception {
        if (disabled) {
            return;
        }
        dropTestTables();
    }


    /**
     * Test for a data set containing multiple namespaces.
     */
    @Test
    public void testDataSet_multiSchema() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("multiSchema"), new DataSetTest());

        assertLoadedDataSet("PUBLIC");
        assertLoadedDataSet("SCHEMA_A");
        assertLoadedDataSet("SCHEMA_B");
    }

    /**
     * Test for a data set containing multiple namespaces.
     */
    @Test
    public void testDataSet_multiSchemaNoDefault() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        dbUnitModule.insertDataSet(DataSetTest.class.getMethod("multiSchemaNoDefault"), new DataSetTest());

        assertLoadedDataSet("PUBLIC");
        assertLoadedDataSet("SCHEMA_A");
        assertLoadedDataSet("SCHEMA_B");
    }


    /**
     * Utility method to assert that the data set for the schema was loaded.
     *
     * @param schemaName the name of the schema, not null
     */
    private void assertLoadedDataSet(String schemaName) {
        String dataSet = getItemAsString("select dataset from " + schemaName + ".test", dataSource);
        assertEquals(schemaName, dataSet);
    }


    /**
     * Creates the test tables.
     */
    private void createTestTables() {
        // PUBLIC SCHEMA
        executeUpdate("create table TEST(dataset varchar(100))", dataSource);
        // SCHEMA_A
        executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_A.TEST(dataset varchar(100))", dataSource);
        // SCHEMA_B
        executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        executeUpdate("create table SCHEMA_B.TEST(dataset varchar(100))", dataSource);
    }


    /**
     * Removes the test database tables
     */
    private void dropTestTables() {
        executeUpdateQuietly("drop table TEST", dataSource);
        executeUpdateQuietly("drop table SCHEMA_A.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        executeUpdateQuietly("drop table SCHEMA_B.TEST", dataSource);
        executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class DataSetTest {

        @DataSet("DbUnitModuleDataSetMultiSchemaTest$DataSetTest.multiSchema.xml")
        public void multiSchema() {
        }

        @DataSet("DbUnitModuleDataSetMultiSchemaTest$DataSetTest.multiSchemaNoDefault.xml")
        public void multiSchemaNoDefault() {
        }
    }

}
