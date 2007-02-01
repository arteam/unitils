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
import org.hibernate.classic.Session;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

/**
 * todo javadoc
 *
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
