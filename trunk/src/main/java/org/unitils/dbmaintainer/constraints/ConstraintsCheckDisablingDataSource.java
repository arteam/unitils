/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Wrapper or decorator for a <code>TestDataSource</code> that makes sure that the constraints are disabled on all
 * <code>Connection</code>s retrieved, provided that the {@link ConstraintsDisabler} is correctly used (see
 * Javadoc of {@link ConstraintsDisabler}
 */
public class ConstraintsCheckDisablingDataSource implements DataSource {

    /* The TestDataSource that is wrapped */
    private DataSource wrappedDataSource;

    /* The implementation of ConstraintsDisabler that is used */
    private ConstraintsDisabler constraintsDisabler;

    /**
     * Creates a new instance.
     * @param wrappedDataSource
     * @param constraintsDisabler
     */
    public ConstraintsCheckDisablingDataSource(DataSource wrappedDataSource, ConstraintsDisabler constraintsDisabler) {
        this.constraintsDisabler = constraintsDisabler;
        this.wrappedDataSource = wrappedDataSource;
    }

    /**
     * Returns a new connection to the database, on which constraints checking has been disabled.
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        Connection conn = wrappedDataSource.getConnection();
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

    /**
     * Returns a new connection to the database, on which constraints checking has been disabled.
     * 
     * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
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
