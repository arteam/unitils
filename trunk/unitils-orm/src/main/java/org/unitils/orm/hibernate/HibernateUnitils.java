/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.orm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.unitils.core.Unitils;
import org.unitils.orm.hibernate.annotation.HibernateSessionFactory;

/**
 * Utility facade for handling Hibernate related stuff such as asserting whether the mappings correspond to the actual
 * structure of the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class HibernateUnitils {


    /**
     * Checks if the mapping of the Hibernate managed objects with the database is still correct for the configurations
     * that are loaded for the current test.
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     */
    public static void assertMappingWithDatabaseConsistent(Object testInstance) {
        getHibernateModule().assertMappingWithDatabaseConsistent(testInstance);
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database, without passing through the currently
     * active hibernate session.
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     */
    public static void flushDatabaseUpdates(Object testInstance) {
        getHibernateModule().flushDatabaseUpdates(testInstance);
    }


    /**
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     * @return The <code>SessionFactory</code> configured for the current test object (spring or using
     *         the {@link HibernateSessionFactory} annotation.
     */
    public static SessionFactory getSessionFactory(Object testInstance) {
        return getHibernateModule().getPersistenceUnit(testInstance);
    }


    /**
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     * @return A <code>Session</code> associated with the current transaction. This method returns the
     *         same <code>Session</code> during the course of a transaction.
     */
    public static Session getSession(Object testInstance) {
        return getHibernateModule().getPersistenceContext(testInstance);
    }

    /**
     * @return The {@link HibernateModule}
     */
    private static HibernateModule getHibernateModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(HibernateModule.class);
    }

}
