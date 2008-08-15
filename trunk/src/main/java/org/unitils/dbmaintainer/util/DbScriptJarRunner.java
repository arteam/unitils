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
package org.unitils.dbmaintainer.util;

import java.util.Properties;

import javax.sql.DataSource;

import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.dbmaintainer.DBMaintainer;

/**
 * Enables applying a jar file that contains database update scripts to a database. Only the scripts that
 * were added after the latest update or repeatable (non-indexed) scripts that were changed since last update
 * are applied.
 * 
 * @author Filip Neven
 * @author Alexander Snaps
 */
public class DbScriptJarRunner extends DbScriptJarHandler {
	
	/**
	 * Creates an instance of this class.
	 * 
	 * @param dataSource The database to which the updates are applied 
	 * @param databaseDialect Indicator of the database type, that defines the SQL dialect that must be used for this database
	 * @param schemaName Name of the database schema that is used
	 */
	public DbScriptJarRunner(DataSource dataSource, String databaseDialect,	String schemaName) {
		super(dataSource, databaseDialect, schemaName);
	}

	
	/**
	 * Executes the jar file with the given file name
	 * 
	 * @param jarFileName Name of the jar file to be applied
	 */
	public void executeJar(String jarFileName) {
		Properties configuration = getDbMaintainerConfiguration(jarFileName);
		
		DBMaintainer dbMaintainer = new DBMaintainer(configuration, new DefaultSQLHandler(dataSource));
		dbMaintainer.updateDatabase();
	}
}
