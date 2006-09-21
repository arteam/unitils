package be.ordina.unitils.hibernate;

import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

/**
 * Base class for Hibernate integration testing operations.
 * Providing a <code>SessionFactory</code> and a <code>Configuration</code> object
 * to the user for testing purposes.
 */
public abstract class AbstractHibernateTestCase extends TestCase {

    /**
     * Hibernate Session creator. Threads servicing client requests obtain <code>Session</code>s from the factory.
     */
    private SessionFactory sessionFactory = HibernateSessionFactory.getInstance(getConfigLocation());

    /**
     * The <code>Configuration</code> allows the application to specify properties and mapping documents
     * to be used when creating a <code>SessionFactory</code>.
     */
    private Configuration configuration;

    protected String getConfigLocation() {
        return null;
    }

    /**
     * Gets the <code>Configuration</code> instance from the HibernateSessionFactory.
     *
     * @return Configuration
     */
    protected final Configuration getConfiguration() {
        if (configuration == null) {
            configuration = HibernateSessionFactory.getConfiguration();
        }

        return configuration;
    }

    /**
     * Gets the <code>SessionFactory</code> instance from the HibernateSessionFactory.
     *
     * @return SessionFactory
     */
    protected final SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = HibernateSessionFactory.getInstance(getConfigLocation());
        }

        return sessionFactory;
    }

    /**
     * Gets the database dialect from the Hibernate <code>Configuration</code.
     *
     * @return Dialect
     * @throws Exception if the dialect could not be retrieved from the configuration
     */
    protected final Dialect getDatabaseDialect() throws Exception {
        return (Dialect) Class.forName(getConfiguration().getProperty("hibernate.dialect")).newInstance();
    }

    /**
     * Hibernate guarantees that within the context of a single HibernateSession,
     * no two different objects of the same entity exist.
     * <p/>
     * This method must be called to avoid getting the retrieved instance from the session cache (first-level).
     */
    protected void flushHibernateSession() {
        // Flush all pending saves, updates and deletes to the database.
        sessionFactory.getCurrentSession().flush();
        // Remove all objects from the Session cache, and cancel all pending saves, updates and deletes.
        sessionFactory.getCurrentSession().clear();
    }
}
