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
package org.unitils.core.dbsupport;

import org.unitils.core.UnitilsException;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.core.util.SQLUtils.getItemsAsStringSet;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Some utility methods for the unit tests.
 */
public class TestSQLUtils {


    /**
     * Drops the test tables
     *
     * @param dbSupport  The db support, not null
     * @param tableNames The tables to drop
     */
    public static void dropTestTables(DbSupport dbSupport, String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
                dbSupport.dropTable(correctCaseTableName);
            } catch (UnitilsException e) {
                // Ignored                
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param dbSupport The db support, not null
     * @param viewNames The views to drop
     */
    public static void dropTestViews(DbSupport dbSupport, String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(viewName);
                dbSupport.dropView(correctCaseViewName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test synonyms
     *
     * @param dbSupport    The db support, not null
     * @param synonymNames The views to drop
     */
    public static void dropTestSynonyms(DbSupport dbSupport, String... synonymNames) {
        for (String synonymName : synonymNames) {
            try {
                String correctCaseSynonymName = dbSupport.toCorrectCaseIdentifier(synonymName);
                dbSupport.dropSynonym(correctCaseSynonymName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param dbSupport     The db support, not null
     * @param sequenceNames The sequences to drop
     */
    public static void dropTestSequences(DbSupport dbSupport, String... sequenceNames) {
        for (String sequenceName : sequenceNames) {
            try {
                String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier(sequenceName);
                dbSupport.dropSequence(correctCaseSequenceName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param dbSupport    The db support, not null
     * @param triggerNames The triggers to drop
     */
    public static void dropTestTriggers(DbSupport dbSupport, String... triggerNames) {
        for (String triggerName : triggerNames) {
            try {
                String correctCaseTriggerName = dbSupport.toCorrectCaseIdentifier(triggerName);
                dbSupport.dropTrigger(correctCaseTriggerName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test types
     *
     * @param dbSupport The db support, not null
     * @param typeNames The types to drop
     */
    public static void dropTestTypes(DbSupport dbSupport, String... typeNames) {
        for (String typeName : typeNames) {
            try {
                String correctCaseTypeName = dbSupport.toCorrectCaseIdentifier(typeName);
                dbSupport.dropType(correctCaseTypeName);
            } catch (UnitilsException e) {
                // Ignored
            }
        }
    }


    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql        The sql string for retrieving the items
     * @param dataSource The data source, not null
     * @return The nr of updates, -1 if not succesful
     */
    public static int executeUpdateQuietly(String sql, DataSource dataSource) {
        try {
            return executeUpdate(sql, dataSource);
        } catch (UnitilsException e) {
            // Ignored
            return -1;
        }
    }


    /**
     * Utility method to check whether the given table is empty.
     *
     * @param tableName The table, not null
     * @param dbSupport The db support, not null
     * @return True if empty
     */
    public static boolean isEmpty(String tableName, DbSupport dbSupport) throws SQLException {
        String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
        return getItemsAsStringSet("select * from " + tableName, dbSupport.getDataSource()).isEmpty();
    }

}
