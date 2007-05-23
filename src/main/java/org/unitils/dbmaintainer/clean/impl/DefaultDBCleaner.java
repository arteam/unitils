/*
 * Copyright 2006 the original author or authors.
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
import org.unitils.dbmaintainer.util.BaseDatabaseTask;

/**
 * Implementation of {@link DBCleaner}. This implementation will delete all data from a database, except for the tables
 * that are configured as tables to preserve. This includes the tables that are listed in the property
 * {@link #PROPKEY_PRESERVE_TABLES}, {@link #PROPKEY_PRESERVE_ONLY_DATA_TABLES}. and the table that is configured as
 * version table using the property {@link #PROPKEY_VERSION_TABLE_NAME}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBCleaner extends BaseDatabaseTask implements DBCleaner {

    /**
     * Property key for schemas that should not be cleaned at all
     */
    public static final String PROPKEY_PRESERVE_ONLY_DATA_SCHEMAS = "dbMaintainer.preserveOnlyData.schemas";

    /**
     * Property key for the tables that should not be cleaned
     */
    public static final String PROPKEY_PRESERVE_ONLY_DATA_TABLES = "dbMaintainer.preserveOnlyData.tables";

    /**
     * The key of the property that specifies which tables should not be dropped (shouls also not be cleaned)
     */
    public static final String PROPKEY_PRESERVE_TABLES = "dbMaintainer.preserve.tables";

    /**
     * The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

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
        schemasToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SCHEMAS, configuration, false);
        schemasToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_ONLY_DATA_SCHEMAS, configuration, false));
        tablesToPreserve = getItemsToPreserve(PROPKEY_VERSION_TABLE_NAME, configuration, true);
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_TABLES, configuration, true));
        tablesToPreserve.addAll(getItemsToPreserve(PROPKEY_PRESERVE_ONLY_DATA_TABLES, configuration, true));
    }


    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i> , and the table in which the database version is stored
     */
    public void cleanSchemas() {
        for (DbSupport dbSupport : dbSupports) {
            // check whether schema needs to be preserved
            if (schemasToPreserve.contains(dbSupport.getSchemaName())) {
                continue;
            }
            logger.info("Cleaning database schema " + dbSupport.getSchemaName());

            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                // check whether table needs to be preserved
                if (tablesToPreserve.contains(tableName) || tablesToPreserve.contains(dbSupport.getSchemaName() + "." + tableName)) {
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
     * Gets the list of items to preserve. The case is correct if necesary. Quoting an identifier
     * makes it case sensitive. If requested, the identifiers will be quailified with the default schema name if no
     * schema name is used as prefix.
     *
     * @param propertyName        The name of the property that defines the items, not null
     * @param configuration       The config, not null
     * @param prefixDefaultSchema True to prefix item with default schema when needed
     * @return The set of items, not null
     */
    protected Set<String> getItemsToPreserve(String propertyName, Properties configuration, boolean prefixDefaultSchema) {
        Set<String> result = new HashSet<String>();
        List<String> itemsToPreserve = getStringList(propertyName, configuration);
        for (String itemToPreserve : itemsToPreserve) {
            String correctCaseitemToPreserve = defaultDbSupport.toCorrectCaseIdentifier(itemToPreserve);
            if (prefixDefaultSchema && correctCaseitemToPreserve.indexOf('.') == -1) {
                correctCaseitemToPreserve = defaultDbSupport.getSchemaName() + "." + correctCaseitemToPreserve;
            }
            result.add(correctCaseitemToPreserve);
        }
        return result;
    }
}