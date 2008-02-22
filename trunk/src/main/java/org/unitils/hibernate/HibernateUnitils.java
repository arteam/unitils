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
package org.unitils.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.hibernate.annotation.HibernateSessionFactory;

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
     */
    public static void assertMappingWithDatabaseConsistent() {
        Object testObject = getTestObject();
        getHibernateModule().assertMappingWithDatabaseConsistent(testObject);
    }


    /**
     * Flushes all pending Hibernate updates to the database. This method is useful when the effect
     * of updates needs to be checked directly on the database, without passing through the currently
     * active hibernate session.
     */
    public static void flushDatabaseUpdates() {
    	Object testObject = getTestObject();
    	getHibernateModule().flushDatabaseUpdates(testObject);
    }
    
    
    /**
     * @return The <code>SessionFactory</code> configured for the current test object (spring or using
     * the {@link HibernateSessionFactory} annotation. 
     */
    public static SessionFactory getSessionFactory() {
    	return getHibernateModule().getPersistenceUnit(getTestObject());
    }
    
    
    /**
     * @return A <code>Session</code> associated with the current transaction. This method returns the 
     * same <code>Session</code> during the course of a transaction.
     */
    public static Session getSession() {
    	return getHibernateModule().getPersistenceContext(getTestObject());
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
	 * @return The {@link HibernateModule}
	 */
	private static HibernateModule getHibernateModule() {
		return Unitils.getInstance().getModulesRepository().getModuleOfType(HibernateModule.class);
	}

}
