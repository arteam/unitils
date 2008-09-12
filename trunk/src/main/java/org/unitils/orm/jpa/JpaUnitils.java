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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.orm.jpa.annotation.JpaEntityManagerFactory;

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
     */
    public static void assertMappingWithDatabaseConsistent() {
        getJpaModule().assertMappingWithDatabaseConsistent(getTestObject());
    }


    /**
     * Flushes all pending entity manager updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database, without passing through the currently active
     * <code>EntityManager</code>
     */
    public static void flushDatabaseUpdates() {
    	getJpaModule().flushDatabaseUpdates(getTestObject());
    }
    
    
    /**
     * For the given target object, injects the active, transactional <code>EntityManager</code> into 
     * fields or methods annotated with <code>javax.persistence.PersistenceContext</code>
     * 
     * @param target
     */
    public static void injectEntityManagerInto(Object target) {
    	getJpaModule().injectJpaResourcesInto(getTestObject(), target);
    }
    
    
    /**
     * @return The <code>EntityManagerFactory</code> configured for the current test object (spring or using
     * the {@link JpaEntityManagerFactory} annotation. 
     */
    public static EntityManagerFactory getEntityManagerFactory() {
    	return getJpaModule().getPersistenceUnit(getTestObject());
    }
    
    
    /**
     * @return An <code>EntityManager</code> associated with the current transaction. This method returns the 
     * same <code>EntityManager</code> during the course of a transaction.
     */
    public static EntityManager getEntityManager() {
    	return getJpaModule().getPersistenceContext(getTestObject());
    }


    /**
	 * @return The current test object
	 */
	private static Object getTestObject() {
		Object testObject = Unitils.getInstance().getTestContext().getTestObject();
        if (testObject == null) {
            throw new UnitilsException("No current test found in context. Unable to execute specified operation");
        }
		return testObject;
	}
    
    
    /**
	 * @return The {@link JpaModule}
	 */
	private static JpaModule getJpaModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(JpaModule.class);
	}
}
