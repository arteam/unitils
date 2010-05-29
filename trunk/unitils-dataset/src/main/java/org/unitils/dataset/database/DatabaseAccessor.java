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
package org.unitils.dataset.database;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dataset.core.database.Value;

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

    private DatabaseMetaData database;


    public DatabaseAccessor(DatabaseMetaData database) {
        this.database = database;
    }

    public int executeUpdate(String sql, List<Value> statementValues) throws Exception {
        PreparedStatement preparedStatement = null;
        Connection connection = database.getConnection();
        try {
            logStatement(sql, statementValues);
            preparedStatement = connection.prepareStatement(sql);
            setStatementValues(preparedStatement, statementValues);
            return preparedStatement.executeUpdate();
        } finally {
            close(connection, preparedStatement, null);
        }
    }

    protected void setStatementValues(PreparedStatement preparedStatement, List<Value> statementValues) throws Exception {
        if (statementValues == null || statementValues.isEmpty()) {
            return;
        }
        int index = 1;
        for (Value value : statementValues) {
            int sqlType = value.getColumn().getSqlType();
            preparedStatement.setObject(index++, value.getValue(), sqlType);
        }
    }


    protected void logStatement(String sql, List<Value> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug(sql);
        } else {
            StringBuilder message = new StringBuilder(sql);
            if (!statementValues.isEmpty()) {
                message.append(" <- ");
                for (Value statementValue : statementValues) {
                    message.append(statementValue.getValue());
                    message.append(", ");
                }
                message.setLength(message.length() - 2);
            }
            logger.debug(message);
        }
    }
}