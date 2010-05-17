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
package org.unitils.dataset.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.loader.impl.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.unitils.core.util.DbUtils.close;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAccessor {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DatabaseAccessor.class);

    private Database database;


    public DatabaseAccessor(Database database) {
        this.database = database;
    }

    public int executeUpdate(String sql, List<DatabaseColumnWithValue> statementValues) throws Exception {
        PreparedStatement preparedStatement = null;
        Connection connection = database.getConnection();
        try {
            preparedStatement = connection.prepareStatement(sql);
            setStatementValues(preparedStatement, statementValues);
            return preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    protected void setStatementValues(PreparedStatement preparedStatement, List<DatabaseColumnWithValue> statementValues) throws Exception {
        if (statementValues == null || statementValues.isEmpty()) {
            return;
        }
        int index = 1;
        for (DatabaseColumnWithValue databaseColumnWithValue : statementValues) {
            int sqlType = databaseColumnWithValue.getSqlType();
            preparedStatement.setObject(index++, databaseColumnWithValue.getValue(), sqlType);
        }
    }


    // todo implement

    protected void logStatement(String sql, List<String> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug(sql);
        } else {
            logger.debug(sql + " <- " + statementValues);
        }
    }
}