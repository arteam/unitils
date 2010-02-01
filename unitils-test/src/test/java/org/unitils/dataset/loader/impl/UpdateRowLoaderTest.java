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
package org.unitils.dataset.loader.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.core.Row;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.*;
import static org.unitils.dataset.loader.impl.TestDataFactory.*;
import static org.unitils.mock.ArgumentMatchers.anyInt;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdateRowLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private UpdateRowLoader updatePreparedStatement = new UpdateRowLoader();

    private PartialMock<Database> database;
    private Mock<Connection> connection;
    private Mock<PreparedStatement> preparedStatement;
    private Mock<ParameterMetaData> parameterMetaData;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws SQLException {
        database.returns(connection).createConnection();
        connection.returns(preparedStatement).prepareStatement(null);

        preparedStatement.returns(1).executeUpdate();
        preparedStatement.returns(parameterMetaData).getParameterMetaData();
        parameterMetaData.returns(VARCHAR).getParameterType(anyInt());

        NameProcessor nameProcessor = new NameProcessor("'");
        ColumnProcessor columnProcessor = new ColumnProcessor('=', '$', nameProcessor);
        updatePreparedStatement.init(columnProcessor, nameProcessor, database.getMock());
    }

    @Before
    public void initializePrimaryKeys() throws SQLException {
        database.returns(asSet("PK1", "Pk2")).getPrimaryKeyColumnNames(null);
    }


    @Test
    public void executeUpdate() throws Exception {
        updatePreparedStatement.loadRow(createRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=?, column_2=?, pk1=?, pk2=? where pk1=?, pk2=?");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().setObject(5, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(6, "4", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test(expected = UnitilsException.class)
    public void noRecordWasUpdated() throws Exception {
        preparedStatement.onceReturns(0).executeUpdate();
        updatePreparedStatement.loadRow(createRow(), emptyVariables);
    }

    @Test
    public void literalColumns() throws Exception {
        updatePreparedStatement.loadRow(createRowWithLiteralValues(), emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=literal1, column_2=literal2, pk1=3, pk2=4 where pk1=3, pk2=4");
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void caseSensitive() throws Exception {
        updatePreparedStatement.loadRow(createCaseSensitiveRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("update 'my_schema'.'table_a' set 'column_1'=?, 'column_2'=?, 'PK1'=?, 'Pk2'=? where 'PK1'=?, 'Pk2'=?");
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void noValuesForPkColumns() throws Exception {
        try {
            Row row = createRow(createColumn("column_1", "1"));
            updatePreparedStatement.loadRow(row, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionContainsPkColumnNames(e, "PK1", "Pk2");
        }
    }

    @Test
    public void noValueForOneOfPkColumns() throws Exception {
        try {
            Row row = createRow(createColumn("column_1", "1"), createColumn("pk1", "3"));
            updatePreparedStatement.loadRow(row, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionNotContainsPkColumnNames(e, "PK1");
            assertExceptionContainsPkColumnNames(e, "Pk2");
        }
    }

    @Test
    public void onlyValuesForPkColumns() throws Exception {
        Row row = createRow(createColumn("pk1", "1"), createColumn("pk2", "2"));

        updatePreparedStatement.loadRow(row, emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set pk1=?, pk2=? where pk1=?, pk2=?");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "2", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }


    private void assertExceptionContainsPkColumnNames(UnitilsException e, String... pkColumnNames) {
        String message = e.getCause().getMessage();
        for (String pkColumnName : pkColumnNames) {
            assertTrue("Exception did not contain pk column name: " + pkColumnName + ". Message: " + message, message.contains(pkColumnName));
        }
    }

    private void assertExceptionNotContainsPkColumnNames(UnitilsException e, String... pkColumnNames) {
        String message = e.getCause().getMessage();
        for (String pkColumnName : pkColumnNames) {
            assertFalse("Exception did contain pk column name: " + pkColumnName + ". Message: " + message, message.contains(pkColumnName));
        }
    }

}