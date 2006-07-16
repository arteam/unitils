package be.ordina.unitils.testing.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;
import org.apache.commons.lang.StringUtils;

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

    public Configuration doGetConfiguration() {
        return configuration;
    }

}
