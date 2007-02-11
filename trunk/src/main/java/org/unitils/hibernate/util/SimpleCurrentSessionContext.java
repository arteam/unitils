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
 * todo javadoc
 * <p/>
 * todo violates CurrentSessionContext contract: is not thread safe. Should be fixed by making unitils completely
 * tread safe.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SimpleCurrentSessionContext implements CurrentSessionContext {

    protected SessionFactoryImplementor factory;

    private Session currentSession;


    public SimpleCurrentSessionContext(SessionFactoryImplementor factory) {
        this.factory = factory;
    }


    public Session currentSession() throws HibernateException {
        if (currentSession == null || !currentSession.isOpen()) {
            currentSession = factory.openSession();
        }
        return currentSession;
    }

}
