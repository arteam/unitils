/*
 * Copyright 2006-2008,  Unitils.org
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
 *
 * $Id$
 */
package org.unitils.dbmaintainer.util.ant;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.unitils.util.CollectionUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DatabaseType {

	private String name;
	
	private String dialect;
	
	private String driverClassName;
	
	private String url;
	
	private String userName;
	
	private String password;
	
	private String defaultSchemaName;
	
	private Set<String> schemaNames;

	public DatabaseType() {
	}

	public String getName() {
		return name;
	}

	public String getDialect() {
		return dialect;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getDefaultSchemaName() {
		return defaultSchemaName;
	}

	public Set<String> getSchemaNames() {
		return schemaNames;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setSchemaNames(String schemaNamesCommaSeparated) {
		if (schemaNamesCommaSeparated == null) {
			defaultSchemaName = null;
			schemaNames = Collections.emptySet();
		} else {
			String[] schemas = StringUtils.split(schemaNamesCommaSeparated, ',');
			schemaNames = CollectionUtils.asSet(schemas);
			if (schemas.length > 0) {
				defaultSchemaName = schemas[0];
			}
		}
	}
	
}
