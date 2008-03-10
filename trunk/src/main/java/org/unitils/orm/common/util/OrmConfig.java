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
package org.unitils.orm.common.util;

import java.lang.reflect.Method;
import java.util.Set;

import org.unitils.core.util.ResourceConfig;

/**
 * Object wrapping all configuration needed for defining a persistence unit for a test object
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OrmConfig implements ResourceConfig {

	/**
	 * Config files used for configuring this ORM persistence unit
	 */
	private Set<String> configFiles;

	
	/**
	 * Custom configuration method that performs additional programmatic configuration
	 */
	private Method configMethod;

	
	/**
	 * Creates a new instance
	 *  
	 * @param configFiles
	 * @param configMethod
	 */
	public OrmConfig(Set<String> configFiles, Method configMethod) {
		this.configFiles = configFiles;
		this.configMethod = configMethod;
	}

	
	public Set<String> getConfigFiles() {
		return configFiles;
	}

	
	public Method getConfigMethod() {
		return configMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configFiles == null) ? 0 : configFiles.hashCode());
		result = prime * result
				+ ((configMethod == null) ? 0 : configMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OrmConfig other = (OrmConfig) obj;
		if (configFiles == null) {
			if (other.configFiles != null)
				return false;
		} else if (!configFiles.equals(other.configFiles))
			return false;
		if (configMethod == null) {
			if (other.configMethod != null)
				return false;
		} else if (!configMethod.equals(other.configMethod))
			return false;
		return true;
	}

}
