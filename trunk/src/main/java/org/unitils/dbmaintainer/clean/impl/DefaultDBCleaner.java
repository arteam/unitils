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

import org.apache.commons.configuration.Configuration;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBCleaner;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link DBCleaner}. This implementation will delete all data from a database, except for the tables
 * that are configured as tables to preserve. This includes the tables that are listed in the property
 * {@link #PROPKEY_TABLESTOPRESERVE}, {@link #PROPKEY_DBCLEARER_ITEMSTOPRESERVE}. and the table that is configured as
 * version table using the property {@link #PROPKEY_VERSION_TABLE_NAME}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBCleaner extends DatabaseTask implements DBCleaner {

    /**
     * Property key for the tables that should not be cleaned
     */
    public static final String PROPKEY_TABLESTOPRESERVE = "dbMaintainer.cleanDb.tablesToPreserve";

    /**
     * Property key for the tables that should not be cleared (these tables should also not be cleaned
     */
    public static final String PROPKEY_DBCLEARER_ITEMSTOPRESERVE = "dbMaintainer.clearDb.itemsToPreserve";

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
    protected void doInit(Configuration configuration) {
        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(configuration.getString(PROPKEY_VERSION_TABLE_NAME).toUpperCase());
        tablesToPreserve.addAll(toCorrectCaseIdentifiers(asList(configuration.getStringArray(PROPKEY_TABLESTOPRESERVE))));
        tablesToPreserve.addAll(toCorrectCaseIdentifiers(asList(configuration.getStringArray(PROPKEY_DBCLEARER_ITEMSTOPRESERVE))));
    }


    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i> , and the table in which the database version is stored
     */
    public void cleanDatabase() throws StatementHandlerException {
        try {
            logger.info("Cleaning database tables.");

            Set<String> tableNames = dbSupport.getTableNames();
            for (String tableName : tableNames) {
                // check whether table needs to be preserved
                if (tablesToPreserve.contains(tableName)) {
                    continue;
                }
                cleanTable(tableName);
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while cleaning database", e);
        }
    }


    /**
     * Deletes the data in the table with the given name.
     * Note: the table name is surrounded with quotes, to make sure that
     * case-sensitive table names are also deleted correctly.
     *
     * @param tableName The name of the table that need to be cleared, not null
     */
    protected void cleanTable(String tableName) throws SQLException {
        logger.debug("Cleaning database table: " + tableName);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("delete from \"" + tableName + "\"");

        } finally {
            closeQuietly(connection, statement, null);
        }
    }


    /**
     * Converts the given list of identifiers to uppercase/lowercase depending on what the default
     * for the database is. If a value is surrounded with double quotes (") it will not be converted and
     * the double quotes will be stripped. These values are treated as case sensitive names.
     *
     * @param identifiers The identifiers, not null
     * @return The names converted to correct case if needed, not null
     */
    protected List<String> toCorrectCaseIdentifiers(List<String> identifiers) {
        Connection connection = null;
        boolean toUpperCase;
        boolean toLowerCase;
        try {
            connection = dataSource.getConnection();
            toUpperCase = connection.getMetaData().storesUpperCaseIdentifiers();
            toLowerCase = connection.getMetaData().storesLowerCaseIdentifiers();
        } catch (SQLException e) {
            throw new UnitilsException("Unable to convert identifiers to correct case.", e);
        } finally {
            closeQuietly(connection, null, null);
        }

        List<String> result = new ArrayList<String>();
        for (String identifier : identifiers) {
            identifier = identifier.trim();
            if (identifier.startsWith("\"") && identifier.endsWith("\"")) {
                result.add(identifier.substring(1, identifier.length() - 1));
            } else if (toUpperCase) {
                result.add(identifier.toUpperCase());
            } else if (toLowerCase) {
                result.add(identifier.toLowerCase());
            } else {
                result.add(identifier);
            }
        }
        return result;
    }
}