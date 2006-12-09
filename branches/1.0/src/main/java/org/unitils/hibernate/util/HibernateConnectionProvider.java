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
     * @param props
     * @throws HibernateException
     */
    public void configure(Properties props) throws HibernateException {
    }

    /**
     * @return A <code>Connection</code> from the unit test database <code>DataSource</code>
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the given connection, i.e. returns it to the connection pool.
     *
     * @param conn
     * @throws SQLException
     */
    public void closeConnection(Connection conn) throws SQLException {
        conn.close();
    }

    /**
     * Method for releasing resources. Does nothing.
     *
     * @throws HibernateException
     */
    public void close() throws HibernateException {
    }

    /**
     * @return true
     * @see org.hibernate.connection.ConnectionProvider#supportsAggressiveRelease()
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
