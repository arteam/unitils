package org.unitils.dbunit;

import org.dbunit.database.AbstractDatabaseConnection;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * 
 */
public class DbUnitDatabaseConnection extends AbstractDatabaseConnection {

    private DataSource dataSource;

    private String schemaName;

    private Connection currentlyUsedConnection;

    public DbUnitDatabaseConnection(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        this.schemaName = schemaName;
    }

    public void close() throws SQLException {
        // Nothing to be done. Connections are closed (i.e. returned to the pool) after every dbUnit operation
    }

    public String getSchema() {
        return schemaName;
    }

    public Connection getConnection() throws SQLException {
        if (currentlyUsedConnection == null) {
            currentlyUsedConnection = dataSource.getConnection();
        }
        return currentlyUsedConnection;
    }

    public void closeJdbcConnection() throws SQLException {
        currentlyUsedConnection.close();
        currentlyUsedConnection = null;
    }

}
