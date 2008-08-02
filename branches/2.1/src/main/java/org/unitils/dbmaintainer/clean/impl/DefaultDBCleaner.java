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

import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.PROPKEY_PRESERVE_SCHEMAS;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.util.BaseDatabaseAccessor;
import org.unitils.dbmaintainer.util.DbItemIdentifier;
/**
 * Implementation of {@link DBCleaner}. This implementation will delete all data from a database, except for the tables
 * that are configured as tables to preserve. This includes the tables that are listed in the property
 * {@link #PROPKEY_PRESERVE_TABLES}, {@link #PROPKEY_PRESERVE_DATA_TABLES}. and the table that is configured as
 * version table using the property {@link #PROPKEY_EXECUTED_SCRIPTS_TABLE_NAME}.
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
     * The key of the property that specifies the name of the database table in which the
     * DB version is stored. This table should not be deleted
     */
    public static final String PROPKEY_EXECUTED_SCRIPTS_TABLE_NAME = "dbMaintainer.executedScriptsTableName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBCleaner.class);

    /**
     * Names of schemas that should left untouched.
     */
    protected Set<DbItemIdentifier> schemasToPreserve;

    /**
     * The tables that should not be cleaned
     */
    protected Set<DbItemIdentifier> tablesToPreserve;


    /**
     * Configures this object.
     *
     * @param configuration The configuration, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        schemasToPreserve = getSchemasToPreserve(PROPKEY_PRESERVE_SCHEMAS);
        schemasToPreserve.addAll(getSchemasToPreserve(PROPKEY_PRESERVE_DATA_SCHEMAS));
        tablesToPreserve = getItemsToPreserve(PROPKEY_EXECUTED_SCRIPTS_TABLE_NAME);
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_TABLES));
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_DATA_TABLES));
    }


    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i> , and the table in which the database version is stored
     */
    public void cleanSchemas() {
        for (DbSupport dbSupport : getDbSupports()) {
			for (String schemaName : dbSupport.getSchemaNames()) {
	            // check whether schema needs to be preserved
	            if (schemasToPreserve.contains(DbItemIdentifier.getSchemaIdentifier(schemaName, dbSupport))) {
	                continue;
	            }
	            logger.info("Cleaning database schema " + schemaName);
	
	            Set<String> tableNames = dbSupport.getTableNames(schemaName);
	            for (String tableName : tableNames) {
	                // check whether table needs to be preserved
	                if (tablesToPreserve.contains(DbItemIdentifier.getItemIdentifier(schemaName, tableName, dbSupport))) {
	                    continue;
	                }
	                cleanTable(dbSupport, schemaName, tableName);
	            }
        	}
        }
    }


    /**
     * Deletes the data in the table with the given name.
     * Note: the table name is surrounded with quotes, to make sure that
     * case-sensitive table names are also deleted correctly.
     * @param dbSupport The database support, not null
     * @param schemaName 
     * @param tableName The name of the table that need to be cleared, not null
     */
    protected void cleanTable(DbSupport dbSupport, String schemaName, String tableName) {
        logger.debug("Deleting all records from table " + tableName + " in database schema " + schemaName);
        sqlHandler.executeUpdate("delete from " + dbSupport.qualified(schemaName, tableName), dbSupport.getDataSource());
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
}