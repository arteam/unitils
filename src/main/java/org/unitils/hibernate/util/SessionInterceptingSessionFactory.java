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
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper for a Hibernate session factory that will intercept all opened session factories and
 * offers operations to get those opened session and close or flush them.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SessionInterceptingSessionFactory implements SessionFactory {

    /**
     * The wrapped session factory.
     */
    protected SessionFactory wrappedSessionFactory;

    /**
     * The intercepted sessions.
     */
    protected Set<org.hibernate.Session> sessions = new HashSet<org.hibernate.Session>();


    /**
     * Creates a wrapper for the given session factory.
     *
     * @param sessionFactory The factory, not null
     */
    public SessionInterceptingSessionFactory(SessionFactory sessionFactory) {
        this.wrappedSessionFactory = sessionFactory;
    }


    /**
     * Opens a new hibernate session. Overriden to store the opened session.
     *
     * @return the session, not null
     */
    public Session openSession() throws HibernateException {
        Session session = wrappedSessionFactory.openSession();
        sessions.add(session);
        return session;
    }


    /**
     * Opens a new hibernate session. Overriden to store the opened session.
     *
     * @param connection The connection to use
     * @return the session, not null
     */
    public Session openSession(Connection connection) {
        Session session = wrappedSessionFactory.openSession(connection);
        sessions.add(session);
        return session;
    }


    /**
     * Opens a new hibernate session. Overriden to store the opened session.
     *
     * @param connection  The connection to use
     * @param interceptor The session interceptor to use
     * @return the session, not null
     */
    public Session openSession(Connection connection, Interceptor interceptor) {
        Session session = wrappedSessionFactory.openSession(connection, interceptor);
        sessions.add(session);
        return session;
    }


    /**
     * Opens a new hibernate session. Overriden to store the opened session.
     *
     * @param interceptor The session interceptor to use
     * @return the session, not null
     */
    public Session openSession(Interceptor interceptor) throws HibernateException {
        Session session = wrappedSessionFactory.openSession(interceptor);
        sessions.add(session);
        return session;
    }


    /**
     * Gets the current session if <code>CurrentSessionContext</code> is configured.
     *
     * @return The current session
     */
    public Session getCurrentSession() throws HibernateException {
        Session session = wrappedSessionFactory.getCurrentSession();
        sessions.add(session);
        return session;
    }


    /**
     * Gets all open intercepted sessions.
     *
     * @return The sessions, not null
     */
    public Set<org.hibernate.Session> getOpenedSessions() {
        return sessions;
    }


    /**
     * Closes and clears all open sessions.
     */
    public void closeOpenSessions() {
        for (org.hibernate.Session session : sessions) {
            if (session.isOpen()) {
                session.close();
            }
        }
        sessions.clear();
    }


    /**
     * Flushes all open sessions.
     */
    public void flushOpenSessions() {
        for (org.hibernate.Session session : sessions) {
            if (session.isOpen()) {
                session.flush();
            }
        }
    }

    //
    // Pass through delegation
    //

    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return wrappedSessionFactory.getClassMetadata(persistentClass);
    }


    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return wrappedSessionFactory.getClassMetadata(entityName);
    }


    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return wrappedSessionFactory.getCollectionMetadata(roleName);
    }


    public Map getAllClassMetadata() throws HibernateException {
        return wrappedSessionFactory.getAllClassMetadata();
    }


    public Map getAllCollectionMetadata() throws HibernateException {
        return wrappedSessionFactory.getAllCollectionMetadata();
    }


    public Statistics getStatistics() {
        return wrappedSessionFactory.getStatistics();
    }


    public void close() throws HibernateException {
        wrappedSessionFactory.close();
    }


    public boolean isClosed() {
        return wrappedSessionFactory.isClosed();
    }


    public void evict(Class persistentClass) throws HibernateException {
        wrappedSessionFactory.evict(persistentClass);
    }


    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        wrappedSessionFactory.evict(persistentClass, id);
    }


    public void evictEntity(String entityName) throws HibernateException {
        wrappedSessionFactory.evictEntity(entityName);
    }


    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        wrappedSessionFactory.evictEntity(entityName, id);
    }


    public void evictCollection(String roleName) throws HibernateException {
        wrappedSessionFactory.evictCollection(roleName);
    }


    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        wrappedSessionFactory.evictCollection(roleName, id);
    }


    public void evictQueries() throws HibernateException {
        wrappedSessionFactory.evictQueries();
    }


    public void evictQueries(String cacheRegion) throws HibernateException {
        wrappedSessionFactory.evictQueries(cacheRegion);
    }


    public StatelessSession openStatelessSession() {
        return wrappedSessionFactory.openStatelessSession();
    }


    public StatelessSession openStatelessSession(Connection connection) {
        return wrappedSessionFactory.openStatelessSession(connection);
    }


    public Set getDefinedFilterNames() {
        return wrappedSessionFactory.getDefinedFilterNames();
    }


    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return wrappedSessionFactory.getFilterDefinition(filterName);
    }


    public Reference getReference() throws NamingException {
        return wrappedSessionFactory.getReference();
    }


}
