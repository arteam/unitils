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
import org.unitils.dataset.core.ColumnProcessor;
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
public class InsertRowLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private InsertRowLoader insertRowLoader = new InsertRowLoader();

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
        insertRowLoader.init(columnProcessor, nameProcessor, database.getMock());
    }

    @Before
    public void initializePrimaryKeys() throws SQLException {
        database.returns(asSet("PK1", "Pk2")).getPrimaryKeyColumnNames(null);
    }


    @Test
    public void loadRow() throws Exception {
        insertRowLoader.loadRow(createRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (?, ?, ?, ?)");
        preparedStatement.assertInvoked().setObject(1, "1", VARCHAR);
        preparedStatement.assertInvoked().setObject(2, "2", VARCHAR);
        preparedStatement.assertInvoked().setObject(3, "3", VARCHAR);
        preparedStatement.assertInvoked().setObject(4, "4", VARCHAR);
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void literalColumns() throws Exception {
        insertRowLoader.loadRow(createRowWithLiteralValues(), emptyVariables);

        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (literal1, literal2, 3, 4)");
        preparedStatement.assertInvoked().executeUpdate();
    }

    @Test
    public void caseSensitive() throws Exception {
        insertRowLoader.loadRow(createCaseSensitiveRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("insert into 'my_schema'.'table_a' ('column_1', 'column_2', 'PK1', 'Pk2') values (?, ?, ?, ?)");
        preparedStatement.assertInvoked().executeUpdate();
    }


}