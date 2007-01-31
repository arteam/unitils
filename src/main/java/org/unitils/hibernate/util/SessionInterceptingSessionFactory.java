package org.unitils.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.StatelessSession;
import org.hibernate.stat.Statistics;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.classic.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;

import javax.naming.Reference;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SessionInterceptingSessionFactory extends BaseSessionFactoryDecorator {

    private Set<Session> sessions = new HashSet<Session>();

    public SessionInterceptingSessionFactory(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Session openSession() throws HibernateException {
        Session session = super.openSession();
        registerOpenedSession(session);
        return session;
    }

    public Session openSession(Connection connection) {
        Session session = super.openSession(connection);
        registerOpenedSession(session);
        return session;
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        Session session = super.openSession(connection, interceptor);
        registerOpenedSession(session);
        return session;
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        Session session = super.openSession(interceptor);
        registerOpenedSession(session);
        return session;
    }

    public Session getCurrentSession() throws HibernateException {
        Session session = super.getCurrentSession();
        registerOpenedSession(session);
        return session;
    }

    private void registerOpenedSession(Session session) {
        sessions.add(session);
    }

    public Set<Session> getOpenedSessions() {
        return sessions;
    }

    public void forgetOpenedSessions() {
        sessions = new HashSet<Session>();
    }
}
