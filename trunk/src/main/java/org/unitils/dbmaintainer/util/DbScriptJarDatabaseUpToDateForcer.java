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

import java.util.Map;
import java.util.Properties;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.dbmaintainer.DBMaintainer;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbScriptJarDatabaseUpToDateForcer extends DbScriptJarHandler {

	public DbScriptJarDatabaseUpToDateForcer(DbSupport defaultDbSupport, Map<String, DbSupport> nameDbSupportMap) {
		super(defaultDbSupport, nameDbSupportMap);
	}

	public void resetDatabaseState(String jarFileName) {
		Properties configuration = getDbMaintainerConfiguration(jarFileName);
		
		DBMaintainer dbMaintainer = new DBMaintainer(configuration, new DefaultSQLHandler(), defaultDbSupport, nameDbSupportMap);
		dbMaintainer.resetDatabaseState();
	}
}
