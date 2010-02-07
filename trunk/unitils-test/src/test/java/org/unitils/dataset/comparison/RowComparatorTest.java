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
package org.unitils.dataset.comparison;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.comparison.impl.RowComparator;
import org.unitils.dataset.core.ColumnProcessor;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.NameProcessor;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.VARCHAR;
import static org.unitils.dataset.loader.impl.TestDataFactory.*;
import static org.unitils.mock.ArgumentMatchers.anyInt;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparatorTest extends UnitilsJUnit4 {

    /* Tested object */
    private RowComparator rowComparator;

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
        rowComparator = new RowComparator();
        rowComparator.init(columnProcessor, nameProcessor, database.getMock());
    }

    @Before
    public void initializePrimaryKeys() throws SQLException {
        database.returns(asSet("PK1", "Pk2")).getPrimaryKeyColumnNames(null);
    }


    @Test
    public void executeUpdate() throws Exception {
        rowComparator.compareRowWithDatabase(createRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("select column_1, ?, column_2, ?, pk1, ?, pk2, ?, 'PK1', 'Pk2' from my_schema.table_a");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().executeQuery();
    }

    @Test
    public void literalColumns() throws Exception {
        rowComparator.compareRowWithDatabase(createRowWithLiteralValues(), emptyVariables);

        connection.assertInvoked().prepareStatement("select column_1, literal1, column_2, literal2, pk1, 3, pk2, 4, 'PK1', 'Pk2' from my_schema.table_a");
        preparedStatement.assertNotInvoked().getMetaData();
        preparedStatement.assertInvoked().executeQuery();
    }

    @Test
    public void caseSensitive() throws Exception {
        rowComparator.compareRowWithDatabase(createCaseSensitiveRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("select 'column_1', ?, 'column_2', ?, 'PK1', ?, 'Pk2', ?, 'PK1', 'Pk2' from 'my_schema'.'table_a'");
        preparedStatement.assertInvoked().executeQuery();
    }
}