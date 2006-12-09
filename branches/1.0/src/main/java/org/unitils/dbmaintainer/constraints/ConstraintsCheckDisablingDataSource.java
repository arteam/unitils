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
package org.unitils.dbmaintainer.constraints;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrapper or decorator for a <code>TestDataSource</code> that makes sure that for every
 * <code>Connection</code>s that is returned, the method {@link ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)}
 * is called.
 *
 * @author Filip Neven
 */
public class ConstraintsCheckDisablingDataSource implements DataSource {

    /* The TestDataSource that is wrapped */
    private DataSource wrappedDataSource;

    /* The implementation of ConstraintsDisabler that is used */
    private ConstraintsDisabler constraintsDisabler;

    /**
     * Creates a new instance that wraps the given <code>DataSource</code>
     *
     * @param wrappedDataSource
     * @param constraintsDisabler
     */
    public ConstraintsCheckDisablingDataSource(DataSource wrappedDataSource, ConstraintsDisabler constraintsDisabler) {
        this.constraintsDisabler = constraintsDisabler;
        this.wrappedDataSource = wrappedDataSource;
    }

    /**
     * Returns a new connection to the database, on which the method
     * {@link ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)} has been called.
     *
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        Connection conn = wrappedDataSource.getConnection();
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

    /**
     * Returns a new connection to the database, on which the method
     * {@link ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)} has been called.
     *
     * @see javax.sql.DataSource#getConnection(java.lang.String,java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = wrappedDataSource.getConnection(username, password);
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return wrappedDataSource.getLogWriter();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        wrappedDataSource.setLogWriter(out);
    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        wrappedDataSource.setLoginTimeout(seconds);
    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return wrappedDataSource.getLoginTimeout();
    }
}
