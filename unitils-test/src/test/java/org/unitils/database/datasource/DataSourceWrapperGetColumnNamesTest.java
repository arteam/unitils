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
import org.unitils.dataset.model.database.Column;

import javax.sql.DataSource;
import java.util.Set;

import static java.sql.Types.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.DatabaseUnitils.getUnitilsDataSource;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DataSetTestUtils.createIdentifierProcessor;
import static org.unitils.dataset.util.DataSetTestUtils.createTableName;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapperGetColumnNamesTest extends UnitilsJUnit4 {

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
    public void getColumns() throws Exception {
        Set<Column> columnNames = dataSourceWrapper.getColumns(createTableName("PUBLIC", "TEST"));
        assertPropertyLenientEquals("name", asList("COL1", "COL2", "COL3"), columnNames);
        assertPropertyLenientEquals("sqlType", asList(VARCHAR, INTEGER, TIMESTAMP), columnNames);
    }

    @Test
    public void caseSensitive() throws Exception {
        Set<Column> columnNames = dataSourceWrapper.getColumns(createTableName("PUBLIC", "TestCase"));
        assertPropertyLenientEquals("name", asList("Col1", "col2", "COL3"), columnNames);
    }

    @Test
    public void columnsCached() throws Exception {
        dataSourceWrapper.getColumns(createTableName("PUBLIC", "TEST"));
        dropTestTables();
        Set<Column> columnNames = dataSourceWrapper.getColumns(createTableName("PUBLIC", "TEST"));
        assertPropertyLenientEquals("name", asList("COL1", "COL2", "COL3"), columnNames);
    }

    @Test
    public void tableNotFound() throws Exception {
        Set<Column> columnNames = dataSourceWrapper.getColumns(createTableName("xxxx", "xxxx"));
        assertTrue(columnNames.isEmpty());
    }
}