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
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

import static java.sql.Types.*;
import static org.junit.Assert.*;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.database.DatabaseMetaData.SQL_TYPE_UNKNOWN;

/**
 * Tests for getting the sql type for a column.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseMetaDataGetColumnSqlTypeTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseMetaData databaseMetaData;

    @TestDataSource
    protected DataSource dataSource;


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        DbSupport defaultDbSupport = DbSupportFactory.getDefaultDbSupport(configuration, new DefaultSQLHandler(dataSource));
        databaseMetaData = new DatabaseMetaData(defaultDbSupport, new SqlTypeHandlerRepository());
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
    public void getPrimaryKeyColumnNames() throws Exception {
        int col1Type = databaseMetaData.getColumnSqlType("PUBLIC.TEST", "COL1");
        int col2Type = databaseMetaData.getColumnSqlType("PUBLIC.TEST", "COL2");
        int col3Type = databaseMetaData.getColumnSqlType("PUBLIC.TEST", "COL3");

        assertEquals(VARCHAR, col1Type);
        assertEquals(INTEGER, col2Type);
        assertEquals(TIMESTAMP, col3Type);
    }

    @Test
    public void caseSensitive() throws Exception {
        int col1Type = databaseMetaData.getColumnSqlType("\"PUBLIC\".\"TestCase\"", "Col1");
        int col2Type = databaseMetaData.getColumnSqlType("\"PUBLIC\".\"TestCase\"", "col2");
        int col3Type = databaseMetaData.getColumnSqlType("\"PUBLIC\".\"TestCase\"", "COL3");

        assertEquals(VARCHAR, col1Type);
        assertEquals(INTEGER, col2Type);
        assertEquals(TIMESTAMP, col3Type);
    }

    @Test
    public void primaryKeySetCached() throws Exception {
        Map<String, Integer> columnSqlTypes1 = databaseMetaData.getColumnSqlTypes("PUBLIC.TEST");
        Map<String, Integer> columnSqlTypes2 = databaseMetaData.getColumnSqlTypes("PUBLIC.TEST");
        assertSame(columnSqlTypes1, columnSqlTypes2);
    }

    @Test
    public void onlyCachedForIdenticalSchemaAndTableName() throws Exception {
        Map<String, Integer> columnSqlTypes1 = databaseMetaData.getColumnSqlTypes("PUBLIC.TEST");
        Map<String, Integer> columnSqlTypes2 = databaseMetaData.getColumnSqlTypes("public.\"TEST\"");
        assertNotSame(columnSqlTypes1, columnSqlTypes2);
    }

    @Test
    public void tableNotFound() throws Exception {
        int result = databaseMetaData.getColumnSqlType("xxxx.xxxx", "xxxx");
        assertEquals(SQL_TYPE_UNKNOWN, result);
    }
}