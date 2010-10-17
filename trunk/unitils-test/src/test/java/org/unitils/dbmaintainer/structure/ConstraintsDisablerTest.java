/*
 * Copyright Unitils.org
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
package org.unitils.dbmaintainer.structure;

import org.dbmaintain.database.Database;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;

import static org.junit.Assert.fail;
import static org.unitils.database.DatabaseUnitils.disableConstraints;
import static org.unitils.database.DatabaseUnitils.getDefaultDatabase;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;

/**
 * Test class for the ConstraintsDisabler. This test is independent of the dbms that is used. The database dialect that
 * is tested depends on the configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ConstraintsDisablerTest extends UnitilsJUnit4 {

    protected Database defaultDatabase;
    protected DataSource dataSource;


    /**
     * Test fixture. Configures the ConstraintsDisabler with the implementation that matches the configured database
     * dialect
     */
    @Before
    public void setUp() throws Exception {
        defaultDatabase = getDefaultDatabase();
        dataSource = defaultDatabase.getDataSource();

        cleanupTestDatabase();
        createTestTables();
    }


    /**
     * Drops the test tables, to avoid influencing other tests
     */
    @After
    public void tearDown() throws Exception {
        cleanupTestDatabase();
    }


    /**
     * Tests whether foreign key constraints are correctly disabled
     */
    @Test
    public void testDisableConstraints_foreignKey() throws Exception {
        try {
            executeUpdate("insert into table2 (col1) values ('test')", dataSource);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected foreign key violation
        }
        disableConstraints();
        // Should not throw exception anymore
        executeUpdate("insert into table2 (col1) values ('test')", dataSource);
    }


    /**
     * Tests whether foreign key constraints are disabled before the alternate keys. Otherwise the disabling of
     * the alternate key will result in an error (issue UNI-36).
     */
    @Test
    public void testDisableConstraints_foreignKeyToAlternateKey() throws Exception {
        try {
            executeUpdate("insert into table3 (col1) values ('test')", dataSource);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected foreign key violation
        }
        disableConstraints();
        // Should not throw exception anymore
        executeUpdate("insert into table3 (col1) values ('test')", dataSource);
    }


    /**
     * Tests whether not-null constraints are correctly disabled
     */
    @Test
    public void testDisableConstraints_notNull() throws Exception {
        try {
            executeUpdate("insert into table1 (col1, col2) values ('test', null)", dataSource);
            fail("UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected not null violation
        }
        disableConstraints();
        // Should not throw exception anymore
        executeUpdate("insert into table1 (col1, col2) values ('test', null)", dataSource);
    }


    /**
     * Creates the test tables
     */
    protected void createTestTables() {
        executeUpdate("create table table1 (col1 varchar(10) not null primary key, col2 varchar(10) not null, unique (col2))", dataSource);
        executeUpdate("create table table2 (col1 varchar(10), foreign key (col1) references table1(col1))", dataSource);
        executeUpdate("create table table3 (col1 varchar(10), foreign key (col1) references table1(col2))", dataSource);
    }


    /**
     * Drops the test tables
     */
    protected void cleanupTestDatabase() {
        executeUpdateQuietly("drop table table3", dataSource);
        executeUpdateQuietly("drop table table2", dataSource);
        executeUpdateQuietly("drop table table1", dataSource);
    }

}
