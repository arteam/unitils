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
package org.unitils.dbmaintainer.util.ant;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tools.ant.Task;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.util.ConfigUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseDatabaseTask extends Task {

	protected DbSupport createDbSupport(DatabaseType database) {
    	Properties configuration = new ConfigurationLoader().getDefaultConfiguration();
    	
    	BasicDataSource dataSource = new BasicDataSource();
    	dataSource.setDriverClassName(database.getDriverClassName());
    	dataSource.setUrl(database.getUrl());
    	dataSource.setUsername(database.getUserName());
    	dataSource.setPassword(database.getPassword());
    	
    	String defaultSchemaName;
		Set<String> schemaNames;
		if (database.getDefaultSchemaName() == null) {
			defaultSchemaName = database.getUserName();
			schemaNames = Collections.singleton(defaultSchemaName);
		} else {
			defaultSchemaName = database.getDefaultSchemaName();
			schemaNames = database.getSchemaNames();
		}
		
		DbSupport dbSupport = ConfigUtils.getInstanceOf(DbSupport.class, configuration, database.getDialect());
		dbSupport.init(configuration, new DefaultSQLHandler(), dataSource, database.getName(), defaultSchemaName, schemaNames);
		return dbSupport;
	}
}
