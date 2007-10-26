/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.jpa;

import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.HibernateModule;

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
     * that are loaded for the current test. This method assumes that the {@link JpaModule} is enabled and
     * correctly configured.
     */
    public static void assertMappingWithDatabaseConsistent() {
        Object testObject = getTestObject();
        getJpaModule().assertMappingWithDatabaseConsistent(testObject);
    }


    /**
     * Closes all open entity managers.
     */
    public static void closeEntityManagers() {
    	Object testObject = getTestObject();
		getJpaModule().closeEntityManagers(testObject);
    }
    
    
    /**
     * Flushes all pending entity manager updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database.
     */
    public static void flushDatabaseUpdates() {
    	Object testObject = getTestObject();
    	getJpaModule().flushDatabaseUpdates(testObject);
    }


    /**
     * Forces the reloading of the EntityManagerFactory configurations the next time that it is requested. If classes are given
     * only EntityManagerFactory configurations that are linked to those classes will be reset. If no classes are given, all cached
     * EntityManager configurations will be reset.
     *
     * @param classes The classes for which to reset the configs, null for all configs
     */
    public static void invalidateEntityManagerFactoryConfiguration(Class<?>... classes) {
        getJpaModule().invalidateConfiguration(classes);
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
