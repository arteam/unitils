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
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.util.StoredIdentifierCase.MIXED_CASE;
import org.unitils.dbmaintainer.clean.DBCleaner;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_SCHEMAS;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
/**
 * Implementation of {@link DBCleaner}. This implementation will delete all data from a database, except for the tables
 * that are configured as tables to preserve. This includes the tables that are listed in the property
 * {@link #PROPKEY_PRESERVE_TABLES}, {@link #PROPKEY_PRESERVE_DATA_TABLES}. and the table that is configured as
 * version table using the property {@link #PROPKEY_VERSION_TABLE_NAME}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBCleaner extends BaseDatabaseAccessor implements DBCleaner {

    /**
     * Property key for schemas in which none of the tables should be cleaned
     */
    public static final String PROPKEY_PRESERVE_DATA_SCHEMAS = "dbMaintainer.preserveDataOnly.schemas";

    /**
     * Property key for the tables that should not be cleaned
     */
    public static final String PROPKEY_PRESERVE_DATA_TABLES = "dbMaintainer.preserveDataOnly.tables";

    /**
     * Property that specifies which tables should not be dropped (should also not be cleaned)
     */
    public static final String PROPKEY_PRESERVE_TABLES = "dbMaintainer.preserve.tables";

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.executedScriptsTableName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCleaner.class);

    /**
     * Names of schemas that should left untouched.
     */
    protected Set<String> schemasToPreserve;

    /**
     * The tables that should not be cleaned
     */
    protected Set<String> tablesToPreserve;


    /**
     * Configures this object.
     *
     * @param configuration The configuration, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        schemasToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SCHEMAS, false);
        schemasToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_DATA_SCHEMAS, false));
        tablesToPreserve = getItemsToPreserve(PROPKEY_VERSION_TABLE_NAME, true);
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_TABLES, true));
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_DATA_TABLES, true));
    }


    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i> , and the table in which the database version is stored
     */
    public void cleanSchemas() {
        for (DbSupport dbSupport : dbSupports) {
            // check whether schema needs to be preserved
            if (isItemToPreserve(dbSupport.getSchemaName(), schemasToPreserve)) {
                continue;
            }
            logger.info("Cleaning database schema " + dbSupport.getSchemaName());

            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                // check whether table needs to be preserved
                if (isItemToPreserve(tableName, tablesToPreserve) || isItemToPreserve(dbSupport.getSchemaName() + "." + tableName, tablesToPreserve)) {
                    continue;
                }
                cleanTable(tableName, dbSupport);
            }
        }
    }


    /**
     * Deletes the data in the table with the given name.
     * Note: the table name is surrounded with quotes, to make sure that
     * case-sensitive table names are also deleted correctly.
     *
     * @param tableName The name of the table that need to be cleared, not null
     * @param dbSupport The database support, not null
     */
    protected void cleanTable(String tableName, DbSupport dbSupport) {
        logger.debug("Deleting all records from table " + tableName + " in database schema " + dbSupport.getSchemaName());
        sqlHandler.executeUpdate("delete from " + dbSupport.qualified(tableName));
    }


    /**
     * Checks whether the given item is one of the items to preserve.
     * This also handles identifiers that are stored in mixed case.
     *
     * @param item            The item, not null
     * @param itemsToPreserve The items to preserve, not null
     * @return True if item to preserve
     */
    protected boolean isItemToPreserve(String item, Set<String> itemsToPreserve) {
        // ignore case when stored in mixed casing (e.g MS-Sql), otherwise we can't compare the item names
        if (defaultDbSupport.getStoredIdentifierCase() == MIXED_CASE) {
            item = item.toUpperCase();
        }
        return itemsToPreserve.contains(item);
    }


    /**
     * Gets the list of items to preserve. The case is correct if necesary. Quoting an identifier
     * makes it case sensitive. If requested, the identifiers will be quailified with the default schema name if no
     * schema name is used as prefix.
     *
     * @param propertyName        The name of the property that defines the items, not null
     * @param prefixDefaultSchema True to prefix item with default schema when needed
     * @return The set of items, not null
     */
    protected Set<String> getItemsToPreserve(String propertyName, boolean prefixDefaultSchema) {
        Set<String> result = new HashSet<String>();
        List<String> itemsToPreserve = getStringList(propertyName, configuration);
        for (String itemToPreserve : itemsToPreserve) {
            // ignore case when stored in mixed casing (e.g MS-Sql), otherwise we can't compare the item names
            if (defaultDbSupport.getStoredIdentifierCase() == MIXED_CASE) {
                itemToPreserve = itemToPreserve.toUpperCase();
            }

            String correctCaseitemToPreserve = defaultDbSupport.toCorrectCaseIdentifier(itemToPreserve);
            if (prefixDefaultSchema && correctCaseitemToPreserve.indexOf('.') == -1) {
                correctCaseitemToPreserve = defaultDbSupport.getSchemaName() + "." + correctCaseitemToPreserve;
            }
            result.add(correctCaseitemToPreserve);
        }
        return result;
    }
}