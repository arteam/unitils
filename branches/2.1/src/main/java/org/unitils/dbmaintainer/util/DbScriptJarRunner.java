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
package org.unitils.dbmaintainer.util;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.dbmaintainer.DBMaintainer;

import java.util.Map;
import java.util.Properties;

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
	 * @param defaultDbSupport 
	 * @param nameDbSupportMap 
	 * @param extensions 
	 */
	public DbScriptJarRunner(DbSupport defaultDbSupport, Map<String, DbSupport> nameDbSupportMap, String extensions) {
		super(defaultDbSupport, nameDbSupportMap, extensions);
	}

	
	/**
	 * Executes the jar file with the given file name
	 * 
	 * @param jarFileName Name of the jar file to be applied
	 */
	public void executeJar(String jarFileName) {
		Properties configuration = getDbMaintainerConfiguration(jarFileName);
		
		DBMaintainer dbMaintainer = new DBMaintainer(configuration, new DefaultSQLHandler(), defaultDbSupport, nameDbSupportMap);
		dbMaintainer.updateDatabase();
	}
}
