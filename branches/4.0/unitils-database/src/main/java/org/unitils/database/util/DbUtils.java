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
package org.unitils.database.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public final class DbUtils {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbUtils.class);


    public static void close(Connection connection, DataSource dataSource) throws SQLException {
        if (connection != null) {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public static void close(Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
    }

    public static void close(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
    }

    public static void close(Connection connection, Statement statement, ResultSet resultSet, DataSource dataSource) throws SQLException {
        try {
            close(resultSet);
        } finally {
            try {
                close(statement);
            } finally {
                close(connection, dataSource);
            }
        }
    }

    public static void closeQuietly(Connection connection, Statement statement, ResultSet resultSet, DataSource dataSource) {
        try {
            close(connection, statement, resultSet, dataSource);
        } catch (SQLException e) {
            logger.warn("Unable to close connection, statement or result set. Ignoring exception.", e);
        }
    }

}