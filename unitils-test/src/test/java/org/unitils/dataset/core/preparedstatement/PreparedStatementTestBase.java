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

import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;
import org.unitils.mock.Mock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.mock.ArgumentMatchers.anyInt;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class PreparedStatementTestBase extends UnitilsJUnit4 {

    protected Mock<Connection> connection;
    protected Mock<PreparedStatement> preparedStatement;
    protected Mock<ResultSet> primaryKeyResultSet;
    protected Mock<ParameterMetaData> parameterMetaData;

    protected Row row;
    protected Row rowWithLiteralValues;
    protected List<String> emptyVariables = new ArrayList<String>();

    @Before
    public void initialize() throws Exception {
        row = createRow();
        rowWithLiteralValues = createRowWithLiteralValues();

        connection.returns(preparedStatement).prepareStatement(null);
        connection.returns(primaryKeyResultSet).getMetaData().getPrimaryKeys(null, null, null);

        preparedStatement.returns(parameterMetaData).getParameterMetaData();
        parameterMetaData.returns(VARCHAR).getParameterType(anyInt());

        initializePrimaryKeys("PK1", "Pk2");
    }


    protected void initializePrimaryKeys(String... pkColumnNames) throws SQLException {
        for (String pkColumnName : pkColumnNames) {
            primaryKeyResultSet.onceReturns(true).next();
            primaryKeyResultSet.onceReturns(pkColumnName).getString(null);
        }
        primaryKeyResultSet.onceReturns(false).next();
    }

    protected void assertExceptionContainsPkColumnNames(UnitilsException e, String... pkColumnNames) {
        for (String pkColumnName : pkColumnNames) {
            assertTrue("Exception did not contain pk column name: " + pkColumnName + ". Message: " + e.getMessage(), e.getMessage().contains(pkColumnName));
        }
    }

    protected void assertExceptionNotContainsPkColumnNames(UnitilsException e, String... pkColumnNames) {
        for (String pkColumnName : pkColumnNames) {
            assertFalse("Exception did contain pk column name: " + pkColumnName + ". Message: " + e.getMessage(), e.getMessage().contains(pkColumnName));
        }
    }

    private Row createRow() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "1"));
        row.addColumn(createColumn("column_2", "2"));
        row.addColumn(createColumn("pk1", "3"));
        row.addColumn(createColumn("pk2", "4"));
        return row;
    }

    private Row createRowWithLiteralValues() {
        Row row = new Row();
        row.addColumn(createColumn("column_1", "=literal1"));
        row.addColumn(createColumn("column_2", "=literal2"));
        row.addColumn(createColumn("pk1", "=3"));
        row.addColumn(createColumn("pk2", "=4"));
        return row;
    }

    protected Column createColumn(String name, String value) {
        return new Column(name, value, false, '=', '$');
    }
}