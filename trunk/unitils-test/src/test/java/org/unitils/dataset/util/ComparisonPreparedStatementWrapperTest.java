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
public class ComparisonPreparedStatementWrapperTest extends PreparedStatementWrapperTestBase {

    /* Tested object */
    private ComparisonPreparedStatementWrapper comparisonPreparedStatementWrapper;


    @Test
    public void addColumn() throws Exception {
        comparisonPreparedStatementWrapper = new ComparisonPreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatementWrapper.addColumn(createColumn("column_1", "1"), emptyVariables);
        comparisonPreparedStatementWrapper.addColumn(createColumn("column_2", "2"), emptyVariables);
        comparisonPreparedStatementWrapper.executeQuery();

        connection.assertInvoked().prepareStatement("select column_1, ?, column_2, ?, 1 from my_schema.table_a");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", INTEGER);
        preparedStatement.assertInvoked().executeQuery();
    }

    @Test
    public void addLiteralColumn() throws Exception {
        comparisonPreparedStatementWrapper = new ComparisonPreparedStatementWrapper("my_schema", "table_a", connection.getMock());
        comparisonPreparedStatementWrapper.addColumn(createColumn("column_1", "=literal1"), emptyVariables);
        comparisonPreparedStatementWrapper.addColumn(createColumn("column_2", "=literal2"), emptyVariables);
        comparisonPreparedStatementWrapper.executeQuery();

        connection.assertInvoked().prepareStatement("select column_1, literal1, column_2, literal2, 1 from my_schema.table_a");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeQuery();
    }
}