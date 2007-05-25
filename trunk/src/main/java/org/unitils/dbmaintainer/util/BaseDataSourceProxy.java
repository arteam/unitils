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
package org.unitils.dbmaintainer.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Base decorator or wrapper for a <code>DataSource</code>. Can be subclassed to create a decorator for a
 * <code>DataSource</code> without having to implement all the methods of the <code>DataSource</code> interface.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class BaseDataSourceProxy implements DataSource {

    /* The TestDataSource that is wrapped */
    private DataSource targetDataSource;


    /**
     * Creates a new instance without initializing the target <code>DataSource</code>. Make sure to call the method
     * {@link #setTargetDataSource(javax.sql.DataSource)} before using this object.
     */
    public BaseDataSourceProxy() {
    }

    /**
     * Creates a new instance that wraps the given <code>DataSource</code>
     *
     * @param targetDataSource the data source that is wrapped, not null
     */
    public BaseDataSourceProxy(DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }


    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return getTargetDataSource().getConnection();
    }


    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String,java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        return getTargetDataSource().getConnection(username, password);
    }


    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter() throws SQLException {
        return getTargetDataSource().getLogWriter();
    }


    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter(PrintWriter out) throws SQLException {
        getTargetDataSource().setLogWriter(out);
    }


    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout(int seconds) throws SQLException {
        getTargetDataSource().setLoginTimeout(seconds);
    }


    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout() throws SQLException {
        return getTargetDataSource().getLoginTimeout();
    }

    public DataSource getTargetDataSource() {
        return targetDataSource;
    }

    public void setTargetDataSource(DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }
}
