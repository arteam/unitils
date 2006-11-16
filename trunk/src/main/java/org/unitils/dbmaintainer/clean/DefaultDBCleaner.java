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
package org.unitils.dbmaintainer.clean;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Implementation of {@link DBCleaner}. This implementation doesn't use any DBMS specific features, so it should work
 * for every database.
 *
 * // todo don't clear dbclearer itemstopreserve
 */
public class DefaultDBCleaner extends DatabaseTask implements DBCleaner {

    /* Property key for the database schema name */
    public static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* Property key for the tables that should not be cleaned */
    public static final String PROPKEY_TABLESTOPRESERVE = "dbMaintainer.cleanDb.tablesToPreserve";

    /* The key of the property that specifies the name of the datase table in which the
     * DB version is stored. This table should not be deleted */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    /* The tables that should not be cleaned */
    private Set<String> tablesToPreserve;

    /**
     * Configures this object
     *
     * @param configuration
     */
    protected void doInit(Configuration configuration) {
        tablesToPreserve = new HashSet<String>();
        tablesToPreserve.add(configuration.getString(PROPKEY_VERSION_TABLE_NAME).toUpperCase());
        tablesToPreserve.addAll(toUpperCaseList(Arrays.asList(configuration.getStringArray(PROPKEY_TABLESTOPRESERVE))));
    }

    /**
     * Deletes all data from all tables in the database, except
     *
     * @throws StatementHandlerException
     */
    public void cleanDatabase() throws StatementHandlerException {
        try {
            Set<String> tables = dbSupport.getTableNames();
            tables.removeAll(tablesToPreserve);
            clearTables(tables);
        } catch (SQLException e) {
            throw new UnitilsException("Error while cleaning database", e);
        }
    }

    /**
     * Deletes the data in the database tables with the given table names.
     *
     * @param tableNames
     * @throws StatementHandlerException
     */
    private void clearTables(Set<String> tableNames) throws StatementHandlerException {
        for (String tableName : tableNames) {
            statementHandler.handle("delete from " + tableName);
        }
    }

    /**
     * Converts the given list of strings to uppercase.
     *
     * @param strings
     * @return the given string list, converted to uppercase
     */
    private List<String> toUpperCaseList(List<String> strings) {
        List<String> toUpperCaseList = new ArrayList<String>();
        for (String string : strings) {
            toUpperCaseList.add(string.toUpperCase());
        }
        return toUpperCaseList;
    }
}
