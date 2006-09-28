package org.unitils.hibernate;

import org.unitils.dbunit.BaseDatabaseTestCase;
import org.unitils.util.UnitilsConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * Base class for DAO tests that use Hibernate.
 */
public abstract class BaseHibernateTestCase extends BaseDatabaseTestCase {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    /**
     * Implementation of <code>HibernateSessionManager</code> for unit testing purposes.
     */
    protected static UnitTestHibernateSessionManager unitTestHibernateSessionManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Make sure Hibernate makes use of the same database connection as the unit test
        if (unitTestHibernateSessionManager == null) {
            Configuration configuration = createHibernateConfiguration();
            unitTestHibernateSessionManager = new UnitTestHibernateSessionManager(configuration);
            HibernateSessionManager.injectInstance(unitTestHibernateSessionManager);
            injectSessionManager(unitTestHibernateSessionManager);
        }
        unitTestHibernateSessionManager.injectConnection(getConnection().getConnection());
    }

    private Configuration createHibernateConfiguration() {

        org.apache.commons.configuration.Configuration unitilsConfiguration = UnitilsConfiguration.getInstance();
        String configurationClassName = unitilsConfiguration.getString(PROPKEY_HIBERNATE_CONFIGURATION_CLASS);

        String[] configFiles = unitilsConfiguration.getStringArray(PROPKEY_HIBERNATE_CONFIGFILES);
        try {
            Configuration configuration = (Configuration) Class.forName(configurationClassName).newInstance();
            for (String configFile : configFiles) {
                configuration.configure(configFile);
            }
            // Hook method to perform extra configuration
            performExtraHibernateConfiguration(configuration);
            return configuration;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration class " + configurationClassName, e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        unitTestHibernateSessionManager.flushSession();
        unitTestHibernateSessionManager.closeSession();

        super.tearDown();
    }

    /**
     * You should override this method if you want to add some extra stuff to the Hibernate configuration object
     * before the <code>SessionFactory</code> is created. For example, if you want to add you entity class or mapping
     * files using the <code>addClass</code> or <code>addFile</code> methods.
     *
     * @param configuration
     */
    abstract protected void performExtraHibernateConfiguration(Configuration configuration);

    /**
     * This method should be overwritten if your regular code doesn't make use of Unitils
     * <code>HibernateSessionManager</code>, but you do want to make use of Unitils
     * <code>UnitTestHibernateSessionManager</code>.
     * <p/>
     * This method should make sure that your DAO classes make
     * use of the Hibernate session provided by the <code>UnitTestHibernateSessionManager</code>.
     */
    abstract protected void injectSessionManager(UnitTestHibernateSessionManager unitTestSessionManager);

    /**
     * Hibernate guarantees that within the context of a single HibernateSession,
     * no two different objects of the same entity exist.
     * <p/>
     * Call this method in your tests if you want to test update behavior, after calling the method that performs
     * the update, to avoid getting the retrieved instance from the session cache (first-level).
     */
    protected void flushAndClearSession() {
        // Flush all pending saves, updates and deletes to the database.
        unitTestHibernateSessionManager.flushSession();
        // Remove all objects from the Session cache
        unitTestHibernateSessionManager.clearSession();
    }

    @Override
    protected void assertDBContentAsExpected() throws Exception {
        // Flush all pending saves, updates and deletes to the database.
        unitTestHibernateSessionManager.flushSession();
        super.assertDBContentAsExpected();
    }

}
