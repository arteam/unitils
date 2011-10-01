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
package org.unitils.orm.common.util;


/**
 * Defines the contract for implementations that can load a persistence unit given a persistence unit configuration object.
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 * @param <ORMPU> Type of the persistence unit
 * @param <ORMCONFOBJ> Type of the implementation specific configuration object
 * @param <ORMCFG> Subtype of {@link OrmConfig} that is used
 */
public interface OrmPersistenceUnitLoader<ORMPU, ORMCONFOBJ, ORMCFG extends OrmConfig> {

	
	/**
	 * @param testObject The test object, not null
	 * @param ormConfig The persistence unit configuration, not null
	 * @return An instance of {@link ConfiguredOrmPersistenceUnit} that wraps the persistence unit and an
	 * implementation specific configuration object
	 */
	public ConfiguredOrmPersistenceUnit<ORMPU, ORMCONFOBJ> getConfiguredOrmPersistenceUnit(Object testObject, ORMCFG ormConfig);
}
