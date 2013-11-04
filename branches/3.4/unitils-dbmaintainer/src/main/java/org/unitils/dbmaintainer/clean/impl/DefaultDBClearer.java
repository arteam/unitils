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
package org.unitils.dbmaintainer.clean.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.util.StoredIdentifierCase.MIXED_CASE;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.*;

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
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.executedScriptsTableName";


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
     * Names of materialized views that should not be dropped per schema.
     */
    protected Map<String, Set<String>> materializedViewsToPreserve;

    /**
     * Names of synonyms that should not be dropped per schema.
     */
    protected Map<String, Set<String>> synonymsToPreserve;

    /**
     * Names of sequences that should not be dropped per schema.
     */
    protected Map<String, Set<String>> sequencesToPreserve;

    /**
     * Names of triggers that should not be dropped per schema.
     */
    protected Map<String, Set<String>> triggersToPreserve;

    /**
     * Names of types that should not be dropped per schema.
     */
    protected Map<String, Set<String>> typesToPreserve;


    /**
     * Initializes the the DBClearer. The list of database items that should be preserved is retrieved from the given
     * <code>Configuration</code> object.
     *
     * @param configuration the config, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        schemasToPreserve = getSchemasToPreserve(configuration);
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
        for (DbSupport dbSupport : dbSupports) {
            // check whether schema needs to be preserved
            if (schemasToPreserve.contains(dbSupport.getSchemaName())) {
                continue;
            }
            logger.info("Clearing (dropping) database schema " + dbSupport.getSchemaName());
            dropSynonyms(dbSupport);
            dropViews(dbSupport);
            dropMaterializedViews(dbSupport);
            dropSequences(dbSupport);
            dropTables(dbSupport);

            dropTriggers(dbSupport);
            dropTypes(dbSupport);
            // todo drop functions, stored procedures.
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
            if (isItemToPreserve(tableName, schemaTablesToPreserve)) {
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
            if (isItemToPreserve(viewName, schemaViewsToPreserve)) {
                continue;
            }
            logger.debug("Dropping view " + viewName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropView(viewName);
        }
    }


    /**
     * Drops all materialized views.
     *
     * @param dbSupport The database support, not null
     */
    protected void dropMaterializedViews(DbSupport dbSupport) {
        if (!dbSupport.supportsMaterializedViews()) {
            return;
        }
        Set<String> materializedViewNames = dbSupport.getMaterializedViewNames();
        Set<String> schemaMaterializedViewsToPreserve = materializedViewsToPreserve.get(dbSupport.getSchemaName());
        for (String materializedViewName : materializedViewNames) {
            // check whether view needs to be preserved
            if (isItemToPreserve(materializedViewName, schemaMaterializedViewsToPreserve)) {
                continue;
            }
            logger.debug("Dropping materialized view " + materializedViewName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropMaterializedView(materializedViewName);
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
            if (isItemToPreserve(synonymName, schemaSynonymsToPreserve)) {
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
            if (isItemToPreserve(sequenceName, schemaSequencesToPreserve)) {
                continue;
            }
            logger.debug("Dropping sequence " + sequenceName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropSequence(sequenceName);
        }
    }


    /**
     * Drops all triggers
     *
     * @param dbSupport The database support, not null
     */
    protected void dropTriggers(DbSupport dbSupport) {
        if (!dbSupport.supportsTriggers()) {
            return;
        }
        Set<String> triggerNames = dbSupport.getTriggerNames();
        Set<String> schemaTriggersToPreserve = triggersToPreserve.get(dbSupport.getSchemaName());
        for (String triggerName : triggerNames) {
            // check whether trigger needs to be preserved
            if (isItemToPreserve(triggerName, schemaTriggersToPreserve)) {
                continue;
            }
            logger.debug("Dropping trigger " + triggerName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropTrigger(triggerName);
        }
    }


    /**
     * Drops all types.
     *
     * @param dbSupport The database support, not null
     */
    protected void dropTypes(DbSupport dbSupport) {
        if (!dbSupport.supportsTypes()) {
            return;
        }
        Set<String> typeNames = dbSupport.getTypeNames();
        Set<String> schemaTypesToPreserve = typesToPreserve.get(dbSupport.getSchemaName());
        for (String typeName : typeNames) {
            // check whether type needs to be preserved
            if (isItemToPreserve(typeName, schemaTypesToPreserve)) {
                continue;
            }
            logger.debug("Dropping type " + typeName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropType(typeName);
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
    protected Set<String> getSchemasToPreserve(Properties configuration) {
        Set<String> result = new HashSet<String>();

        List<String> schemasToPreserve = getStringList(PROPKEY_PRESERVE_SCHEMAS, configuration);
        for (String schemaToPreserve : schemasToPreserve) {

            boolean found = false;
            for (DbSupport dbSupport : dbSupports) {
                // ignore case when stored in mixed casing (e.g MS-Sql), otherwise we can't compare the item names
                if (defaultDbSupport.getStoredIdentifierCase() == MIXED_CASE) {
                    schemaToPreserve = schemaToPreserve.toUpperCase();
                }
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
     * <p/>
     * If a table to preserve does not exist, a UnitilsException is thrown.
     * <p/>
     * The db version table is also added as a table to preserve, but will not be checked on existence.
     *
     * @return The tables to preserve per schema, not null
     */
    protected Map<String, Set<String>> getTablesToPreserve() {
        Map<String, Set<String>> tablesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TABLES);
        for (Map.Entry<String, Set<String>> entry : tablesToPreserve.entrySet()) {
            String schemaName = entry.getKey();
            Set<String> tableNames = getDbSupport(schemaName, dialect).getTableNames();

            for (String tableToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(tableToPreserve, tableNames)) {
                    throw new UnitilsException("Table to preserve does not exist: " + tableToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which tables need to be preserved. To assure nothing is dropped by mistake, no tables will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_TABLES + " property.");
                }
            }
        }

        // add version table as item to preserve
        Map<String, Set<String>> dbVersionTablesToPreserve = getItemsToPreserve(PROPKEY_VERSION_TABLE_NAME);
        for (Map.Entry<String, Set<String>> entry : dbVersionTablesToPreserve.entrySet()) {
            String schemaName = entry.getKey();
            Set<String> dbVersionTableNames = entry.getValue();

            Set<String> tableNames = tablesToPreserve.get(schemaName);
            if (tableNames == null) {
                tablesToPreserve.put(schemaName, dbVersionTableNames);
            } else {
                tableNames.addAll(dbVersionTableNames);
            }
        }
        return tablesToPreserve;
    }


    /**
     * Gets the list of all views to preserve per schema. The case is corrected if necesary. Quoting a view name makes
     * it case sensitive. If no schema is specified, the view will be added to the default schema name set.
     * <p/>
     * If a view to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The views to preserve per schema, not null
     */
    protected Map<String, Set<String>> getViewsToPreserve() {
        Map<String, Set<String>> viewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_VIEWS);
        for (Map.Entry<String, Set<String>> entry : viewsToPreserve.entrySet()) {
            String schemaName = entry.getKey();
            Set<String> viewNames = getDbSupport(schemaName, dialect).getViewNames();

            for (String viewToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(viewToPreserve, viewNames)) {
                    throw new UnitilsException("View to preserve does not exist: " + viewToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which views need to be preserved. To assure nothing is dropped by mistake, no views will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_VIEWS + " property.");
                }
            }
        }
        return viewsToPreserve;
    }


    /**
     * Gets the list of all materialized views to preserve per schema. The case is corrected if necesary. Quoting a view name makes
     * it case sensitive. If no schema is specified, the view will be added to the default schema name set.
     * <p/>
     * If a view to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The materialized views to preserve per schema, not null
     */
    protected Map<String, Set<String>> getMaterializedViewsToPreserve() {
        Map<String, Set<String>> materializedViewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_MATERIALIZED_VIEWS);
        for (Map.Entry<String, Set<String>> entry : materializedViewsToPreserve.entrySet()) {
            String schemaName = entry.getKey();

            DbSupport dbSupport = getDbSupport(schemaName, dialect);
            Set<String> materializedViewNames;
            if (!dbSupport.supportsMaterializedViews()) {
                materializedViewNames = new HashSet<String>();
            } else {
                materializedViewNames = dbSupport.getMaterializedViewNames();
            }
            for (String materializedViewToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(materializedViewToPreserve, materializedViewNames)) {
                    throw new UnitilsException("Materialized view to preserve does not exist: " + materializedViewToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which materialized views need to be preserved. To assure nothing is dropped by mistake, no views will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_MATERIALIZED_VIEWS + " property.");
                }
            }
        }
        return materializedViewsToPreserve;
    }


    /**
     * Gets the list of all sequences to preserve per schema. The case is corrected if necesary. Quoting a sequence name
     * makes it case sensitive. If no schema is specified, the sequence will be added to the default schema name set.
     * <p/>
     * If a sequence to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The sequences to preserve per schema, not null
     */
    protected Map<String, Set<String>> getSequencesToPreserve() {
        Map<String, Set<String>> sequencesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SEQUENCES);
        for (Map.Entry<String, Set<String>> entry : sequencesToPreserve.entrySet()) {
            String schemaName = entry.getKey();

            DbSupport dbSupport = getDbSupport(schemaName, dialect);
            Set<String> sequenceNames;
            if (!dbSupport.supportsSequences()) {
                sequenceNames = new HashSet<String>();
            } else {
                sequenceNames = dbSupport.getSequenceNames();
            }
            for (String sequenceToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(sequenceToPreserve, sequenceNames)) {
                    throw new UnitilsException("Sequence to preserve does not exist: " + sequenceToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which sequences need to be preserved. To assure nothing is dropped by mistake, no sequences will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_SEQUENCES + " property.");
                }
            }
        }
        return sequencesToPreserve;
    }


    /**
     * Gets the list of all synonym to preserve per schema. The case is corrected if necesary. Quoting a synonym name
     * makes it case sensitive. If no schema is specified, the synonym will be added to the default schema name set.
     * <p/>
     * If a synonym to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The synonym to preserve per schema, not null
     */
    protected Map<String, Set<String>> getSynonymsToPreserve() {
        Map<String, Set<String>> synonymsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SYNONYMS);
        for (Map.Entry<String, Set<String>> entry : synonymsToPreserve.entrySet()) {
            String schemaName = entry.getKey();

            DbSupport dbSupport = getDbSupport(schemaName, dialect);
            Set<String> synonymNames;
            if (!dbSupport.supportsSynonyms()) {
                synonymNames = new HashSet<String>();
            } else {
                synonymNames = dbSupport.getSynonymNames();
            }
            for (String synonymToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(synonymToPreserve, synonymNames)) {
                    throw new UnitilsException("Synonym to preserve does not exist: " + synonymToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which synonyms need to be preserved. To assure nothing is dropped by mistake, no synonyms will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_SYNONYMS + " property.");
                }
            }
        }
        return synonymsToPreserve;
    }


    /**
     * Gets the list of all triggers to preserve per schema. The case is corrected if necesary. Quoting a trigger name
     * makes it case sensitive. If no schema is specified, the trigger will be added to the default schema name set.
     * <p/>
     * If a trigger to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The trigger to preserve per schema, not null
     */
    protected Map<String, Set<String>> getTriggersToPreserve() {
        Map<String, Set<String>> triggersToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TRIGGERS);
        for (Map.Entry<String, Set<String>> entry : triggersToPreserve.entrySet()) {
            String schemaName = entry.getKey();

            DbSupport dbSupport = getDbSupport(schemaName, dialect);
            Set<String> triggerNames;
            if (!dbSupport.supportsTriggers()) {
                triggerNames = new HashSet<String>();
            } else {
                triggerNames = dbSupport.getTriggerNames();
            }
            for (String triggerToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(triggerToPreserve, triggerNames)) {
                    throw new UnitilsException("Trigger to preserve does not exist: " + triggerToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which triggers need to be preserved. To assure nothing is dropped by mistake, no triggers will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_TRIGGERS + " property.");
                }
            }
        }
        return triggersToPreserve;
    }


    /**
     * Gets the list of all types to preserve per schema. The case is corrected if necesary. Quoting a type name
     * makes it case sensitive. If no schema is specified, the type will be added to the default schema name set.
     * <p/>
     * If a type to preserve does not exist, a UnitilsException is thrown.
     *
     * @return The type to preserve per schema, not null
     */
    protected Map<String, Set<String>> getTypesToPreserve() {
        Map<String, Set<String>> typesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TYPES);
        for (Map.Entry<String, Set<String>> entry : typesToPreserve.entrySet()) {
            String schemaName = entry.getKey();

            DbSupport dbSupport = getDbSupport(schemaName, dialect);
            Set<String> typeNames;
            if (!dbSupport.supportsTypes()) {
                typeNames = new HashSet<String>();
            } else {
                typeNames = dbSupport.getTypeNames();
            }
            for (String typeToPreserve : entry.getValue()) {
                if (!itemToPreserveExists(typeToPreserve, typeNames)) {
                    throw new UnitilsException("Type to preserve does not exist: " + typeToPreserve + " in schema: " + schemaName + ".\nUnitils cannot determine which types need to be preserved. To assure nothing is dropped by mistake, no types will be dropped.\nPlease fix the configuration of the " + PROPKEY_PRESERVE_TYPES + " property.");
                }
            }
        }
        return typesToPreserve;
    }


    /**
     * Checks whether the given item is one of the items to preserve.
     * This also handles identifiers that are stored in mixed case.
     *
     * @param item            The item, not null
     * @param itemsToPreserve The items to preserve
     * @return True if item to preserve
     */
    protected boolean isItemToPreserve(String item, Set<String> itemsToPreserve) {
        if (itemsToPreserve == null) {
            return false;
        }
        // ignore case when stored in mixed casing (e.g MS-Sql), otherwise we can't compare the item names
        if (defaultDbSupport.getStoredIdentifierCase() == MIXED_CASE) {
            item = item.toUpperCase();
        }
        return itemsToPreserve.contains(item);
    }


    /**
     * Checks whether the given item to preserve is one of the items.
     * This also handles identifiers that are stored in mixed case.
     *
     * @param itemToPreserve The item to preserve, not null
     * @param items          The items, not null
     * @return True if on of the items
     */
    protected boolean itemToPreserveExists(String itemToPreserve, Set<String> items) {
        // ignore case when stored in mixed casing (e.g MS-Sql), otherwise we can't compare the item names
        if (defaultDbSupport.getStoredIdentifierCase() == MIXED_CASE) {
            for (String item : items) {
                if (itemToPreserve.equalsIgnoreCase(item)) {
                    return true;
                }
            }
            return false;
        }
        return items.contains(itemToPreserve);
    }


    /**
     * Gets the list of items to preserve per schema. The case is corrected if necesary.
     * Quoting an identifier makes it case sensitive. If no schema is specified, the identifiers will be added to the
     * default schema name set.
     *
     * @param propertyName The name of the property that defines the items, not null
     * @return The set of items per schema name, not null
     */
    protected Map<String, Set<String>> getItemsToPreserve(String propertyName) {
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
                dbSupport = getDbSupport(schemaName, dialect);
                itemToPreserve = itemToPreserve.substring(index + 1);
            }

            // ignore case when stored in mixed casing (e.g MS-Sql), otherwise the item names can't be compared
            if (dbSupport.getStoredIdentifierCase() == MIXED_CASE) {
                itemToPreserve = itemToPreserve.toUpperCase();
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