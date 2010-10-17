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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DatabaseTestUtils.createDatabaseMetaData;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Simple test to see if the tableExists works as intended.
 *
 * @author Jeroen Horemans
 */

public class DatabaseMetaDataGetTableNamesTest extends UnitilsJUnit4 {

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
    public void tableNames() {
        Set<String> result = databaseMetaData.getTableNames("PUBLIC");
        assertReflectionEquals(asList("TEST"), result);
    }

    @Test
    public void schemaNotFound() {
        Set<String> result = databaseMetaData.getTableNames("XXXX");
        Assert.assertTrue(result.isEmpty());
    }


    private void createTestTables() {
        executeUpdate("create table test (col1 varchar(100) not null, col2 integer not null, col3 timestamp, primary key (col1, col2))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
    }

}
