/*
 * Copyright 2006-2007,  Unitils.org
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

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.dbunit.database.AbstractDatabaseConnection;
import org.springframework.jdbc.datasource.DataSourceUtils;

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
    private Connection currentlyUsedConnection;


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
    @Override
    public void close() throws SQLException {
    	System.out.println("close");
        // Nothing to be done. Connections are closed (i.e. returned to the pool) after every dbUnit operation
    }


    /**
     * @return The database schema name
     */
    @Override
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
    @Override
    public Connection getConnection() throws SQLException {
        if (currentlyUsedConnection == null) {
            Connection connection = DataSourceUtils.getConnection(dataSource);
			currentlyUsedConnection = connection;
        }
        return currentlyUsedConnection;
    }
    
    
    // TODO move to utility class
    
    /**
	 * Returns a proxy that implements the {@link CloseSuppressingConnection} interface, that delegates to an actual
	 * {@link Connection}, suppresses the {@link Connection#close()} method and implements the
	 * {@link CloseSuppressingConnection#doClose()} method which actually closes the target connection.
	 * 
	 * @param connection The wrapped connection to which is delegated
	 * @return A close suppressing connection
	 */
	protected Connection getCloseSuppressingConnectionProxy(Connection connection) {
		return (Connection) newProxyInstance(getClass().getClassLoader(), new Class[] { Connection.class }, new CloseSuppressionConnectionProxyInvocationHandler(connection));
	}
    
    
	/**
	 * Invocation handler that can be used to implement a proxy implementing the {@link CloseSuppressingConnection}
	 * interface. The generated proxy delegates all method calls, unless the {@link Connection#close()} method, which is
	 * suppressed. Actually closing the underlying {@link Connection} can be done using
	 * {@link CloseSuppressingConnection#doClose()}.
	 */
	protected static class CloseSuppressionConnectionProxyInvocationHandler implements InvocationHandler {

		private Connection wrappedConnection;

		protected CloseSuppressionConnectionProxyInvocationHandler(Connection wrappedConnection) {
			this.wrappedConnection = wrappedConnection;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("close")) {
				// do nothing, connection close is suppressed
				return null;
			}
			try {
				return method.invoke(wrappedConnection, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

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
        }
    }

}
