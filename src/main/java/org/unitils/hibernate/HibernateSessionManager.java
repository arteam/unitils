package org.unitils.hibernate;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

/**
 * 
 */
public abstract class HibernateSessionManager {

    private static HibernateSessionManager instance;

    private static HibernateSessionManager getInstance() {
        if (instance == null) {
            instance = new DefaultHibernateSessionManager();
        }
        return instance;
    }

    public static void injectInstance(HibernateSessionManager injectedInstance) {
        instance = injectedInstance;
    }

    public static Configuration getConfiguration() {
        return getInstance().doGetConfiguration();
    }

    public static Session getSession() {
        return getInstance().doGetSession();
    }

    /**
     * Retrieves the current Session local to the thread. <p/> If no Session is open, opens a new
     * Session for the running thread.
     *
     * @return Session
     * @throws org.hibernate.HibernateException
     *
     */
    public abstract Session doGetSession();

    /**
     * Returns the Hibernate Configuration
     *
     * @return the Hibernate Configuration
     */
    public abstract Configuration doGetConfiguration();

}
