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

import static org.unitils.dataset.loader.impl.TestDataFactory.createRow;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetLoaderTest extends UnitilsJUnit4 {

    /* Tested object */
    private RefreshRowLoader refreshRowLoader = new RefreshRowLoader();

    private PartialMock<Database> database;
    private Mock<Connection> connection;
    private Mock<PreparedStatement> preparedStatement;
    private Mock<ParameterMetaData> parameterMetaData;

    private List<String> emptyVariables = new ArrayList<String>();


    @Before
    public void initialize() throws Exception {
        database.returns(connection).createConnection();
        connection.returns(preparedStatement).prepareStatement(null);
        preparedStatement.returns(1).executeUpdate();
        preparedStatement.returns(parameterMetaData).getParameterMetaData();

        NameProcessor nameProcessor = new NameProcessor("'");
        ColumnProcessor columnProcessor = new ColumnProcessor('=', '$', nameProcessor);
        refreshRowLoader.init(columnProcessor, nameProcessor, database.getMock());
    }

    @Before
    public void initializePrimaryKeys() throws SQLException {
        database.returns(asSet("PK1", "Pk2")).getPrimaryKeyColumnNames(null);
    }


    @Test
    public void rowAlreadyInDatabase() throws Exception {
        refreshRowLoader.loadRow(createRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=?, column_2=?, pk1=?, pk2=? where pk1=?, pk2=?");
        connection.assertNotInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (?, ?, ?, ?)");
    }

    @Test
    public void rowNotYetInDatabase() throws Exception {
        preparedStatement.onceReturns(0).executeUpdate();
        refreshRowLoader.loadRow(createRow(), emptyVariables);

        connection.assertInvoked().prepareStatement("update my_schema.table_a set column_1=?, column_2=?, pk1=?, pk2=? where pk1=?, pk2=?");
        connection.assertInvoked().prepareStatement("insert into my_schema.table_a (column_1, column_2, pk1, pk2) values (?, ?, ?, ?)");
    }

}