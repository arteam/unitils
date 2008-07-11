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
package org.unitils.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DefaultDbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.dbmaintainer.clean.impl.DefaultDBCleaner;
import org.unitils.dbmaintainer.clean.impl.DefaultDBClearer;
import org.unitils.dbmaintainer.script.impl.DefaultScriptRunner;
import org.unitils.dbmaintainer.structure.impl.DefaultConstraintsDisabler;
import org.unitils.dbmaintainer.structure.impl.DefaultSequenceUpdater;
import org.unitils.dbmaintainer.structure.impl.DtdDataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.impl.XsdDataSetStructureGenerator;
import org.unitils.dbmaintainer.version.impl.DefaultExecutedScriptInfoSource;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class TestUtils {

	public static DbSupport getDefaultDbSupport(Properties configuration) {
		SQLHandler sqlHandler = new DefaultSQLHandler();
		DefaultDbSupportFactory defaultDbSupportFactory = new DefaultDbSupportFactory();
		defaultDbSupportFactory.init(configuration);
		return defaultDbSupportFactory.createDefaultDbSupport(sqlHandler);
	}
	
	
	public static DefaultDBClearer getDefaultDBClearer(Properties configuration, DbSupport dbSupport) {
		DefaultDBClearer defaultDbClearer = new DefaultDBClearer();
		defaultDbClearer.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultDbClearer;
	}
	
	
	public static DefaultDBCleaner getDefaultDBCleaner(Properties configuration, DbSupport dbSupport) {
		DefaultDBCleaner defaultDbCleaner = new DefaultDBCleaner();
		defaultDbCleaner.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultDbCleaner;
	}
	
	
	public static DefaultScriptRunner getDefaultScriptRunner(Properties configuration, DbSupport dbSupport) {
		DefaultScriptRunner defaultScriptRunner = new DefaultScriptRunner();
		defaultScriptRunner.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultScriptRunner;
	}
	
	public static DefaultConstraintsDisabler getDefaultConstraintsDisabler(Properties configuration, DbSupport dbSupport) {
		DefaultConstraintsDisabler defaultConstraintsDisabler = new DefaultConstraintsDisabler();
		defaultConstraintsDisabler.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultConstraintsDisabler;
	}
	
	
	public static DefaultSequenceUpdater getDefaultSequenceUpdater(Properties configuration, DbSupport dbSupport) {
		DefaultSequenceUpdater defaultSequenceUpdater = new DefaultSequenceUpdater();
		defaultSequenceUpdater.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultSequenceUpdater;
	}
	
	
	public static DefaultExecutedScriptInfoSource getDefaultExecutedScriptInfoSource(Properties configuration, DbSupport dbSupport) {
		DefaultExecutedScriptInfoSource defaultExecutedScriptInfoSource = new DefaultExecutedScriptInfoSource();
		defaultExecutedScriptInfoSource.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return defaultExecutedScriptInfoSource;
	}
	

	public static XsdDataSetStructureGenerator getXsdDataSetStructureGenerator(Properties configuration, DbSupport dbSupport) {
		XsdDataSetStructureGenerator xsdDataSetStructureGenerator = new XsdDataSetStructureGenerator();
		xsdDataSetStructureGenerator.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return xsdDataSetStructureGenerator;
	}
	
	
	public static DtdDataSetStructureGenerator getDtdDataSetStructureGenerator(Properties configuration, DbSupport dbSupport) {
		DtdDataSetStructureGenerator dtdDataSetStructureGenerator = new DtdDataSetStructureGenerator();
		dtdDataSetStructureGenerator.init(configuration, new DefaultSQLHandler(), dbSupport, getDbNameDbSupportMap(dbSupport));
		return dtdDataSetStructureGenerator;
	}

	private static Map<String, DbSupport> getDbNameDbSupportMap(DbSupport dbSupport) {
		Map<String, DbSupport> dbNameDbSupportMap = new HashMap<String, DbSupport>();
		dbNameDbSupportMap.put(null, dbSupport);
		return dbNameDbSupportMap;
	}
}
