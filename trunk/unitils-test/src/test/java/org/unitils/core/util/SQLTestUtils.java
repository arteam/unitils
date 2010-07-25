/*
 * Copyright Unitils.org
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
package org.unitils.core.util;

import org.dbmaintain.database.Database;
import org.dbmaintain.util.DbMaintainException;

/**
 * Utilities for creating and dropping test tables, views....
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SQLTestUtils {


    /**
     * Drops the test tables
     *
     * @param database   The database, not null
     * @param tableNames The tables to drop
     */
    public static void dropTestTables(Database database, String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseTableName = database.toCorrectCaseIdentifier(tableName);
                database.dropTable(defaultSchemaName, correctCaseTableName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param database  The db support, not null
     * @param viewNames The views to drop
     */
    public static void dropTestViews(Database database, String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseViewName = database.toCorrectCaseIdentifier(viewName);
                database.dropView(defaultSchemaName, correctCaseViewName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test materialized views
     *
     * @param database              The db support, not null
     * @param materializedViewNames The views to drop
     */
    public static void dropTestMaterializedViews(Database database, String... materializedViewNames) {
        for (String materializedViewName : materializedViewNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseViewName = database.toCorrectCaseIdentifier(materializedViewName);
                database.dropMaterializedView(defaultSchemaName, correctCaseViewName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test synonyms
     *
     * @param database     The db support, not null
     * @param synonymNames The views to drop
     */
    public static void dropTestSynonyms(Database database, String... synonymNames) {
        for (String synonymName : synonymNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseSynonymName = database.toCorrectCaseIdentifier(synonymName);
                database.dropSynonym(defaultSchemaName, correctCaseSynonymName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param database      The db support, not null
     * @param sequenceNames The sequences to drop
     */
    public static void dropTestSequences(Database database, String... sequenceNames) {
        for (String sequenceName : sequenceNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseSequenceName = database.toCorrectCaseIdentifier(sequenceName);
                database.dropSequence(defaultSchemaName, correctCaseSequenceName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param database     The db support, not null
     * @param triggerNames The triggers to drop
     */
    public static void dropTestTriggers(Database database, String... triggerNames) {
        for (String triggerName : triggerNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseTriggerName = database.toCorrectCaseIdentifier(triggerName);
                database.dropTrigger(defaultSchemaName, correctCaseTriggerName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test types
     *
     * @param database  The db support, not null
     * @param typeNames The types to drop
     */
    public static void dropTestTypes(Database database, String... typeNames) {
        for (String typeName : typeNames) {
            try {
                String defaultSchemaName = database.getDefaultSchemaName();
                String correctCaseTypeName = database.toCorrectCaseIdentifier(typeName);
                database.dropType(defaultSchemaName, correctCaseTypeName);
            } catch (DbMaintainException e) {
                // Ignored
            }
        }
    }

}
