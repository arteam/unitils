package org.unitils.dbmaintainer.constraints;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.PrintWriter;

/**
 * Wrapper or decorator for a <code>DataSource</code> that makes sure that the constraints are disabled on all
 * <code>Connection</code>s retrieved, provided that the {@link ConstraintsDisabler} is correctly used (see
 * Javadoc of {@link ConstraintsDisabler}
 */
public class ConstraintsCheckDisablingDataSource implements DataSource {

    private DataSource wrappedDataSource;

    private ConstraintsDisabler constraintsDisabler;

    public ConstraintsCheckDisablingDataSource(DataSource wrappedDataSource, ConstraintsDisabler constraintsDisabler) {
        this.constraintsDisabler = constraintsDisabler;
        this.wrappedDataSource = wrappedDataSource;
    }

    public Connection getConnection() throws SQLException {
        Connection conn = wrappedDataSource.getConnection();
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = wrappedDataSource.getConnection(username, password);
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return wrappedDataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        wrappedDataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        wrappedDataSource.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return wrappedDataSource.getLoginTimeout();
    }
}
