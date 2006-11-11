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
package org.unitils.sample.eshop.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * 
 */
public class HibernateSessionManager {

    private static Session session;

    public static Session getSession() {
        if (session == null) {
            initSession();
        }
        return session;
    }

    private static void initSession() {
        Configuration configuration = new AnnotationConfiguration();
        configuration.addFile("hibernate.cfg.xml");
        configuration.addFile("hibernate-mappedClasses.cfg.xml");
        configuration.configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public static void injectSession(Session session) {
        HibernateSessionManager.session = session;
    }
}
