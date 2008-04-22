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
package org.unitils.orm.common.spring;

import org.unitils.orm.common.util.ConfiguredOrmPersistenceUnit;

/**
 * Defines the contract for implementations that retrieve an ORM persistence unit from the spring 
 * <code>ApplicationContext</code> that is configured for this class (if any)
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 * @param <ORMPU> Type of the persistence unit
 * @param <ORMCONFOBJ> Type of the configuration object
 */
public interface OrmSpringSupport<ORMPU, ORMCONFOBJ> {

	
	/**
	 * @param testObject The test instance, not null
	 * @return True if an <code>ApplicationContext</code> is associated with the given test object, 
	 * and if a persistence unit of the type supported by this interface's implementation is available
	 */
	boolean isPersistenceUnitConfiguredInSpring(Object testObject);
	
	
	/**
	 * @param testObject The test instance, not null
	 * @return An instance of {@link ConfiguredOrmPersistenceUnit} that wraps the persistence unit and the
	 * configuration object
	 */
	ConfiguredOrmPersistenceUnit<ORMPU, ORMCONFOBJ> getConfiguredPersistenceUnit(Object testObject);
}
