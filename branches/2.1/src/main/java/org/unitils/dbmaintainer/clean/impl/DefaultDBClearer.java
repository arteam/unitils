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

import java.util.Collections;
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
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import org.unitils.dbmaintainer.util.DbItemIdentifier;
import org.unitils.util.PropertyUtils;

/**
 * Implementation of {@link DBClearer}. This implementation individually drops every table, view, constraint, trigger
 * and sequence in the database. A list of tables, views, ... that should be preserved can be specified using the
 * property {@link #PROPKEY_PRESERVE_TABLES}. <p/> NOTE: FK constraints give problems in MySQL and Derby The cascade in
 * drop table A cascade; does not work in MySQL-5.0 The DBMaintainer will first remove all constraints before calling
 * the db clearer
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBClearer extends BaseDatabaseAccessor implements DBClearer {

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
     * The key of the property that specifies which materialized views should not be dropped
     */
    public static final String PROPKEY_PRESERVE_MATERIALIZED_VIEWS = "dbMaintainer.preserve.materializedViews";

    /**
     * The key of the property that specifies which synonyms should not be dropped
     */
    public static final String PROPKEY_PRESERVE_SYNONYMS = "dbMaintainer.preserve.synonyms";

    /**
     * The key of the property that specifies which sequences should not be dropped
     */
    public static final String PROPKEY_PRESERVE_SEQUENCES = "dbMaintainer.preserve.sequences";

    /**
     * The key of the property that specifies which triggers should not be dropped
     */
    public static final String PROPKEY_PRESERVE_TRIGGERS = "dbMaintainer.preserve.triggers";

    /**
     * The key of the property that specifies which types should not be dropped
     */
    public static final String PROPKEY_PRESERVE_TYPES = "dbMaintainer.preserve.types";

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted
     */
    public static final String PROPKEY_EXECUTED_SCRIPTS_TABLE_NAME = "dbMaintainer.executedScriptsTableName";


    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearer.class);

    /**
     * Names of schemas that should left untouched.
     */
    protected Set<DbItemIdentifier> schemasToPreserve;

    /**
     * Names of tables that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> tablesToPreserve;

    /**
     * Names of views that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> viewsToPreserve;

    /**
     * Names of materialized views that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> materializedViewsToPreserve;

    /**
     * Names of synonyms that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> synonymsToPreserve;

    /**
     * Names of sequences that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> sequencesToPreserve;

    /**
     * Names of triggers that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> triggersToPreserve;

    /**
     * Names of types that should not be dropped per schema.
     */
    protected Set<DbItemIdentifier> typesToPreserve;


    /**
     * Initializes the the DBClearer. The list of database items that should be preserved is retrieved from the given
     * <code>Configuration</code> object.
     *
     * @param configuration the config, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        schemasToPreserve = getSchemasToPreserve();
        tablesToPreserve = getTablesToPreserve(); // also adds db version table
        viewsToPreserve = getViewsToPreserve();
        materializedViewsToPreserve = getMaterializedViewsToPreserve();
        sequencesToPreserve = getSequencesToPreserve();
        synonymsToPreserve = getSynonymsToPreserve();
        triggersToPreserve = getTriggersToPreserve();
        typesToPreserve = getTypesToPreserve();
    }


    /**
     * Clears the database schemas. This means, all the tables, views, constraints, triggers and sequences are dropped,
     * so that the database schema is empty. The database items that are configured as items to preserve, are left
     * untouched.
     */
    public void clearSchemas() {
        for (DbSupport dbSupport : getDbSupports()) {
        	for (String schemaName : dbSupport.getSchemaNames()) {
        	
	            // check whether schema needs to be preserved
	            if (schemasToPreserve.contains(DbItemIdentifier.getSchemaIdentifier(schemaName, dbSupport))) {
	                continue;
	            }
	            logger.info("Clearing (dropping) database schema " + schemaName);
	            dropSynonyms(dbSupport, schemaName);
	            dropViews(dbSupport, schemaName);
	            dropMaterializedViews(dbSupport, schemaName);
	            dropSequences(dbSupport, schemaName);
	            dropTables(dbSupport, schemaName);
	
	            dropTriggers(dbSupport, schemaName);
	            dropTypes(dbSupport, schemaName);
	            // todo drop functions, stored procedures.
        	}
        }
    }


    /**
     * Drops all tables.
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropTables(DbSupport dbSupport, String schemaName) {
        Set<String> tableNames = dbSupport.getTableNames(schemaName);
        for (String tableName : tableNames) {
            // check whether table needs to be preserved
            if (tablesToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, tableName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping table " + tableName + " in database schema " + schemaName);
            dbSupport.dropTable(schemaName, tableName);
        }
    }


    /**
     * Drops all views.
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropViews(DbSupport dbSupport, String schemaName) {
        Set<String> viewNames = dbSupport.getViewNames(schemaName);
        for (String viewName : viewNames) {
            // check whether view needs to be preserved
            if (viewsToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, viewName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping view " + viewName + " in database schema " + schemaName);
            dbSupport.dropView(schemaName, viewName);
        }
    }


    /**
     * Drops all materialized views.
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropMaterializedViews(DbSupport dbSupport, String schemaName) {
        if (!dbSupport.supportsMaterializedViews()) {
            return;
        }
        Set<String> materializedViewNames = dbSupport.getMaterializedViewNames(schemaName);
        for (String materializedViewName : materializedViewNames) {
            // check whether view needs to be preserved
        	if (materializedViewsToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, materializedViewName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping materialized view " + materializedViewName + " in database schema " + schemaName);
            dbSupport.dropMaterializedView(schemaName, materializedViewName);
        }
    }


    /**
     * Drops all synonyms
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropSynonyms(DbSupport dbSupport, String schemaName) {
        if (!dbSupport.supportsSynonyms()) {
            return;
        }
        Set<String> synonymNames = dbSupport.getSynonymNames(schemaName);
        for (String synonymName : synonymNames) {
            // check whether table needs to be preserved
            if (synonymsToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, synonymName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping synonym " + synonymName + " in database schema " + schemaName);
            dbSupport.dropSynonym(schemaName, synonymName);
        }
    }


    /**
     * Drops all sequences
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropSequences(DbSupport dbSupport, String schemaName) {
        if (!dbSupport.supportsSequences()) {
            return;
        }
        Set<String> sequenceNames = dbSupport.getSequenceNames(schemaName);
        for (String sequenceName : sequenceNames) {
            // check whether sequence needs to be preserved
            if (sequencesToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, sequenceName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping sequence " + sequenceName + " in database schema " + schemaName);
            dbSupport.dropSequence(schemaName, sequenceName);
        }
    }


    /**
     * Drops all triggers
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropTriggers(DbSupport dbSupport, String schemaName) {
        if (!dbSupport.supportsTriggers()) {
            return;
        }
        Set<String> triggerNames = dbSupport.getTriggerNames(schemaName);
        for (String triggerName : triggerNames) {
            // check whether trigger needs to be preserved
            if (triggersToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, triggerName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping trigger " + triggerName + " in database schema " + schemaName);
            dbSupport.dropTrigger(schemaName, triggerName);
        }
    }


    /**
     * Drops all types.
     *
     * @param dbSupport The database support, not null
     * @param schemaName 
     */
    protected void dropTypes(DbSupport dbSupport, String schemaName) {
        if (!dbSupport.supportsTypes()) {
            return;
        }
        Set<String> typeNames = dbSupport.getTypeNames(schemaName);
        for (String typeName : typeNames) {
            // check whether type needs to be preserved
            if (typesToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, typeName, dbSupport))) {
                continue;
            }
            logger.debug("Dropping type " + typeName + " in database schema " + schemaName);
            dbSupport.dropType(schemaName, typeName);
        }
    }


    /**
     * Gets the list of all schemas to preserve. The case is corrected if necesary. Quoting a schema name makes it case
     * sensitive.
     * <p/>
     * If a schema name is not defined in the unitils configuration, a UnitilsException is thrown.
     *
     * @param configuration The unitils configuration, not null
     * @return The schemas to preserve, not null
     */
    protected Set<DbItemIdentifier> getSchemasToPreserve() {
        Set<DbItemIdentifier> schemasToPreserve = getSchemasToPreserve(PROPKEY_PRESERVE_SCHEMAS); 

        for (DbItemIdentifier schemaToPreserve : schemasToPreserve) {
        	// Verify if the schema exists.
        	DbSupport dbSupport = dbNameDbSupportMap.get(schemaToPreserve.getDatabaseName());
			if (!dbSupport.getSchemaNames().contains(schemaToPreserve.getSchemaName())) {
        		throw new UnitilsException("Schema to preserve does not exist: " + schemaToPreserve.getSchemaName() + 
        				".\nUnitils cannot determine which schemas need to be preserved. To assure nothing is dropped by mistake, no schemas will be dropped.\nPlease fix the configuration of the " + 
        				PROPKEY_PRESERVE_SCHEMAS + " property.");
        	}
        }
        return schemasToPreserve;
    }


    /**
     * Gets the list of all tables to preserve per schema. Quoting a table name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a table to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info table is also added as a table to preserve, but will not be checked on existence.
     *
     * @return The tables to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getTablesToPreserve() {
        Set<DbItemIdentifier> tablesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TABLES);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaTableNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier tableToPreserve : tablesToPreserve) {
        	Set<DbItemIdentifier> tableNames = schemaTableNames.get(tableToPreserve.getSchema());
        	if (tableNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(tableToPreserve.getDatabaseName());
        		tableNames = toDbItemIdentifiers(dbSupport, tableToPreserve.getSchemaName(), dbSupport.getTableNames(tableToPreserve.getSchemaName()));
        		schemaTableNames.put(tableToPreserve.getSchema(), tableNames);
        	}
        	
            if (!tableNames.contains(tableToPreserve)) {
                throw new UnitilsException("Table to preserve does not exist: " + tableToPreserve.getItemName() + " in schema: " + tableToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which tables need to be preserved. To assure nothing is dropped by mistake, no tables will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_TABLES + " property.");
            }
        }
        
        // add executed scripts info table as item to preserve
        tablesToPreserve.add(DbItemIdentifier.getItemIdentifier(defaultDbSupport.getDefaultSchemaName(), 
        		PropertyUtils.getString(PROPKEY_EXECUTED_SCRIPTS_TABLE_NAME, configuration), defaultDbSupport));

        return tablesToPreserve;
    }


    /**
     * Gets the list of all views to preserve per schema. Quoting a view name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a view to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info view is also added as a view to preserve, but will not be checked on existence.
     *
     * @return The views to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getViewsToPreserve() {
        Set<DbItemIdentifier> viewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_VIEWS);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaViewNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier viewToPreserve : viewsToPreserve) {
        	Set<DbItemIdentifier> viewNames = schemaViewNames.get(viewToPreserve.getSchema());
        	if (viewNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(viewToPreserve.getDatabaseName());
        		viewNames = toDbItemIdentifiers(dbSupport, viewToPreserve.getSchemaName(), dbSupport.getViewNames(viewToPreserve.getSchemaName()));
        	}
        	
            if (!viewNames.contains(viewToPreserve)) {
                throw new UnitilsException("View to preserve does not exist: " + viewToPreserve.getItemName() + " in schema: " + viewToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which views need to be preserved. To assure nothing is dropped by mistake, no views will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_VIEWS + " property.");
            }
        }
        
        return viewsToPreserve;
    }


    /**
     * Gets the list of all materialized views to preserve per schema. Quoting a materialized view name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a materialized view to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info materialized view is also added as a materialized view to preserve, but will not be checked on existence.
     *
     * @return The materialized views to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getMaterializedViewsToPreserve() {
        Set<DbItemIdentifier> materializedViewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_MATERIALIZED_VIEWS);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaMaterializedViewNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier materializedViewToPreserve : materializedViewsToPreserve) {
        	Set<DbItemIdentifier> materializedViewNames = schemaMaterializedViewNames.get(materializedViewToPreserve.getSchema());
        	if (materializedViewNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(materializedViewToPreserve.getDatabaseName());
        		if (dbSupport.supportsMaterializedViews()) {
        			materializedViewNames = toDbItemIdentifiers(dbSupport, materializedViewToPreserve.getSchemaName(), dbSupport.getMaterializedViewNames(materializedViewToPreserve.getSchemaName()));
        		} else {
        			materializedViewNames = Collections.emptySet();
        		}
        	}
        	
            if (!materializedViewNames.contains(materializedViewToPreserve)) {
                throw new UnitilsException("Materialized view to preserve does not exist: " + materializedViewToPreserve.getItemName() + " in schema: " + materializedViewToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which materialized views need to be preserved. To assure nothing is dropped by mistake, no materialized views will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_MATERIALIZED_VIEWS + " property.");
            }
        }
        
        return materializedViewsToPreserve;
    }


    /**
     * Gets the list of all sequences to preserve per schema. Quoting a sequence name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a sequence to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info sequence is also added as a sequence to preserve, but will not be checked on existence.
     *
     * @return The sequences to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getSequencesToPreserve() {
        Set<DbItemIdentifier> sequencesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SEQUENCES);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaSequenceNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier sequenceToPreserve : sequencesToPreserve) {
        	Set<DbItemIdentifier> sequenceNames = schemaSequenceNames.get(sequenceToPreserve.getSchema());
        	if (sequenceNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(sequenceToPreserve.getDatabaseName());
        		if (dbSupport.supportsSequences()) {
        			sequenceNames = toDbItemIdentifiers(dbSupport, sequenceToPreserve.getSchemaName(), dbSupport.getSequenceNames(sequenceToPreserve.getSchemaName()));
        		} else {
        			sequenceNames = Collections.emptySet();
        		}
        	}
        	
            if (!sequenceNames.contains(sequenceToPreserve)) {
                throw new UnitilsException("Sequence to preserve does not exist: " + sequenceToPreserve.getItemName() + " in schema: " + sequenceToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which sequences need to be preserved. To assure nothing is dropped by mistake, no sequences will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_SEQUENCES + " property.");
            }
        }
        
        return sequencesToPreserve;
    }


    /**
     * Gets the list of all synonyms to preserve per schema. Quoting a synonym name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a synonym to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info synonym is also added as a synonym to preserve, but will not be checked on existence.
     *
     * @return The synonyms to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getSynonymsToPreserve() {
        Set<DbItemIdentifier> synonymsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SYNONYMS);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaSynonymNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier synonymToPreserve : synonymsToPreserve) {
        	Set<DbItemIdentifier> synonymNames = schemaSynonymNames.get(synonymToPreserve.getSchema());
        	if (synonymNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(synonymToPreserve.getDatabaseName());
        		if (dbSupport.supportsSynonyms()) {
        			synonymNames = toDbItemIdentifiers(dbSupport, synonymToPreserve.getSchemaName(), dbSupport.getSynonymNames(synonymToPreserve.getSchemaName()));
        		} else {
        			synonymNames = Collections.emptySet();
        		}
        	}
        	
            if (!synonymNames.contains(synonymToPreserve)) {
                throw new UnitilsException("Synonym to preserve does not exist: " + synonymToPreserve.getItemName() + " in schema: " + synonymToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which synonyms need to be preserved. To assure nothing is dropped by mistake, no synonyms will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_SYNONYMS + " property.");
            }
        }
        
        return synonymsToPreserve;
    }


    /**
     * Gets the list of all triggers to preserve per schema. Quoting a trigger name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a trigger to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info trigger is also added as a trigger to preserve, but will not be checked on existence.
     *
     * @return The triggers to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getTriggersToPreserve() {
        Set<DbItemIdentifier> triggersToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TRIGGERS);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaTriggerNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier triggerToPreserve : triggersToPreserve) {
        	Set<DbItemIdentifier> triggerNames = schemaTriggerNames.get(triggerToPreserve.getSchema());
        	if (triggerNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(triggerToPreserve.getDatabaseName());
        		if (dbSupport.supportsTriggers()) {
        			triggerNames = toDbItemIdentifiers(dbSupport, triggerToPreserve.getSchemaName(), dbSupport.getTriggerNames(triggerToPreserve.getSchemaName()));
        		} else {
        			triggerNames = Collections.emptySet();
        		}
        	}
        	
            if (!triggerNames.contains(triggerToPreserve)) {
                throw new UnitilsException("Trigger to preserve does not exist: " + triggerToPreserve.getItemName() + " in schema: " + triggerToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which triggers need to be preserved. To assure nothing is dropped by mistake, no triggers will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_TRIGGERS + " property.");
            }
        }
        
        return triggersToPreserve;
    }


    /**
     * Gets the list of all types to preserve per schema. Quoting a type name makes it case sensitive. 
     * If no database or schema is specified, the default one is taken.
     * <p/>
     * If a type to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The executed scripts info type is also added as a type to preserve, but will not be checked on existence.
     *
     * @return The types to preserve per schema, not null
     */
    protected Set<DbItemIdentifier> getTypesToPreserve() {
        Set<DbItemIdentifier> typesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TYPES);
        
        Map<DbItemIdentifier, Set<DbItemIdentifier>> schemaTypeNames = new HashMap<DbItemIdentifier, Set<DbItemIdentifier>>();
        for (DbItemIdentifier typeToPreserve : typesToPreserve) {
        	Set<DbItemIdentifier> typeNames = schemaTypeNames.get(typeToPreserve.getSchema());
        	if (typeNames == null) {
        		DbSupport dbSupport = dbNameDbSupportMap.get(typeToPreserve.getDatabaseName());
        		if (dbSupport.supportsTypes()) {
        			typeNames = toDbItemIdentifiers(dbSupport, typeToPreserve.getSchemaName(), dbSupport.getTypeNames(typeToPreserve.getSchemaName()));
        		} else {
        			typeNames = Collections.emptySet();
        		}
        	}
        	
            if (!typeNames.contains(typeToPreserve)) {
                throw new UnitilsException("Type to preserve does not exist: " + typeToPreserve.getItemName() + " in schema: " + typeToPreserve.getSchemaName() + 
                		".\nUnitils cannot determine which types need to be preserved. To assure nothing is dropped by mistake, no types will be dropped.\nPlease fix the configuration of the " + 
                		PROPKEY_PRESERVE_TYPES + " property.");
            }
        }
        
        return typesToPreserve;
    }


    /**
     * Gets the list of items to preserve. The case is correct if necesSary. Quoting an identifier
     * makes it case sensitive. If requested, the identifiers will be qualified with the default schema name if no
     * schema name is used as prefix.
     *
     * @param propertyName        The name of the property that defines the items, not null
     * @return The set of items, not null
     */
    protected Set<DbItemIdentifier> getSchemasToPreserve(String propertyName) {
        Set<DbItemIdentifier> result = new HashSet<DbItemIdentifier>();
        List<String> schemasToPreserve = getStringList(propertyName, configuration);
        for (String schemaToPreserve : schemasToPreserve) {
        	DbItemIdentifier itemIdentifier = DbItemIdentifier.parseSchemaIdentifier(schemaToPreserve, defaultDbSupport, dbNameDbSupportMap);
        	result.add(itemIdentifier);
        }
        return result;
    }


    /**
     * Gets the list of items to preserve. The case is correct if necesSary. Quoting an identifier
     * makes it case sensitive. If requested, the identifiers will be qualified with the default schema name if no
     * schema name is used as prefix.
     *
     * @param propertyName        The name of the property that defines the items, not null
     * @return The set of items, not null
     */
    protected Set<DbItemIdentifier> getItemsToPreserve(String propertyName) {
        Set<DbItemIdentifier> result = new HashSet<DbItemIdentifier>();
        List<String> itemsToPreserve = getStringList(propertyName, configuration);
        for (String itemToPreserve : itemsToPreserve) {
        	DbItemIdentifier itemIdentifier = DbItemIdentifier.parseItemIdentifier(itemToPreserve, defaultDbSupport, dbNameDbSupportMap);
        	result.add(itemIdentifier);
        }
        return result;
    }
    
    
    protected Set<DbItemIdentifier> toDbItemIdentifiers(DbSupport dbSupport, String schemaName, Set<String> itemNames) {
		Set<DbItemIdentifier> result = new HashSet<DbItemIdentifier>();
		for (String itemName : itemNames) {
			result.add(DbItemIdentifier.getItemIdentifier(schemaName, itemName, dbSupport));
		}
		return result;
	}
}