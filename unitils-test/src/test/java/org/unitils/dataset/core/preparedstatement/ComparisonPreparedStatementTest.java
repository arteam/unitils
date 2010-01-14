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
public class ComparisonPreparedStatementTest extends PreparedStatementTestBase {

    /* Tested object */
    private ComparisonPreparedStatement comparisonPreparedStatement;


    @Test
    public void executeUpdate() throws Exception {
        comparisonPreparedStatement = new ComparisonPreparedStatement("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatement.executeQuery(row, emptyVariables);

        connection.assertInvoked().prepareStatement("select column_1, ?, column_2, ?, pk1, ?, pk2, ?, Pk2, PK1 from my_schema.table_a");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().executeQuery();
    }

    @Test
    public void literalColumns() throws Exception {
        comparisonPreparedStatement = new ComparisonPreparedStatement("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatement.executeQuery(rowWithLiteralValues, emptyVariables);

        connection.assertInvoked().prepareStatement("select column_1, literal1, column_2, literal2, pk1, 3, pk2, 4, Pk2, PK1 from my_schema.table_a");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeQuery();
    }
}