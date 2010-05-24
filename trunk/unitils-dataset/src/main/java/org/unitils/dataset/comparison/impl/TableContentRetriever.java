/*
 * Copyright Unitils.org
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
import org.unitils.dataset.core.Column;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static org.unitils.core.util.DbUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableContentRetriever {

    protected Database database;
    protected SqlTypeHandlerRepository sqlTypeHandlerRepository;


    public void init(Database database, SqlTypeHandlerRepository sqlTypeHandlerRepository) {
        this.database = database;
        this.sqlTypeHandlerRepository = sqlTypeHandlerRepository;
    }


    // todo remove primary key column names  (implicit in database columns)

    public TableContents getTableContents(String qualifiedTableName, List<Column> columns, Set<String> primaryKeyColumnNames) throws SQLException {
        String sql = createStatement(qualifiedTableName, columns, primaryKeyColumnNames);

        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection connection = database.getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            return new TableContents(qualifiedTableName, columns, sqlTypeHandlerRepository, primaryKeyColumnNames, connection, preparedStatement, resultSet);

        } catch (Exception e) {
            closeQuietly(connection, preparedStatement, resultSet);
            throw new UnitilsException("Unable to execute query " + sql, e);
        }
    }


    protected String createStatement(String qualifiedTableName, List<Column> columns, Set<String> primaryKeyColumnNames) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select ");
        for (Column column : columns) {
            stringBuilder.append(column.getName());
            stringBuilder.append(", ");
        }
        for (String primaryKeyColumnName : primaryKeyColumnNames) {
            stringBuilder.append(primaryKeyColumnName);
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        stringBuilder.append(" from ");
        stringBuilder.append(qualifiedTableName);
        return stringBuilder.toString();
    }

}