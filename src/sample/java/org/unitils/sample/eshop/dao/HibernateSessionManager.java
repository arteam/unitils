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
