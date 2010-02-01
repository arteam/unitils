/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TODO Make sure we use DataSourceUtils.getConnection and releaseConnection for getting / releasing Connections
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public final class DbUtils {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DbUtils.class);


    public static void close(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
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

    public static void close(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        try {
            close(resultSet);
        } finally {
            try {
                close(statement);
            } finally {
                close(connection);
            }
        }

    }

    public static void closeQuietly(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            close(connection, statement, resultSet);
        } catch (Throwable t) {
            logger.warn("Unable to close connection, statement or result set. Ignoring exception.", t);
        }
    }

}