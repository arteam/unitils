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
    public void addColumn() throws Exception {
        comparisonPreparedStatement = new ComparisonPreparedStatement("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatement.addColumn(createColumn("column_1", "1"), emptyVariables);
        comparisonPreparedStatement.addColumn(createColumn("column_2", "2"), emptyVariables);
        comparisonPreparedStatement.executeQuery();

        connection.assertInvoked().prepareStatement("select column_1, ?, column_2, ? from my_schema.table_a");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().executeQuery();
    }

    @Test
    public void addLiteralColumn() throws Exception {
        comparisonPreparedStatement = new ComparisonPreparedStatement("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatement.addColumn(createColumn("column_1", "=literal1"), emptyVariables);
        comparisonPreparedStatement.addColumn(createColumn("column_2", "=literal2"), emptyVariables);
        comparisonPreparedStatement.executeQuery();

        connection.assertInvoked().prepareStatement("select column_1, literal1, column_2, literal2 from my_schema.table_a");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeQuery();
    }
}