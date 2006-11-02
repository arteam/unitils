package org.untils.sample.eshop;

import org.unitils.UnitilsJUnit3;
import org.unitils.sample.eshop.dao.HibernateSessionManager;
import org.unitils.hibernate.annotation.HibernateTest;
import org.unitils.hibernate.annotation.HibernateSession;
import org.hibernate.Session;

/**
 * 
 */
@HibernateTest
public abstract class BaseHibernateDaoTest extends UnitilsJUnit3 {

    @HibernateSession
    public void injectHibernateSession(Session session) {
        HibernateSessionManager.injectSession(session);
    }
}
