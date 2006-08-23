package be.ordina.unitils.testing.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;

/**
 *
 */
public class UnitTestHibernateSessionManager extends HibernateSessionManager {

    private Configuration configuration;

    private SessionFactory sessionFactory;

    private Session currentSession;

    public UnitTestHibernateSessionManager(Configuration configuration) {
        this.configuration = configuration;
        sessionFactory = configuration.buildSessionFactory();
    }

    public void injectConnection(Connection injectedConnection) {
        currentSession = sessionFactory.openSession(injectedConnection);
    }

    @Override
    public Session doGetSession() {
        return currentSession;
    }

    public void closeSession() {
        currentSession.close();
    }

    public void flushSession() {
        currentSession.flush();
    }

    public void clearSession() {
        currentSession.clear();
    }

    @Override
    public Configuration doGetConfiguration() {
        return configuration;
    }

}
