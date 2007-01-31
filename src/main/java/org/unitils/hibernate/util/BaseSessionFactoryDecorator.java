package org.unitils.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.Interceptor;
import org.hibernate.HibernateException;
import org.hibernate.StatelessSession;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.stat.Statistics;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.classic.Session;

import javax.naming.Reference;
import javax.naming.NamingException;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class BaseSessionFactoryDecorator implements SessionFactory {

    protected SessionFactory wrappedSessionFactory;

    public BaseSessionFactoryDecorator(SessionFactory wrappedSessionFactory) {
        this.wrappedSessionFactory = wrappedSessionFactory;
    }

    public Session openSession(Connection connection) {
        return wrappedSessionFactory.openSession(connection);
    }

    public Session openSession(Interceptor interceptor) throws HibernateException {
        return wrappedSessionFactory.openSession(interceptor);
    }

    public Session openSession(Connection connection, Interceptor interceptor) {
        return wrappedSessionFactory.openSession(connection, interceptor);
    }

    public Session openSession() throws HibernateException {
        return wrappedSessionFactory.openSession();
    }

    public Session getCurrentSession() throws HibernateException {
        return wrappedSessionFactory.getCurrentSession();
    }

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
