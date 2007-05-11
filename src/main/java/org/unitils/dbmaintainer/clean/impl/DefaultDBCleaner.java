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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.PropertyUtils.getStringList;

import java.util.*;

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
     * The tables that should not be cleaned
     */
    protected Set<String> tablesToPreserve;


    /**
     * Configures this object.
     *
     * @param configuration The configuration, not null
     */
    protected void doInit(Properties configuration) {
        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(defaultDbSupport.toCorrectCaseIdentifier(PropertyUtils.getString(PROPKEY_VERSION_TABLE_NAME, configuration)));
        tablesToPreserve.addAll(toCorrectCaseIdentifiers(getStringList(PROPKEY_PRESERVE_TABLES, configuration), defaultDbSupport));
        tablesToPreserve.addAll(toCorrectCaseIdentifiers(getStringList(PROPKEY_PRESERVE_ONLY_DATA_TABLES, configuration), defaultDbSupport));
    }


    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i> , and the table in which the database version is stored
     */
    public void cleanSchemas() {
        for (DbSupport dbSupport : dbSupports) {
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
        executeUpdate("delete from " + dbSupport.qualified(tableName), dataSource);
    }


    /**
     * Converts the given list of identifiers to uppercase/lowercase depending on what the default
     * for the database is. If a value is surrounded with double quotes (") it will not be converted and
     * the double quotes will be stripped. These values are treated as case sensitive names.
     *
     * @param identifiers The identifiers, not null
     * @param dbSupport   The database support, not null
     * @return The names converted to correct case if needed, not null
     */
    protected List<String> toCorrectCaseIdentifiers(List<String> identifiers, DbSupport dbSupport) {
        List<String> result = new ArrayList<String>();
        for (String identifier : identifiers) {
            result.add(dbSupport.toCorrectCaseIdentifier(identifier));
        }
        return result;
    }
}