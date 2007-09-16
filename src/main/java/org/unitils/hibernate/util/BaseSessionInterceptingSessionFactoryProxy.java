/*
 * Copyright 2006-2007,  Unitils.org
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
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * Base decorator or wrapper for a Hibernate <code>SessionFactory</code>. Can be subclassed to create a decorator for a
 * <code>SessionFactory</code> without having to implement all the methods of the <code>SessionFactory</code> interface.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@SuppressWarnings("unchecked")
public class BaseSessionInterceptingSessionFactoryProxy implements SessionFactory {


    /* The wrapped session factory */
    private SessionFactory targetSessionFactory;


    /**
     * Creates a wrapper for the given session factory.
     *
     * @param sessionFactory The factory, not null
     */
    public BaseSessionInterceptingSessionFactoryProxy(SessionFactory sessionFactory) {
        this.targetSessionFactory = sessionFactory;
    }


    /**
     * Creates a new instance without initializing the target <code>SessionFactory</code>. Make sure to call the method
     * {@link #setTargetSessionFactory(SessionFactory)} before using this object.
     */
    public BaseSessionInterceptingSessionFactoryProxy() {
    }


    /**
     * Gets the wrapped session factory
     *
     * @return The session factory, not null
     */
    public SessionFactory getTargetSessionFactory() {
        return targetSessionFactory;
    }


    /**
     * Sets the wrapped session factory
     *
     * @param targetSessionFactory The session factory, not null
     */
    public void setTargetSessionFactory(SessionFactory targetSessionFactory) {
        this.targetSessionFactory = targetSessionFactory;
    }


    /**
     * @see SessionFactory#openSession()
     */
    public org.hibernate.classic.Session openSession() throws HibernateException {
        return getTargetSessionFactory().openSession();
    }


    /**
     * @see SessionFactory#openSession(Connection)
     */
    public org.hibernate.classic.Session openSession(Connection connection) {
        return getTargetSessionFactory().openSession(connection);
    }


    /**
     * @see SessionFactory#openSession(Connection,Interceptor)
     */
    public org.hibernate.classic.Session openSession(Connection connection, Interceptor interceptor) {
        return getTargetSessionFactory().openSession(connection, interceptor);
    }


    /**
     * @see SessionFactory#openSession(Interceptor)
     */
    public org.hibernate.classic.Session openSession(Interceptor interceptor) throws HibernateException {
        return getTargetSessionFactory().openSession(interceptor);
    }


    /**
     * @see SessionFactory#getCurrentSession()
     */
    public org.hibernate.classic.Session getCurrentSession() throws HibernateException {
        return getTargetSessionFactory().getCurrentSession();
    }


    /**
     * @see SessionFactory#getClassMetadata(Class)
     */
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return getTargetSessionFactory().getClassMetadata(persistentClass);
    }


    /**
     * @see SessionFactory#getClassMetadata(String)
     */
    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return getTargetSessionFactory().getClassMetadata(entityName);
    }


    /**
     * @see SessionFactory#getCollectionMetadata(String)
     */
    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return getTargetSessionFactory().getCollectionMetadata(roleName);
    }


    /**
     * @see SessionFactory#getAllClassMetadata()
     */
    public Map getAllClassMetadata() throws HibernateException {
        return getTargetSessionFactory().getAllClassMetadata();
    }


    /**
     * @see SessionFactory#getAllCollectionMetadata()
     */
    public Map getAllCollectionMetadata() throws HibernateException {
        return getTargetSessionFactory().getAllCollectionMetadata();
    }


    /**
     * @see SessionFactory#getStatistics()
     */
    public Statistics getStatistics() {
        return getTargetSessionFactory().getStatistics();
    }


    /**
     * @see SessionFactory#close()
     */
    public void close() throws HibernateException {
        getTargetSessionFactory().close();
    }


    /**
     * @see SessionFactory#isClosed()
     */
    public boolean isClosed() {
        return getTargetSessionFactory().isClosed();
    }


    /**
     * @see SessionFactory#evict(Class)
     */
    public void evict(Class persistentClass) throws HibernateException {
        getTargetSessionFactory().evict(persistentClass);
    }


    /**
     * @see SessionFactory#evict(Class,Serializable)
     */
    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        getTargetSessionFactory().evict(persistentClass, id);
    }


    /**
     * @see SessionFactory#evictEntity(String)
     */
    public void evictEntity(String entityName) throws HibernateException {
        getTargetSessionFactory().evictEntity(entityName);
    }


    /**
     * @see SessionFactory#evictEntity(String,Serializable)
     */
    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        getTargetSessionFactory().evictEntity(entityName, id);
    }


    /**
     * @see SessionFactory#evictCollection(String)
     */
    public void evictCollection(String roleName) throws HibernateException {
        getTargetSessionFactory().evictCollection(roleName);
    }


    /**
     * @see SessionFactory#evictCollection(String,Serializable)
     */
    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        getTargetSessionFactory().evictCollection(roleName, id);
    }


    /**
     * @see SessionFactory#evictQueries()
     */
    public void evictQueries() throws HibernateException {
        getTargetSessionFactory().evictQueries();
    }


    /**
     * @see SessionFactory#evictQueries(String)
     */
    public void evictQueries(String cacheRegion) throws HibernateException {
        getTargetSessionFactory().evictQueries(cacheRegion);
    }


    /**
     * @see SessionFactory#openStatelessSession()
     */
    public StatelessSession openStatelessSession() {
        return getTargetSessionFactory().openStatelessSession();
    }


    /**
     * @see SessionFactory#openStatelessSession(Connection)
     */
    public StatelessSession openStatelessSession(Connection connection) {
        return getTargetSessionFactory().openStatelessSession(connection);
    }


    /**
     * @see SessionFactory#getDefinedFilterNames()
     */
    public Set getDefinedFilterNames() {
        return getTargetSessionFactory().getDefinedFilterNames();
    }


    /**
     * @see SessionFactory#getFilterDefinition(String)
     */
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return getTargetSessionFactory().getFilterDefinition(filterName);
    }


    /**
     * @see SessionFactory#getReference()
     */
    public Reference getReference() throws NamingException {
        return getTargetSessionFactory().getReference();
    }


}
