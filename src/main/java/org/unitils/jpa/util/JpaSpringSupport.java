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
package org.unitils.jpa.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;

/**
 * Interface for {@link JpaSpringSupportImpl} containing JPA and {@link JpaModule} related actions 
 * for the {@link SpringModule}. This interface doesn't refer to spring interfaces, so that there is no
 * classloading problem when loading the {@link JpaModule} when spring is not in the classpath.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface JpaSpringSupport {

	/**
	 * @param testObject The test instance, not null
	 * @return true if an EntityManagerFactory is configured in the spring ApplicationContext that is associated
	 * with the given testObject
	 */
    boolean isEntityManagerFactoryConfiguredInSpring(Object testObject);
    
    
    /**
     * Returns the JPA <code>EntityManagerFactory</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>EntityManagerFactory</code> configured in spring for the given testObject, null if no such
     *         <code>EntityManagerFactory</code> was configured.
     */
    EntityManagerFactory getEntityManagerFactory(Object testObject);

    
    /**
     * Returns the JPA implementation specific configuration object that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The JPA implementation specific configuration object configured in spring for the given testObject, null if no such
     *         JPA implementation specific configuration object is available.
     */
    Object getConfigurationObject(Object testObject);

    
    /**
     * @param testObject The test instance, not null
     * @return The enity manager that's currently active, if any
     */
	EntityManager getActiveEntityManager(Object testObject);

}
