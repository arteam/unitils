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
package org.unitils.dataset.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DatabaseTestUtils.createDatabaseMetaData;

/**
 * Simple test to see if the tableExists works as intended.
 *
 * @author Jeroen Horemans
 */

public class DatabaseMetaDataAssertTableNameExists extends UnitilsJUnit4 {

    private DatabaseMetaData databaseMetaData;

    @TestDataSource
    protected DataSource dataSource;

    @Before
    public void initialize() {
        databaseMetaData = createDatabaseMetaData();
        dropTestTables();
        createTestTables();
    }

    @After
    public void cleanup() throws Exception {
        dropTestTables();
    }


    @Test
    public void tableNameDoesNotExist() throws SQLException {
        try {
            databaseMetaData.assertTableNameExists("PUBLIC.NON_EXISTING_TABLE");
            fail("Expected UnitilsException");
        } catch (UnitilsException e) {
            assertEquals("No table with name NON_EXISTING_TABLE found in schema PUBLIC.", e.getMessage());
        }
    }

    @Test
    public void tableNameExists() throws SQLException {
        databaseMetaData.assertTableNameExists("PUBLIC.TEST");
    }

    @Test
    public void tableExistsCachedTest() throws SQLException {
        databaseMetaData.assertTableNameExists("PUBLIC.TEST");
        dropTestTables();
        databaseMetaData.assertTableNameExists("PUBLIC.TEST");
    }


    private void createTestTables() {
        executeUpdate("create table test (col1 varchar(100) not null, col2 integer not null, col3 timestamp, primary key (col1, col2))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
    }

}
