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
package org.unitils.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Interface for {@link HibernateSpringSupportImpl} containing Hibernate and {@link HibernateModule} related actions 
 * for the {@link SpringModule}. This interface doesn't refer to spring interfaces, so that there is no
 * classloading problem when loading the {@link HibernateModule} when spring is not in the classpath.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface HibernateSpringSupport {

	
	/**
	 * @param testObject The test instance, not null
	 * @return true if a SessionFactory is configured in the spring ApplicationContext that is associated
	 * with the given testObject
	 */
    boolean isSessionFactoryConfiguredInSpring(Object testObject);
    
    
    /**
     * Returns the hibernate <code>SessionFactory</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>SessionFactory</code> configured in spring for the given testObject, null if no such
     *         <code>SessionFactory</code> was configured.
     */
    SessionFactory getSessionFactory(Object testObject);

    
    /**
     * Returns the hibernate <code>Configuration</code> that was configured in spring for the given testObject, if any
     *
     * @param testObject The test instance, not null
     * @return The <code>Configuration</code> configured in spring for the given testObject, null if no such
     *         <code>Configuration</code> was configured.
     */
    Configuration getConfiguration(Object testObject);

    
    /**
     * @param testObject The test instance, not null
     * @return The hibernate that's currently active, if any
     */
	Session getActiveSession(Object testObject);

}
