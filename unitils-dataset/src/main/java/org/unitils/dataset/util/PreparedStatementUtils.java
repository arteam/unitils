/*
 * Copyright 2006-2009,  Unitils.org
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
import org.unitils.core.UnitilsException;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.unitils.core.util.DbUtils.close;
import static org.unitils.core.util.DbUtils.closeQuietly;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PreparedStatementUtils {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PreparedStatementUtils.class);


    public static int executeUpdate(String sql, List<String> statementValues, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = createPreparedStatement(sql, statementValues, connection);
            return preparedStatement.executeUpdate();
        } finally {
            close(preparedStatement);
        }
    }

    public static PreparedStatement createPreparedStatement(String sql, List<String> statementValues, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            setStatementValues(preparedStatement, statementValues);
            return preparedStatement;
        } catch (Throwable t) {
            closeQuietly(null, preparedStatement, null);
            throw new UnitilsException("Unable to execute query " + sql + ", statement values: " + statementValues, t);
        }
    }


    protected static void setStatementValues(PreparedStatement preparedStatement, List<String> statementValues) throws SQLException {
        if (statementValues.isEmpty()) {
            return;
        }
        ParameterMetaData parameterMetaData = preparedStatement.getParameterMetaData();

        int index = 1;
        for (String value : statementValues) {
            int columnTypeInDatabase = parameterMetaData.getParameterType(index);
            preparedStatement.setObject(index++, value, columnTypeInDatabase);
        }
    }

    protected static void logStatement(String sql, List<String> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug(sql);
        } else {
            logger.debug(sql + " <- " + statementValues);
        }
    }
}