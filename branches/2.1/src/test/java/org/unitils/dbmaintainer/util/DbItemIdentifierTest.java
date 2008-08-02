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

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.util.TestUtils;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public final class DbItemIdentifierTest {

	DbSupport dbSupport;
	
	Map<String, DbSupport> dbNameDbSupportMap;

	@Before
	public void init() {
		Properties configuration = new ConfigurationLoader().loadConfiguration();
		dbSupport = TestUtils.getDefaultDbSupport(configuration);
		dbNameDbSupportMap = new HashMap<String, DbSupport>();
		dbNameDbSupportMap.put("mydatabase", dbSupport);
	}
	
	@Test
	public void parseItemIdentifier_itemOnly() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier("test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier("public", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseItemIdentifier_schemaAndItem() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier("myschema.test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier("myschema", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseItemIdentifier_databaseSchemaAndItem() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseItemIdentifier("mydatabase.myschema.test", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getItemIdentifier("myschema", "test", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
	
	@Test
	public void parseSchemaschemaOnly() throws Exception {
		DbItemIdentifier parsedIdentifier = DbItemIdentifier.parseSchemaIdentifier("public", dbSupport, dbNameDbSupportMap);
		DbItemIdentifier identifier = DbItemIdentifier.getSchemaIdentifier("public", dbSupport);
		assertEquals(identifier, parsedIdentifier);
	}
}
