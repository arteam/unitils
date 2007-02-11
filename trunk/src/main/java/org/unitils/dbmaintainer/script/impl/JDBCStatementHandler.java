/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbmaintainer.script.impl;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.script.StatementHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Implementation of {@link StatementHandler} that will execute the SQL statements on a database using JDBC.
 * A <code>TestDataSource</code> is provided on creation to provide the connection to the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JDBCStatementHandler implements StatementHandler {

    /* The TestDataSource */
    private DataSource dataSource;


    /**
     * Init of <code>TestDataSource</code> on which statements should org executed
     *
     * @param configuration The config, not null
     * @param dataSource    The database data source, not null
     */
    public void init(Properties configuration, DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Executes the given statement on the database
     *
     * @param statement The statement, not null
     * @throws StatementHandlerException If the statement could not be executed
     */
    public void handle(String statement) throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute(statement);

        } catch (SQLException e) {
            throw new StatementHandlerException("Error while executing statement: " + statement, e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }
}
