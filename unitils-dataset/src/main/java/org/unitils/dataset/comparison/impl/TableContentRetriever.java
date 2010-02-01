/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.comparison.impl;

import org.unitils.core.UnitilsException;
import org.unitils.dataset.comparison.impl.QueryResultSet;
import org.unitils.dataset.core.Table;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.NameProcessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import static org.unitils.core.util.DbUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableContentRetriever {

    private Database database;
    private NameProcessor nameProcessor;


    public TableContentRetriever(NameProcessor nameProcessor, Database database) throws SQLException {
        this.database = database;
        this.nameProcessor = nameProcessor;
    }

    public QueryResultSet getTableContent(Table table) throws SQLException {
        Set<String> primaryKeyColumnNames = database.getPrimaryKeyColumnNames(table);
        String sql = createStatement(table);

        Connection connection = database.createConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            return new QueryResultSet(connection, preparedStatement, resultSet, primaryKeyColumnNames);
        } catch (Throwable t) {
            closeQuietly(connection, preparedStatement, resultSet);
            throw new UnitilsException("Unable to get content for table " + table, t);
        }
    }

    protected String createStatement(Table table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select * from ");
        stringBuilder.append(nameProcessor.getTableName(table));
        return stringBuilder.toString();
    }

}