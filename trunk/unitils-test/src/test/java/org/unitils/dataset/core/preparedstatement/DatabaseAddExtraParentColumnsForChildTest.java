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
package org.unitils.dataset.core.preparedstatement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dataset.core.Row;
import org.unitils.dataset.core.Schema;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.Database;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.dataset.loader.impl.TestDataFactory.createColumn;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAddExtraParentColumnsForChildTest extends UnitilsJUnit4 {

    /* Tested object */
    private Database database;

    @TestDataSource
    private DataSource dataSource;

    private Row parentRow;
    private Row childRow;
    private Row parentToParentRow;


    @Before
    public void initialize() throws SQLException {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        DbSupport dbSupport = getDefaultDbSupport(configuration, sqlHandler);
        database = new Database(dbSupport);

        dropTestTables();
        createTestTables();
    }

    @Before
    public void initializeTestData() {
        parentRow = new Row();
        childRow = new Row(parentRow, false);
        parentToParentRow = new Row(parentRow, false);

        Table parentTable = new Table("parent", false);
        parentTable.addRow(parentRow);
        parentTable.addRow(parentToParentRow);

        Table childTable = new Table("child", false);
        childTable.addRow(childRow);

        Schema schema = new Schema("public", false);
        schema.addTable(parentTable);
        schema.addTable(childTable);
    }

    @After
    public void cleanup() throws Exception {
        dropTestTables();
    }


    @Test
    public void addExtraParentColumnsForChild() throws Exception {
        parentRow.addColumn(createColumn("pk1", "1"));
        parentRow.addColumn(createColumn("pk2", "2"));

        database.addExtraParentColumnsForChild(childRow);
        assertEquals("1", childRow.getColumn("FK1").getValue());
        assertEquals("2", childRow.getColumn("FK2").getValue());
    }

    @Test
    public void fkColumnNotFoundInParent() throws Exception {
        try {
            parentRow.addColumn(createColumn("pk2", "2"));
            database.addExtraParentColumnsForChild(childRow);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            assertExceptionContainsColumnNames(e, "PK1", "FK1");
        }
    }

    @Test
    public void overridingValueOfChild() throws Exception {
        parentRow.addColumn(createColumn("pk1", "1"));
        parentRow.addColumn(createColumn("pk2", "2"));
        childRow.addColumn(createColumn("fk1", "9999"));

        database.addExtraParentColumnsForChild(childRow);
        assertEquals("1", childRow.getColumn("FK1").getValue());
        assertEquals("2", childRow.getColumn("FK2").getValue());
    }

    @Test
    public void noForeignKeysFound() throws Exception {
        try {
            database.addExtraParentColumnsForChild(parentToParentRow);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("No foreign key relationship found"));
        }
    }

    private void createTestTables() {
        executeUpdate("create table parent (pk1 integer not null, pk2 integer not null, parentColumn varchar(100), primary key (pk1, pk2))", dataSource);
        executeUpdate("create table child (pk integer not null primary key, childColumn varchar(100), fk1 integer, fk2 integer, foreign key (fk1, fk2) references parent(pk1, pk2))", dataSource);
    }

    private void dropTestTables() {
        executeUpdateQuietly("drop table child", dataSource);
        executeUpdateQuietly("drop table parent", dataSource);
    }

    private void assertExceptionContainsColumnNames(UnitilsException e, String... columnNames) {
        String message = e.getMessage();
        for (String columnName : columnNames) {
            assertTrue("Exception did not contain column name: " + columnName + ". Message: " + message, message.contains(columnName));
        }
    }

}