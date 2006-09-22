/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.handler;

import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of {@link StatementHandler} that will execute the SQL statements on a database using JDBC.
 * A <code>DataSource</code> is provided on creation to provide the connection to the database.
 */
public class JDBCStatementHandler implements StatementHandler {

    private DataSource dataSource;

    /**
     * Init of <code>DataSource</code> on which statements should org executed
     *
     * @param dataSource
     */
    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes the given statement on the database
     *
     * @param statement
     * @throws StatementHandlerException
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
