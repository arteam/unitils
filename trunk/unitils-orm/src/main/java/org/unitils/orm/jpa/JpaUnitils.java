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
package org.unitils.orm.jpa;

import org.unitils.core.Unitils;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Utility facade for handling JPA related stuff such as asserting whether the mappings correspond to the actual
 * structure of the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaUnitils {


    /**
     * Checks if the mapping of the JPA entities with the database is still correct for the configurations
     * that are loaded for the current test.
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     */
    public static void assertMappingWithDatabaseConsistent(Object testInstance) {
        getJpaModule().assertMappingWithDatabaseConsistent(testInstance);
    }


    /**
     * Flushes all pending entity manager updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database, without passing through the currently active
     * <code>EntityManager</code>
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     */
    public static void flushDatabaseUpdates(Object testInstance) {
        getJpaModule().flushDatabaseUpdates(testInstance);
    }


    /**
     * For the given target object, injects the active, transactional <code>EntityManager</code> into
     * fields or methods annotated with <code>javax.persistence.PersistenceContext</code>
     *
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     * @param target       The target instance for the injection, not null
     */
    public static void injectEntityManagerInto(Object target, Object testInstance) {
        getJpaModule().injectJpaResourcesInto(testInstance, target);
    }


    /**
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     * @return The <code>EntityManagerFactory</code> configured for the current test object (spring or using
     *         the {@link JpaEntityManagerFactory} annotation.
     */
    public static EntityManagerFactory getEntityManagerFactory(Object testInstance) {
        return getJpaModule().getPersistenceUnit(testInstance);
    }


    /**
     * @param testInstance The current test instance (e.g. this if your in the test), not null
     * @return An <code>EntityManager</code> associated with the current transaction. This method returns the
     *         same <code>EntityManager</code> during the course of a transaction.
     */
    public static EntityManager getEntityManager(Object testInstance) {
        return getJpaModule().getPersistenceContext(testInstance);
    }


    /**
     * @return The {@link JpaModule}
     */
    private static JpaModule getJpaModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(JpaModule.class);
    }
}
