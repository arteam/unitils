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

import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Row;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdatePreparedStatementTest extends PreparedStatementTestBase {

    /* Tested object */
    private UpdatePreparedStatement updatePreparedStatement;


    @Test
    public void executeUpdate() throws Exception {
        updatePreparedStatement = new UpdatePreparedStatement("my_schema", "table_a", connection.getMock());
        updatePreparedStatement.executeUpdate(row, emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=?, column_2=?, pk1=?, pk2=? where pk1=?, pk2=?");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().setObject(5, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(6, "4", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void literalColumns() throws Exception {
        updatePreparedStatement = new UpdatePreparedStatement("my_schema", "table_a", connection.getMock());
        updatePreparedStatement.executeUpdate(rowWithLiteralValues, emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=literal1, column_2=literal2, pk1=3, pk2=4 where pk1=3, pk2=4");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void noValuesForPkColumns() throws Exception {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        try {
            updatePreparedStatement = new UpdatePreparedStatement("my_schema", "table_a", connection.getMock());
            updatePreparedStatement.executeUpdate(row, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionContainsPkColumnNames(e, "PK1", "Pk2");
        }
    }

    @Test
    public void noValueForOneOfPkColumns() throws Exception {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        row.addColumn(createColumn("pk1", "3"));
        try {
            updatePreparedStatement = new UpdatePreparedStatement("my_schema", "table_a", connection.getMock());
            updatePreparedStatement.executeUpdate(row, emptyVariables);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionNotContainsPkColumnNames(e, "PK1");
            assertExceptionContainsPkColumnNames(e, "Pk2");
        }
    }

    @Test
    public void onlyValuesForPkColumns() throws Exception {
        Row row = new Row();
        row.addColumn(createColumn("pk1", "1"));
        row.addColumn(createColumn("pk2", "2"));

        updatePreparedStatement = new UpdatePreparedStatement("my_schema", "table_a", connection.getMock());
        updatePreparedStatement.executeUpdate(row, emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set pk1=?, pk2=? where pk1=?, pk2=?");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "2", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }
}