package org.unitils.sample.eshop.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * 
 */
public class HibernateDao {

    protected Session getSession() {
        return HibernateSessionManager.getSession();
    }
}
