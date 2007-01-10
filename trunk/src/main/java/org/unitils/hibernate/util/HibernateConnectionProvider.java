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
package org.unitils.hibernate.util;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Implementation of the Hibernate <code>ConnectionProvider</code> interface. Provides JDBC connections to Hibernate
 * using the <code>DataSource</code> from the {@link DatabaseModule}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateConnectionProvider implements ConnectionProvider {

    /* Provides connections to the unit test database */
    private DataSource dataSource;


    /**
     * Create instance and fetch the <code>DataSource</code> from the {@link DatabaseModule}
     */
    public HibernateConnectionProvider() {
        dataSource = getDatabaseModule().getDataSource();
    }


    /**
     * Possibility to do something with the Hibernate properties. Nothing is done with it at the moment.
     *
     * @param properties The hibernate properties, not null
     */
    public void configure(Properties properties) throws HibernateException {
    }


    /**
     * @return A <code>Connection</code> from the unit test database <code>DataSource</code>
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    /**
     * Closes the given connection, i.e. returns it to the connection pool.
     *
     * @param connection The connection, not null
     */
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }


    /**
     * Method for releasing resources. Does nothing.
     */
    public void close() throws HibernateException {
    }


    /**
     * @return true
     * @see ConnectionProvider#supportsAggressiveRelease()
     */
    public boolean supportsAggressiveRelease() {
        return true;
    }


    /**
     * @return Implementation of DatabaseModule, that provides the <code>DataSource</code>
     */
    protected DatabaseModule getDatabaseModule() {

        Unitils unitils = Unitils.getInstance();
        return unitils.getModulesRepository().getModuleOfType(DatabaseModule.class);
    }
}
