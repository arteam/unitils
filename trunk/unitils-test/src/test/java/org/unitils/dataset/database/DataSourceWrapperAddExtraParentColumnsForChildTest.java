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

import org.dbmaintain.database.IdentifierProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.model.dataset.DataSetRow;
import org.unitils.dataset.model.dataset.DataSetSettings;
import org.unitils.dataset.model.dataset.DataSetValue;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.unitils.database.DatabaseUnitils.getUnitilsDataSource;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.util.DataSetTestUtils.createIdentifierProcessor;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapperAddExtraParentColumnsForChildTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapper dataSourceWrapper;

    @TestDataSource
    protected DataSource dataSource;

    private DataSetRow parentDataSetRow;
    private DataSetRow childDataSetRow;
    private DataSetRow parentToParentDataSetRow;
    private DataSetRow caseSensitiveParentDataSetRow;
    private DataSetRow caseSensitiveChildDataSetRow;


    @Before
    public void initialize() throws SQLException {
        IdentifierProcessor identifierProcessor = createIdentifierProcessor();
        this.dataSourceWrapper = new DataSourceWrapper(getUnitilsDataSource(), identifierProcessor);

        dropTestTables();
        createTestTables();
    }

    @Before
    public void initializeTestData() {
        DataSetSettings dataSetSettings = new DataSetSettings('=', '$', false);
        parentDataSetRow = new DataSetRow("public", "parent", null, false, dataSetSettings);
        childDataSetRow = new DataSetRow("public", "child", parentDataSetRow, false, dataSetSettings);
        parentToParentDataSetRow = new DataSetRow("public", "parent", parentDataSetRow, false, dataSetSettings);

        DataSetSettings caseSensitiveDataSetSettings = new DataSetSettings('=', '$', true);
        caseSensitiveParentDataSetRow = new DataSetRow("PUBLIC", "ParentCase", null, false, caseSensitiveDataSetSettings);
        caseSensitiveChildDataSetRow = new DataSetRow("PUBLIC", "ChildCase", caseSensitiveParentDataSetRow, false, caseSensitiveDataSetSettings);
    }

    @After
    public void cleanup() throws Exception {
        dropTestTables();
    }


    @Test
    public void addExtraParentColumnsForChild() throws Exception {
        parentDataSetRow.addDataSetValue(new DataSetValue("pk1", "1"));
        parentDataSetRow.addDataSetValue(new DataSetValue("pk2", "2"));

        dataSourceWrapper.addExtraParentColumnsForChild(childDataSetRow);
        assertEquals("1", childDataSetRow.getDataSetColumn("FK1").getValue());
        assertEquals("2", childDataSetRow.getDataSetColumn("FK2").getValue());
    }

    @Test
    public void caseSensitive() throws Exception {
        caseSensitiveParentDataSetRow.addDataSetValue(new DataSetValue("pk1", "1"));
        caseSensitiveParentDataSetRow.addDataSetValue(new DataSetValue("Pk2", "2"));

        dataSourceWrapper.addExtraParentColumnsForChild(caseSensitiveChildDataSetRow);
        assertEquals("1", caseSensitiveChildDataSetRow.getDataSetColumn("fk1").getValue());
        assertEquals("2", caseSensitiveChildDataSetRow.getDataSetColumn("FK2").getValue());
    }

    @Test
    public void fkColumnNotFoundInParent() throws Exception {
        try {
            parentDataSetRow.addDataSetValue(new DataSetValue("pk2", "2"));

            dataSourceWrapper.addExtraParentColumnsForChild(childDataSetRow);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            assertExceptionContainsColumnNames(e, "PK1", "FK1");
        }
    }

    @Test
    public void overridingValueOfChild() throws Exception {
        parentDataSetRow.addDataSetValue(new DataSetValue("pk1", "1"));
        parentDataSetRow.addDataSetValue(new DataSetValue("pk2", "2"));
        childDataSetRow.addDataSetValue(new DataSetValue("fk1", "9999"));

        dataSourceWrapper.addExtraParentColumnsForChild(childDataSetRow);
        assertEquals("1", childDataSetRow.getDataSetColumn("FK1").getValue());
        assertEquals("2", childDataSetRow.getDataSetColumn("FK2").getValue());
    }

    @Test
    public void noForeignKeysFound() throws Exception {
        try {
            dataSourceWrapper.addExtraParentColumnsForChild(parentToParentDataSetRow);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("No foreign key relationship found"));
        }
    }


    private void createTestTables() {
        executeUpdate("create table parent (pk1 integer not null, pk2 integer not null, parentColumn varchar(100), primary key (pk1, pk2))", dataSource);
        executeUpdate("create table child (pk integer not null primary key, childColumn varchar(100), fk1 integer, fk2 integer, foreign key (fk1, fk2) references parent(pk1, pk2))", dataSource);
        executeUpdate("create table \"ParentCase\" (\"pk1\" integer not null, \"Pk2\" integer not null, \"parentColumn\" varchar(100), primary key (\"pk1\", \"Pk2\"))", dataSource);
        executeUpdate("create table \"ChildCase\" (\"pk\" integer not null primary key, childColumn varchar(100), \"fk1\" integer, fk2 integer, foreign key (\"fk1\", fk2) references \"ParentCase\"(\"pk1\", \"Pk2\"))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table child", dataSource);
        executeUpdateQuietly("drop table parent", dataSource);
        executeUpdateQuietly("drop table \"ChildCase\"", dataSource);
        executeUpdateQuietly("drop table \"ParentCase\"", dataSource);
    }

    private void assertExceptionContainsColumnNames(UnitilsException e, String... columnNames) {
        String message = e.getMessage();
        for (String columnName : columnNames) {
            assertTrue("Exception did not contain column name: " + columnName + ". Message: " + message, message.contains(columnName));
        }
    }

}