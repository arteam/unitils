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
package org.unitils.dataset.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UpdatePreparedStatementWrapperTest extends PreparedStatementWrapperTestBase {

    /* Tested object */
    private UpdatePreparedStatementWrapper updatePreparedStatementWrapper;


    @Before
    public void initializePrimaryKeys() throws Exception {
        initializePrimaryKeys("PK1", "Pk2");
    }


    @Test
    public void addColumn() throws Exception {
        updatePreparedStatementWrapper = new UpdatePreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        updatePreparedStatementWrapper.addColumn(createColumn("column_1", "1"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("column_2", "2"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("pk1", "3"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("pk2", "4"), emptyVariables);
        updatePreparedStatementWrapper.executeUpdate();

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=?, column_2=? where pk1=?, pk2=?");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", INTEGER);
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void addLiteralColumn() throws Exception {
        updatePreparedStatementWrapper = new UpdatePreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        updatePreparedStatementWrapper.addColumn(createColumn("column_1", "=literal1"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("column_2", "=literal2"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("pk1", "=3"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("pk2", "=4"), emptyVariables);
        updatePreparedStatementWrapper.executeUpdate();

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=literal1, column_2=literal2 where pk1=3, pk2=4");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void noValuesForPkColumns() throws Exception {
        updatePreparedStatementWrapper = new UpdatePreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        updatePreparedStatementWrapper.addColumn(createColumn("column_1", "1"), emptyVariables);
        try {
            updatePreparedStatementWrapper.executeUpdate();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionContainsPkColumnNames(e, "PK1", "Pk2");
        }
    }

    @Test
    public void noValueForOneOfPkColumns() throws Exception {
        updatePreparedStatementWrapper = new UpdatePreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        updatePreparedStatementWrapper.addColumn(createColumn("column_1", "1"), emptyVariables);
        updatePreparedStatementWrapper.addColumn(createColumn("pk1", "3"), emptyVariables);
        try {
            updatePreparedStatementWrapper.executeUpdate();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertExceptionNotContainsPkColumnNames(e, "PK1");
            assertExceptionContainsPkColumnNames(e, "Pk2");
        }
    }
}