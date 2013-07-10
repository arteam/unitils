/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dbunit.dataset;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.dbunit.dataset.datatype.DataType.NUMERIC;
import static org.dbunit.dataset.datatype.DataType.VARCHAR;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

/**
 * @author Tim Ducheyne
 */
public class SchemaFactoryCreateSchemaForDbUnitDataSetTest extends UnitilsJUnit4 {

    /* Tested object */
    private SchemaFactory schemaFactory;

    private Mock<IDataSet> dataSetMock;
    private Mock<ITableIterator> tableIteratorMock;
    private Mock<ITable> tableMock1;
    private Mock<ITableMetaData> tableMetaDataMock1;
    private Mock<ITable> tableMock2;
    private Mock<ITableMetaData> tableMetaDataMock2;
    private Mock<ITable> tableMock3;
    private Mock<ITableMetaData> tableMetaDataMock3;


    @Before
    public void initialize() throws Exception {
        schemaFactory = new SchemaFactory();

        Column pk1 = new Column("pk1", VARCHAR);
        Column pk2 = new Column("pk2", NUMERIC);
        Column column1 = new Column("column1", VARCHAR);
        Column column2 = new Column("column2", VARCHAR);
        Column column3 = new Column("column3", VARCHAR);

        dataSetMock.returns(tableIteratorMock).iterator();

        tableMock1.returns(tableMetaDataMock1).getTableMetaData();
        tableMetaDataMock1.returns("table1").getTableName();
        tableMetaDataMock1.returns(new Column[]{pk1, pk2}).getPrimaryKeys();
        tableMetaDataMock1.returns(new Column[]{pk1, pk2, column1, column2}).getColumns();

        tableMock2.returns(tableMetaDataMock2).getTableMetaData();
        tableMetaDataMock2.returns("table2").getTableName();
        tableMetaDataMock2.returns(new Column[]{column3}).getColumns();

        tableMock3.returns(tableMetaDataMock3).getTableMetaData();
        tableMetaDataMock3.returns("table3").getTableName();
    }


    @Test
    public void createSchemaForDbUnitDataSet() throws Exception {
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock1).getTable();
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock2).getTable();
        tableIteratorMock.onceReturns(false).next();

        tableMock1.onceReturns(1).getRowCount();
        tableMock1.returns("aa").getValue(0, "pk1");
        tableMock1.returns(11).getValue(0, "pk2");
        tableMock1.returns("cc").getValue(0, "column1");
        tableMock1.returns("dd").getValue(0, "column2");
        tableMock2.onceReturns(2).getRowCount();
        tableMock2.returns("ee").getValue(0, "column3");
        tableMock2.returns("ff").getValue(1, "column3");

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock());
        assertEquals("name", result.getName());
        assertPropertyReflectionEquals("name", asList("table1", "table2"), result.getTables());

        Table table1 = result.getTable("table1");
        assertEquals("table1", table1.getName());
        List<Row> table1Rows = table1.getRows();
        assertEquals(1, table1Rows.size());
        assertPropertyReflectionEquals("name", asList("pk1", "pk2"), table1Rows.get(0).getPrimaryKeyColumns());
        assertPropertyReflectionEquals("name", asList("column1", "column2"), table1Rows.get(0).getColumns());
        assertEquals("pk1", table1Rows.get(0).getColumn("pk1").getName());
        assertEquals(VARCHAR, table1Rows.get(0).getColumn("pk1").getType());
        assertEquals("aa", table1Rows.get(0).getColumn("pk1").getValue());
        assertEquals("pk2", table1Rows.get(0).getColumn("pk2").getName());
        assertEquals(NUMERIC, table1Rows.get(0).getColumn("pk2").getType());
        assertEquals(11, table1Rows.get(0).getColumn("pk2").getValue());
        assertEquals("column1", table1Rows.get(0).getColumn("column1").getName());
        assertEquals(VARCHAR, table1Rows.get(0).getColumn("column1").getType());
        assertEquals("cc", table1Rows.get(0).getColumn("column1").getValue());
        assertEquals("column2", table1Rows.get(0).getColumn("column2").getName());
        assertEquals(VARCHAR, table1Rows.get(0).getColumn("column2").getType());
        assertEquals("dd", table1Rows.get(0).getColumn("column2").getValue());

        Table table2 = result.getTable("table2");
        assertEquals("table2", table2.getName());
        List<Row> table2Rows = table2.getRows();
        assertEquals(2, table2Rows.size());
        assertTrue(table2Rows.get(0).getPrimaryKeyColumns().isEmpty());
        assertPropertyReflectionEquals("name", asList("column3"), table2Rows.get(0).getColumns());
        assertEquals("column3", table2Rows.get(0).getColumn("column3").getName());
        assertEquals(VARCHAR, table2Rows.get(0).getColumn("column3").getType());
        assertEquals("ee", table2Rows.get(0).getColumn("column3").getValue());
        assertTrue(table2Rows.get(1).getPrimaryKeyColumns().isEmpty());
        assertPropertyReflectionEquals("name", asList("column3"), table2Rows.get(1).getColumns());
        assertEquals("column3", table2Rows.get(1).getColumn("column3").getName());
        assertEquals(VARCHAR, table2Rows.get(1).getColumn("column3").getType());
        assertEquals("ff", table2Rows.get(1).getColumn("column3").getValue());
    }

    @Test
    public void onlyIncludedTables() throws Exception {
        // todo fix mocking framework => this does not seem to work

        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock1).getTable();
        tableIteratorMock.onceReturns(tableMetaDataMock1).getTableMetaData();
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock3).getTable();
        tableIteratorMock.onceReturns(tableMetaDataMock3).getTableMetaData();
        tableIteratorMock.returns(tableMock3).getTable();
        tableIteratorMock.onceReturns(false).next();

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock(), asList("TABLE3"));
        assertPropertyReflectionEquals("name", asList("table3"), result.getTables());
    }

    @Test
    public void allTablesWhenNullTablesToInclude() throws Exception {
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock1).getTable();
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock2).getTable();
        tableIteratorMock.onceReturns(false).next();

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock(), null);
        assertPropertyReflectionEquals("name", asList("table1", "table2"), result.getTables());
    }


    @Test
    public void allTablesWhenEmptyTablesToInclude() throws Exception {
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock1).getTable();
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock2).getTable();
        tableIteratorMock.onceReturns(false).next();

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock(), Collections.<String>emptyList());
        assertPropertyReflectionEquals("name", asList("table1", "table2"), result.getTables());
    }

    @Test
    public void noRows() throws Exception {
        tableIteratorMock.onceReturns(true).next();
        tableIteratorMock.onceReturns(tableMock3).getTable();
        tableIteratorMock.onceReturns(false).next();

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock());
        assertTrue(result.getTable("table3").isEmpty());
    }

    @Test
    public void emptySchemaWhenNoTables() throws Exception {
        dataSetMock.returns(false).iterator().next();

        Schema result = schemaFactory.createSchemaForDbUnitDataSet("name", dataSetMock.getMock());
        assertTrue(result.getTables().isEmpty());
    }

    @Test
    public void exceptionWhenFailure() throws Exception {
        tableIteratorMock.raises(new NullPointerException("expected")).next();
        try {
            schemaFactory.createSchemaForDbUnitDataSet("schemaName", dataSetMock.getMock());
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create data set for schema schemaName\n" +
                    "Reason: NullPointerException: expected", e.getMessage());
        }

    }
}