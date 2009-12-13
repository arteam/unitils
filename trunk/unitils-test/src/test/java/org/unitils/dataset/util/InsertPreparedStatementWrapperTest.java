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

import org.junit.Test;

import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertPreparedStatementWrapperTest extends PreparedStatementWrapperTestBase {

    /* Tested object */
    private InsertPreparedStatementWrapper insertPreparedStatementWrapper;


    @Test
    public void addColumn() throws Exception {
        insertPreparedStatementWrapper = new InsertPreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        insertPreparedStatementWrapper.addColumn(createColumn("column_1", "1"), emptyVariables);
        insertPreparedStatementWrapper.addColumn(createColumn("column_2", "2"), emptyVariables);
        insertPreparedStatementWrapper.executeUpdate();

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1,column_2) values (?,?)");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", INTEGER);
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void addLiteralColumn() throws Exception {
        insertPreparedStatementWrapper = new InsertPreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        insertPreparedStatementWrapper.addColumn(createColumn("column_1", "=literal1"), emptyVariables);
        insertPreparedStatementWrapper.addColumn(createColumn("column_2", "=literal2"), emptyVariables);
        insertPreparedStatementWrapper.executeUpdate();

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1,column_2) values (literal1,literal2)");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeUpdate();
    }
}