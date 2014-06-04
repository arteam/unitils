/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dbunit.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.database.AbstractDatabaseConnection;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.unitils.database.DatabaseUnitils;

/**
 * Implementation of DBUnits <code>IDatabaseConnection</code> interface. This implementation returns connections from
 * an underlying <code>DataSource</code>. This implementation stores the <code>Connection</code> that was retrieved last,
 * to enable closing it (or returing it to the pool) using {@link #closeJdbcConnection()}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitDatabaseConnection extends AbstractDatabaseConnection {

    /* DataSource that provides access to JDBC connections */
    private DataSource dataSource;

    /* Name of the database schema */
    private String schemaName;

    /* Connection that is currently in use by DBUnit. Is stored to enable returning it to the connection pool after
     the DBUnit operation finished */
    private Connection currentlyUsedConnection, currentlyUsedNativeConnection;


    /**
     * Creates a new instance that wraps the given <code>DataSource</code>
     *
     * @param dataSource The data source, not null
     * @param schemaName The database schema, not null
     */
    public DbUnitDatabaseConnection(DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;
        this.schemaName = schemaName;
    }


    /**
     * Method that is invoked by DBUnit when the connection is no longer needed. This method is not implemented,
     * connections are 'closed' (returned to the connection pool) after every DBUnit operation
     */
    public void close() throws SQLException {
        // Nothing to be done. Connections are closed (i.e. returned to the pool) after every dbUnit operation
    }


    /**
     * @return The database schema name
     */
    public String getSchema() {
        return schemaName;
    }


    /**
     * Returns a <code>Connection</code> that can be used by DBUnit. A reference to the connection is kept, to be able
     * to 'close' it (return it to the connection pool) after the DBUnit operation finished. If an open connection
     * is already in use by DBUnit, this connection is returned
     *
     * @return A JDBC connection
     */
    public Connection getConnection() throws SQLException {
        if (currentlyUsedConnection == null) {
            currentlyUsedConnection = DataSourceUtils.getConnection(dataSource);
            currentlyUsedNativeConnection = getNativeConnection(currentlyUsedConnection);
        }
        
        
        /*if (dataSource instanceof BasicDataSource) {
            BasicDataSource tempDataSource = (BasicDataSource) dataSource;
            boolean canAccess = tempDataSource.isAccessToUnderlyingConnectionAllowed();
            if (!canAccess) {
                tempDataSource.setAccessToUnderlyingConnectionAllowed(true);
            }
            if (tempDataSource.getDriverClassName().toLowerCase().contains("oracle")  && currentlyUsedNativeConnection instanceof DelegatingConnection) {
                DelegatingConnection tempConnection = (DelegatingConnection) currentlyUsedNativeConnection;
                return tempConnection.getInnermostDelegate();
            }
            if (!canAccess) {
                tempDataSource.setAccessToUnderlyingConnectionAllowed(false);
            } 
            
        }
        return currentlyUsedNativeConnection;*/
        
        return DatabaseUnitils.getGoodConnection(currentlyUsedNativeConnection, dataSource);
    }


    /**
     * @param connection The wrapper connection, not null
     * @return The 'native' connection, which is wrapped by the given connection. Could be the supplied connection itself
     */
    protected Connection getNativeConnection(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        if (metaData != null) {
            Connection targetConnection = metaData.getConnection();
            if (targetConnection != null) {
                return targetConnection;
            }
        }
        return connection;
    }


    /**
     * Closes the <code>Connection</code> that was last retrieved using the {@link #getConnection} method
     *
     * @throws SQLException When connection close fails
     */
    public void closeJdbcConnection() throws SQLException {
        if (currentlyUsedConnection != null) {
            DataSourceUtils.releaseConnection(currentlyUsedConnection, dataSource);
            currentlyUsedConnection = null;
            currentlyUsedNativeConnection = null;
        }
    }

}
