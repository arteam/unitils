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
package org.unitils.dbmaintainer.clean.impl;

import static org.unitils.util.PropertyUtils.getStringList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;

/**
 * Implementation of {@link DBClearer}. This implementation individually drops every table, view, constraint, trigger
 * and sequence in the database. A list of tables, views, ... that should be preserverd can be specified using the
 * property {@link #PROPKEY_PRESERVE_TABLES}. <p/> NOTE: FK constraints give problems in MySQL and Derby The cascade in
 * drop table A cascade; does not work in MySQL-5.0 The DBMaintainer will first remove all constraints before calling
 * the db clearer
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBClearer extends BaseDatabaseTask implements DBClearer {

	/**
	 * The key of the property that specifies of which schemas nothing should be dropped
	 */
	public static final String PROPKEY_PRESERVE_SCHEMAS = "dbMaintainer.preserve.schemas";

	/**
	 * The key of the property that specifies which tables should not be dropped
	 */
	public static final String PROPKEY_PRESERVE_TABLES = "dbMaintainer.preserve.tables";

	/**
	 * The key of the property that specifies which views should not be dropped
	 */
	public static final String PROPKEY_PRESERVE_VIEWS = "dbMaintainer.preserve.views";

	/**
	 * The key of the property that specifies which synonyms should not be dropped
	 */
	public static final String PROPKEY_PRESERVE_SYNONYMS = "dbMaintainer.preserve.synonyms";

	/**
	 * The key of the property that specifies which sequences should not be dropped
	 */
	public static final String PROPKEY_PRESERVE_SEQUENCES = "dbMaintainer.preserve.sequences";


	/* The logger instance for this class */
	private static Log logger = LogFactory.getLog(DefaultDBClearer.class);

	/**
	 * Names of schemas that should left untouched.
	 */
	protected Set<String> schemasToPreserve;

	/**
	 * Names of tables that should not be dropped per schema.
	 */
	protected Map<String, Set<String>> tablesToPreserve;

	/**
	 * Names of views that should not be dropped per schema.
	 */
	protected Map<String, Set<String>> viewsToPreserve;

	/**
	 * Names of synonyms that should not be dropped per schema.
	 */
	protected Map<String, Set<String>> synonymsToPreserve;

	/**
	 * Names of sequences that should not be dropped per schema.
	 */
	protected Map<String, Set<String>> sequencesToPreserve;


	/**
	 * Initializes the the DBClearer. The list of database items that should be preserved is retrieved from the given
	 * <code>Configuration</code> object.
	 * 
	 * @param configuration the config, not null
	 */
	@Override
	protected void doInit(Properties configuration) {
		schemasToPreserve = getSchemasToPreserve(configuration);
		tablesToPreserve = getTablesToPreserve(configuration);
		viewsToPreserve = getViewsToPreserve(configuration);
		sequencesToPreserve = getSequencesToPreserve(configuration);
		synonymsToPreserve = getSynonymsToPreserve(configuration);
	}


	/**
	 * Clears the database schemas. This means, all the tables, views, constraints, triggers and sequences are dropped,
	 * so that the database schema is empty. The database items that are configured as items to preserve, are left
	 * untouched.
	 */
	public void clearSchemas() {
		for (DbSupport dbSupport : dbSupports) {
			// check whether schema needs to be preserved
			if (schemasToPreserve.contains(dbSupport.getSchemaName())) {
				continue;
			}
			logger.info("Clearing (dropping) database schema " + dbSupport.getSchemaName());
			dropSynonyms(dbSupport);
			dropViews(dbSupport);
			dropSequences(dbSupport);
			dropTables(dbSupport);
		}
	}


	/**
	 * Drops all tables.
	 * 
	 * @param dbSupport The database support, not null
	 */
	protected void dropTables(DbSupport dbSupport) {
		Set<String> tableNames = dbSupport.getTableNames();
		Set<String> schemaTablesToPreserve = tablesToPreserve.get(dbSupport.getSchemaName());
		for (String tableName : tableNames) {
			// check whether table needs to be preserved
			if (schemaTablesToPreserve != null && schemaTablesToPreserve.contains(tableName)) {
				continue;
			}
			logger.debug("Dropping table " + tableName + " in database schema " + dbSupport.getSchemaName());
			dbSupport.dropTable(tableName);
		}
	}


	/**
	 * Drops all views.
	 * 
	 * @param dbSupport The database support, not null
	 */
	protected void dropViews(DbSupport dbSupport) {
		Set<String> viewNames = dbSupport.getViewNames();
		Set<String> schemaViewsToPreserve = viewsToPreserve.get(dbSupport.getSchemaName());
		for (String viewName : viewNames) {
			// check whether view needs to be preserved
			if (schemaViewsToPreserve != null && schemaViewsToPreserve.contains(viewName)) {
				continue;
			}
			logger.debug("Dropping view " + viewName + " in database schema " + dbSupport.getSchemaName());
			dbSupport.dropView(viewName);
		}
	}


	/**
	 * Drops all synonyms
	 * 
	 * @param dbSupport The database support, not null
	 */
	protected void dropSynonyms(DbSupport dbSupport) {
		if (!dbSupport.supportsSynonyms()) {
			return;
		}
		Set<String> synonymNames = dbSupport.getSynonymNames();
		Set<String> schemaSynonymsToPreserve = synonymsToPreserve.get(dbSupport.getSchemaName());
		for (String synonymName : synonymNames) {
			// check whether table needs to be preserved
			if (schemaSynonymsToPreserve != null && schemaSynonymsToPreserve.contains(synonymName)) {
				continue;
			}
			logger.debug("Dropping synonym " + synonymName + " in database schema " + dbSupport.getSchemaName());
			dbSupport.dropSynonym(synonymName);
		}
	}


	/**
	 * Drops all sequences
	 * 
	 * @param dbSupport The database support, not null
	 */
	protected void dropSequences(DbSupport dbSupport) {
		if (!dbSupport.supportsSequences()) {
			return;
		}
		Set<String> sequenceNames = dbSupport.getSequenceNames();
		Set<String> schemaSequencesToPreserve = sequencesToPreserve.get(dbSupport.getSchemaName());
		for (String sequenceName : sequenceNames) {
			// check whether sequence needs to be preserved
			if (schemaSequencesToPreserve != null && schemaSequencesToPreserve.contains(sequenceName)) {
				continue;
			}
			logger.debug("Dropping sequence " + sequenceName + " in database schema " + dbSupport.getSchemaName());
			dbSupport.dropSequence(sequenceName);
		}
	}


	/**
	 * Gets the list of all schemas to preserve. The case is corrected if necesary. Quoting a schema name makes it case
	 * sensitive.
	 * 
	 * If a schema name is not defined in the unitils configuration, a UnitilsException is thrown.
	 * 
	 * @param configuration The unitils configuration, not null
	 * @return The schemas to preserve, not null
	 */
	protected Set<String> getSchemasToPreserve(Properties configuration) {
		Set<String> result = new HashSet<String>();

		List<String> schemasToPreserve = getStringList(PROPKEY_PRESERVE_SCHEMAS, configuration);
		for (String schemaToPreserve : schemasToPreserve) {

			boolean found = false;
			for (DbSupport dbSupport : dbSupports) {
				// convert to correct case if needed
				String correctCaseSchemaToPreserve = dbSupport.toCorrectCaseIdentifier(schemaToPreserve);
				if (dbSupport.getSchemaName().equals(correctCaseSchemaToPreserve)) {
					found = true;
					result.add(correctCaseSchemaToPreserve);
					break;
				}
			}
			if (!found) {
				throw new UnitilsException("Schema to preserve does not exist: " + schemaToPreserve + ".\nUnitils cannot determine which schemas need to be preserved. To assure nothing is dropped by mistake, no schemas will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_SCHEMAS + " property.");
			}
		}
		return result;
	}

	/**
	 * Gets the list of all tables to preserve per schema. The case is corrected if necesary. Quoting a table name makes
	 * it case sensitive. If no schema is specified, the tables will be added to the default schema name set.
	 * 
	 * If a table to preserve does not exist, a UnitilsException is thrown.
	 * 
	 * @param configuration The unitils configuration, not null
	 * @return The tables to preserve per schema, not null
	 */
	protected Map<String, Set<String>> getTablesToPreserve(Properties configuration) {
		Map<String, Set<String>> tablesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TABLES, configuration);
		for (Map.Entry<String, Set<String>> entry : tablesToPreserve.entrySet()) {
			String schemaName = entry.getKey();
			Set<String> tableNames = getDbSupport(schemaName).getTableNames();

			for (String tableToPreserve : entry.getValue()) {
				if (!tableNames.contains(tableToPreserve)) {
					throw new UnitilsException("Table to preserve does not exist: " + tableToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which tables need to be preserved. To assure nothing is dropped by mistake, no tables will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_TABLES + " property.");
				}
			}
		}
		return tablesToPreserve;
	}


	/**
	 * Gets the list of all views to preserve per schema. The case is corrected if necesary. Quoting a view name makes
	 * it case sensitive. If no schema is specified, the view will be added to the default schema name set.
	 * 
	 * If a view to preserve does not exist, a UnitilsException is thrown.
	 * 
	 * @param configuration The unitils configuration, not null
	 * @return The views to preserve per schema, not null
	 */
	protected Map<String, Set<String>> getViewsToPreserve(Properties configuration) {
		Map<String, Set<String>> viewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_VIEWS, configuration);
		for (Map.Entry<String, Set<String>> entry : viewsToPreserve.entrySet()) {
			String schemaName = entry.getKey();
			Set<String> viewNames = getDbSupport(schemaName).getViewNames();

			for (String viewToPreserve : entry.getValue()) {
				if (!viewNames.contains(viewToPreserve)) {
					throw new UnitilsException("View to preserve does not exist: " + viewToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which views need to be preserved. To assure nothing is dropped by mistake, no views will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_VIEWS + " property.");
				}
			}
		}
		return viewsToPreserve;
	}


	/**
	 * Gets the list of all sequences to preserve per schema. The case is corrected if necesary. Quoting a sequence name
	 * makes it case sensitive. If no schema is specified, the sequence will be added to the default schema name set.
	 * 
	 * If a sequence to preserve does not exist, a UnitilsException is thrown.
	 * 
	 * @param configuration The unitils configuration, not null
	 * @return The sequences to preserve per schema, not null
	 */
	protected Map<String, Set<String>> getSequencesToPreserve(Properties configuration) {
		Map<String, Set<String>> sequencesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SEQUENCES, configuration);
		for (Map.Entry<String, Set<String>> entry : sequencesToPreserve.entrySet()) {
			String schemaName = entry.getKey();

			DbSupport dbSupport = getDbSupport(schemaName);
			Set<String> sequenceNames;
			if (!dbSupport.supportsSequences()) {
				sequenceNames = new HashSet<String>();
			} else {
				sequenceNames = dbSupport.getSequenceNames();
			}
			for (String sequenceToPreserve : entry.getValue()) {
				if (!sequenceNames.contains(sequenceToPreserve)) {
					throw new UnitilsException("Sequence to preserve does not exist: " + sequenceToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which sequences need to be preserved. To assure nothing is dropped by mistake, no sequences will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_SEQUENCES + " property.");
				}
			}
		}
		return sequencesToPreserve;
	}


	/**
	 * Gets the list of all synonym to preserve per schema. The case is corrected if necesary. Quoting a synonym name
	 * makes it case sensitive. If no schema is specified, the synonym will be added to the default schema name set.
	 * 
	 * If a synonym to preserve does not exist, a UnitilsException is thrown.
	 * 
	 * @param configuration The unitils configuration, not null
	 * @return The synonym to preserve per schema, not null
	 */
	protected Map<String, Set<String>> getSynonymsToPreserve(Properties configuration) {
		Map<String, Set<String>> synonymsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SYNONYMS, configuration);
		for (Map.Entry<String, Set<String>> entry : synonymsToPreserve.entrySet()) {
			String schemaName = entry.getKey();

			DbSupport dbSupport = getDbSupport(schemaName);
			Set<String> synonymNames;
			if (!dbSupport.supportsSynonyms()) {
				synonymNames = new HashSet<String>();
			} else {
				synonymNames = dbSupport.getSynonymNames();
			}
			for (String synonymToPreserve : entry.getValue()) {
				if (!synonymNames.contains(synonymToPreserve)) {
					throw new UnitilsException("Synonym to preserve does not exist: " + synonymToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which synonyms need to be preserved. To assure nothing is dropped by mistake, no synonyms will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_SYNONYMS + " property.");
				}
			}
		}
		return synonymsToPreserve;
	}


	/**
	 * Gets the list of items to preserve per schema. The case is corrected if necesary. Quoting an identifier makes it
	 * case sensitive. If no schema is specified, the identifiers will be added to the default schema name set.
	 * 
	 * @param propertyName The name of the property that defines the items, not null
	 * @param configuration The config, not null
	 * @return The set of items per schema name, not null
	 */
	protected Map<String, Set<String>> getItemsToPreserve(String propertyName, Properties configuration) {
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		List<String> itemsToPreserve = getStringList(propertyName, configuration);
		for (String itemToPreserve : itemsToPreserve) {

			// parse item string
			DbSupport dbSupport;
			int index = itemToPreserve.indexOf('.');
			if (itemToPreserve.indexOf('.') == -1) {
				dbSupport = defaultDbSupport;
			} else {
				String schemaName = itemToPreserve.substring(0, index);
				dbSupport = getDbSupport(schemaName);
				itemToPreserve = itemToPreserve.substring(index + 1);
			}

			// convert to correct case if needed
			String correctCaseItemToPreserve = dbSupport.toCorrectCaseIdentifier(itemToPreserve);

			// store item per schema
			Set<String> schemaItems = result.get(dbSupport.getSchemaName());
			if (schemaItems == null) {
				schemaItems = new HashSet<String>();
				result.put(dbSupport.getSchemaName(), schemaItems);
			}
			schemaItems.add(correctCaseItemToPreserve);
		}
		return result;
	}
}