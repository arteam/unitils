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
package org.unitils.database.util;

import java.util.Set;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class UnitilsDataSource {

	private String name;
	
	private DataSource dataSource;
	
	private String defaultSchemaName;
	
	private Set<String> schemaNames;

	
	public UnitilsDataSource(String name, DataSource dataSource, String defaultSchemaName, Set<String> schemaNames) {
		this.name = name;
		this.dataSource = dataSource;
	}

	
	public UnitilsDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	public String getName() {
		return name;
	}

	
	public DataSource getDataSource() {
		return dataSource;
	}


	public String getDefaultSchemaName() {
		return defaultSchemaName;
	}


	public Set<String> getSchemaNames() {
		return schemaNames;
	}

}
