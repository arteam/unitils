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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper for a JPA entity manager that will intercept all opened entity managers. These entity
 * managers can later be retrieved for e.g. closing them or flushing their updates to the database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class EntityManagerInterceptingEntityManagerFactory implements EntityManagerFactory {

	/* The logger instance for this class */
    private static Log logger = LogFactory.getLog(EntityManagerInterceptingEntityManagerFactory.class);
	
	/**
	 * The entity manager factory that is proxied
	 */
	private EntityManagerFactory targetEntityManagerFactory;
	
	/**
	 * The intercepted entity managers
	 */
	private Set<EntityManager> entityManagers = new HashSet<EntityManager>();
	
	
	/**
	 * Constructs a new instance that proxies the given target instance
	 * 
	 * @param target The actual <code>EntityManagerFactory</code>, not null
	 */
	public EntityManagerInterceptingEntityManagerFactory(EntityManagerFactory target) {
		this.targetEntityManagerFactory = target;
	}
	
	
	/**
	 * Simply delegates the call
	 */
	public void close() {
		targetEntityManagerFactory.close();
	}

	
	/**
	 * Delegates the call and stores the entity manager which is obtained
	 */
	public EntityManager createEntityManager() {
		EntityManager entityManager = targetEntityManagerFactory.createEntityManager();
		entityManagers.add(entityManager);
		return entityManager;
	}

	
	/**
	 * Delegates the call and stores the entity manager which is obtained
	 */
	public EntityManager createEntityManager(Map properties) {
		EntityManager entityManager = targetEntityManagerFactory.createEntityManager(properties);
		entityManagers.add(entityManager);
		return entityManager;
	}

	/**
	 * Simply delegates the call
	 */
	public boolean isOpen() {
		return targetEntityManagerFactory.isOpen();
	}
	
	
	public Set<EntityManager> getActiveEntityManagers() {
		return entityManagers;
	}

	
    /**
     * Removes all intercepted entity managers
     */
    public void clearInterceptedEntityManagers() {
		entityManagers.clear();
	}
	
}
