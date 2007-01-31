package org.unitils.hibernate.util;

import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * todo violates CurrentSessionContext contract: is not thread safe. Should be fixed by making unitils completely
 * tread safe.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateCurrentSessionContext implements CurrentSessionContext {

    protected SessionFactoryImplementor factory;

    private Session currentSession;

    public HibernateCurrentSessionContext(SessionFactoryImplementor factory) {
        this.factory = factory;
    }

    public Session currentSession() throws HibernateException {
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = factory.openSession();
        }
        return currentSession;
    }


}
