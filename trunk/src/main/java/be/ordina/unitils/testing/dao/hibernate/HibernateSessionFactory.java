package be.ordina.unitils.testing.dao.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import javax.naming.InitialContext;

/**
 * This class guarantees that only one single SessionFactory is instanciated and
 * that the configuration is done thread safe as a singleton. Actually it only
 * wraps the Hibernate SessionFactory. When a JNDI name is configured the session
 * is bound to to JNDI, else it is only saved locally.
 * <p/>
 * You are free to use any kind of JTA or Thread transactionFactories.
 */
public class HibernateSessionFactory {

    /**
     * Location of hibernate.cfg.xml file. NOTICE: Location should be on the
     * classpath as Hibernate uses #resourceAsStream style lookup for its
     * configuration file. That is place the config file in a Java package - the
     * default location is the default Java package.<br>
     * <br>
     * Examples: <br>
     * <code>CONFIG_FILE_LOCATION = "/hibernate.conf.xml".
     * CONFIG_FILE_LOCATION = "/com/foo/bar/myhiberstuff.conf.xml".</code>
     */
    private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";

    /**
     * The single instance of hibernate configuration
     */
    private static Configuration cfg = new Configuration();

    /**
     * The single instance of hibernate SessionFactory
     */
    private static org.hibernate.SessionFactory sessionFactory;

    /**
     * Default constructor.
     */
    private HibernateSessionFactory() {
    }

    /**
     * initialises the configuration if not yet done and returns the current
     * instance
     *
     * @return SessionFactory instance
     */
    public static SessionFactory getInstance(String configLocation) {
        if (configLocation != null) {
            CONFIG_FILE_LOCATION = configLocation;
        }

        if (sessionFactory == null) {
            initSessionFactory();
        }

        return sessionFactory;
    }

     /**
     * initialises the configuration if not yet done and returns the current
     * instance
     *
     * @return SessionFactory instance
     */
    public static SessionFactory getInstance() {
        if (sessionFactory == null) {
            initSessionFactory();
        }

        return sessionFactory;
    }

    /**
     * Returns the ThreadLocal Session instance. Lazy initialize the
     * <code>SessionFactory</code> if needed.
     *
     * @return Session
     * @throws org.hibernate.HibernateException
     *
     */
    public Session openSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * The behaviour of this method depends on the session context you have
     * configured. This factory is intended to be used with a hibernate.cfg.xml
     * including the following property <property name="current_session_context_class">thread</property>.
     * This would return the current open session or if this does not exist, will create a new session.
     *
     * @return Session that is currently associated with the <code>SessionFactory</code>
     */
    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * initializes the sessionfactory in a safe way even if more than one thread
     * tries to build a sessionFactory
     */
    private static synchronized void initSessionFactory() {
        Logger log = Logger.getLogger(HibernateSessionFactory.class);
        /*
         * Check again for null because sessionFactory may have been
         * initialized between the last check and now
         */
        if (sessionFactory == null) {
            try {
                cfg.configure(CONFIG_FILE_LOCATION);
                String sessionFactoryJndiName = cfg.getProperty(Environment.SESSION_FACTORY_NAME);
                if (sessionFactoryJndiName != null) {
                    log.debug("Get a JNDI SessionFactory");
                    cfg.buildSessionFactory();
                    sessionFactory = (SessionFactory) (new InitialContext()).lookup(sessionFactoryJndiName);
                } else {
                    log.debug("Classic SessionFactory");
                    sessionFactory = cfg.buildSessionFactory();
                }
            } catch (Exception e) {
                // find root cause
                Throwable exception = e;
                while (exception.getCause() != null) {
                    exception = exception.getCause();
                }

                throw new HibernateException("Hibernate is not properly configured: " + exception.getMessage());
            }
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }

        sessionFactory = null;
    }

    public static Configuration getConfiguration() {

        return cfg;
    }
}
