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
import org.hibernate.classic.Session;
import org.hibernate.context.CurrentSessionContext;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * Simple implementation of <code>CurrentSessionContext</code> that manages a single open session.
 * If the session was closed, a new one is opened.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SimpleCurrentSessionContext implements CurrentSessionContext {

    /* The session factory */
    protected SessionFactoryImplementor factory;

    /* The current session, if there is one */
    private Session currentSession;


    /**
     * Creates a context.
     *
     * @param factory The session factory to use for opening new sessions, not null
     */
    public SimpleCurrentSessionContext(SessionFactoryImplementor factory) {
        this.factory = factory;
    }


    /**
     * Gets the current session. If there is no current session or the current session is no
     * longer open, a new one is opened.
     *
     * @return The current session, not null
     */
    public Session currentSession() throws HibernateException {
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = factory.openSession();
        }
        return currentSession;
    }

}
