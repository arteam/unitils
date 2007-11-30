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

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.unitils.core.util.AnnotatedInstanceManager;
import org.unitils.hibernate.annotation.HibernateSessionFactory;
import org.unitils.jpa.annotation.JpaEntityManagerFactory;

abstract public class EntityManagerFactoryManager extends AnnotatedInstanceManager<EntityManagerInterceptingEntityManagerFactory, JpaEntityManagerFactory> {

	protected EntityManagerFactoryManager() {
		super(EntityManagerInterceptingEntityManagerFactory.class, JpaEntityManagerFactory.class);
	}
	
	/**
     * Gets the JPA entity manager factory for the given test as described in the class javadoc. A UnitilsException will
     * be thrown if no configuration could be retrieved or created.
     *
     * @param testObject The test object, not null
     * @return The JPA entity manager factory, not null
     */
    public EntityManagerInterceptingEntityManagerFactory getEntityManagerFactory(Object testObject) {
    	return getInstance(testObject);
    }

    
    /**
     * Gets the locations that are specified for the given {@link HibernateSessionFactory} annotation. An array with
     * 1 empty string should be considered to be empty and null should be returned.
     *
     * @param annotation The annotation, not null
     * @return The locations, null if no values were specified
     */
    @Override
    protected List<String> getAnnotationValues(JpaEntityManagerFactory annotation) {
        String persistenceUnit = annotation.persistenceUnit();
        String[] configFiles = annotation.configFiles();
        if ("".equals(persistenceUnit) && configFiles.length == 0) {
        	return Arrays.asList();
        }
        List<String> values = new ArrayList<String>();
        values.add(persistenceUnit);
        values.addAll(asList(configFiles));
        return values;
    }
    
    
    abstract public void invalidateEntityManagerFactory(Class<?>... classes);


}
