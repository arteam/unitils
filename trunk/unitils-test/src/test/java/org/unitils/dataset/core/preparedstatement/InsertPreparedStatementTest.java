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

import static java.sql.Types.VARCHAR;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InsertPreparedStatementTest extends PreparedStatementTestBase {

    /* Tested object */
    private InsertPreparedStatement insertPreparedStatement;


    @Test
    public void executeUpdate() throws Exception {
        insertPreparedStatement = new InsertPreparedStatement("my_schema", "table_a", connection.getMock());
        insertPreparedStatement.executeUpdate(row, emptyVariables);

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (?, ?, ?, ?)");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }


    @Test
    public void literalColumns() throws Exception {
        insertPreparedStatement = new InsertPreparedStatement("my_schema", "table_a", connection.getMock());
        insertPreparedStatement.executeUpdate(rowWithLiteralValues, emptyVariables);

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (literal1, literal2, 3, 4)");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeUpdate();
    }


}