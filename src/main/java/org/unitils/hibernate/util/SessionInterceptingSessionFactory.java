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
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SessionInterceptingSessionFactory implements SessionFactory {

    protected SessionFactory wrappedSessionFactory;

    protected Set<org.hibernate.Session> sessions = new HashSet<org.hibernate.Session>();


    public SessionInterceptingSessionFactory(SessionFactory sessionFactory) {
        this.wrappedSessionFactory = sessionFactory;
    }


    public Session openSession() throws HibernateException {
        Session session = wrappedSessionFactory.openSession();
        registerOpenedSession(session);

        simulateTransactionBegin();
        return session;
    }


    public Session openSession(Connection connection) {
        Session session = wrappedSessionFactory.openSession(connection);
        registerOpenedSession(session);

        simulateTransactionBegin();
        return session;
    }


    public Session openSession(Connection connection, Interceptor interceptor) {
        Session session = wrappedSessionFactory.openSession(connection, interceptor);
        registerOpenedSession(session);

        simulateTransactionBegin();
        return session;
    }


    public Session openSession(Interceptor interceptor) throws HibernateException {
        Session session = wrappedSessionFactory.openSession(interceptor);
        registerOpenedSession(session);

        simulateTransactionBegin();
        return session;
    }


    public Session getCurrentSession() throws HibernateException {
        Session session = wrappedSessionFactory.getCurrentSession();
        registerOpenedSession(session);
        return session;
    }


    private void registerOpenedSession(Session session) {
        sessions.add(session);
    }


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


    private boolean transactionWasSimulated = false;
    private boolean simulateTransactionEnabled = true;

    //todo implement
    //todo javadoc
    protected void simulateTransactionBegin() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        if (!simulateTransactionEnabled) {
            return;
        }
        transactionWasSimulated = true;

        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.setCurrentTransactionName("simulatedByUnitils");
        TransactionSynchronizationManager.initSynchronization();
    }


    //todo implement
    //todo javadoc
    protected void simulateTransactionEnd() {
        if (!transactionWasSimulated) {
            return;
        }
        transactionWasSimulated = false;

        TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setCurrentTransactionName(null);
        TransactionSynchronizationManager.setActualTransactionActive(false);
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
