package org.unitils.dbmaintainer.util;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base decorator or wrapper for a <code>DataSource</code>. Can be subclassed to create a decorator for a
 * <code>DataSource</code> without having to implement all the methods of the <code>DataSource</code> interface.
 */
public class BaseDataSourceDecorator implements DataSource {

    /* The TestDataSource that is wrapped */
    private DataSource wrappedDataSource;

    /**
     * Creates a new instance that wraps the given <code>DataSource</code>
     *
     * @param wrappedDataSource
     */
    public BaseDataSourceDecorator(DataSource wrappedDataSource) {
        this.wrappedDataSource = wrappedDataSource;
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        return wrappedDataSource.getConnection();
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String,java.lang.String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        return wrappedDataSource.getConnection(username, password);
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
