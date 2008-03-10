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

import java.lang.reflect.Method;

import org.unitils.orm.common.util.OrmConfig;
import org.unitils.util.CollectionUtils;

/**
 * Value object representing a test object's configuration of a JPA <code>EntityManagerFactory</code>
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class JpaConfig extends OrmConfig {

	/**
	 * The name of the persistence unit, as defined in the persistence config file
	 */
	private String persistenceUnitName;

	
	/**
	 * Creates a new instance
	 * 
	 * @param persistenceUnitName The name of the persistence unit, as defined in the persistence config file
	 * @param configFile The name of the persistence.xml file. May be null: in this case, we try to find a 
	 * persistence.xml file in the default location META-INF/persistence.xml
	 * @param configMethod Custom configuration method, null if not available
	 */
	public JpaConfig(String persistenceUnitName, String configFile,	Method configMethod) {
		super(CollectionUtils.asSet(configFile), configMethod);
		this.persistenceUnitName = persistenceUnitName;
	}

	
	/**
	 * @return The name of the persistence unit, defined in the persistence config file
	 */
	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((persistenceUnitName == null) ? 0 : persistenceUnitName
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JpaConfig other = (JpaConfig) obj;
		if (persistenceUnitName == null) {
			if (other.persistenceUnitName != null)
				return false;
		} else if (!persistenceUnitName.equals(other.persistenceUnitName))
			return false;
		return true;
	}
	
	
	
}
