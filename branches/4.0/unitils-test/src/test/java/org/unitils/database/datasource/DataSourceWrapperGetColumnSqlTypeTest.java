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
package org.unitils.database.datasource;

import org.dbmaintain.database.IdentifierProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.database.DataSourceWrapper;

import javax.sql.DataSource;

import static java.sql.Types.*;
import static org.junit.Assert.assertEquals;
import static org.unitils.database.DatabaseUnitils.getUnitilsDataSource;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.database.DataSourceWrapper.SQL_TYPE_UNKNOWN;
import static org.unitils.dataset.util.DataSetTestUtils.createIdentifierProcessor;
import static org.unitils.dataset.util.DataSetTestUtils.createTableName;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapperGetColumnSqlTypeTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapper dataSourceWrapper;

    @TestDataSource
    protected DataSource dataSource;


    @Before
    public void initialize() throws Exception {
        IdentifierProcessor identifierProcessor = createIdentifierProcessor();
        dataSourceWrapper = new DataSourceWrapper(getUnitilsDataSource(), identifierProcessor);
    }

    @Before
    public void createTestTables() {
        dropTestTables();
        executeUpdate("create table test (col1 varchar(100), col2 integer, col3 timestamp)", dataSource);
        executeUpdate("create table \"TestCase\" (\"Col1\" varchar(100), \"col2\" integer, col3 timestamp)", dataSource);
    }

    @After
    public void dropTestTables() {
        executeUpdateQuietly("drop table test", dataSource);
        executeUpdateQuietly("drop table \"TestCase\"", dataSource);
    }


    @Test
    public void getSqlTypes() throws Exception {
        int col1Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TEST"), "COL1");
        int col2Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TEST"), "COL2");
        int col3Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TEST"), "COL3");

        assertEquals(VARCHAR, col1Type);
        assertEquals(INTEGER, col2Type);
        assertEquals(TIMESTAMP, col3Type);
    }

    @Test
    public void caseSensitive() throws Exception {
        int col1Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TestCase"), "Col1");
        int col2Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TestCase"), "col2");
        int col3Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TestCase"), "COL3");

        assertEquals(VARCHAR, col1Type);
        assertEquals(INTEGER, col2Type);
        assertEquals(TIMESTAMP, col3Type);
    }

    @Test
    public void columnSqlTypesSetCached() throws Exception {
        dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TEST"), "COL1");
        dropTestTables();
        int col1Type = dataSourceWrapper.getColumnSqlType(createTableName("PUBLIC", "TEST"), "COL1");
        assertEquals(VARCHAR, col1Type);
    }

    @Test
    public void tableNotFound() throws Exception {
        int result = dataSourceWrapper.getColumnSqlType(createTableName("xxxx", "xxxx"), "xxxx");
        assertEquals(SQL_TYPE_UNKNOWN, result);
    }
}