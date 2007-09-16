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

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.util.BaseDatabaseTask;

/**
 * Implementation of {@link DBClearer}. This implementation individually drops every table, view, constraint,
 * trigger and sequence in the database. A list of tables, views, ... that should be preserverd can be specified
 * using the property {@link #PROPKEY_PRESERVE_TABLES}.
 * <p/>
 * NOTE: FK constraints are give problems in MySQL
 * The cascade in drop table A cascade; does not work in MySQL-5.0
 * The DBMaintainer will first remove all constraints before calling the db clearer
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDBClearer extends BaseDatabaseTask implements DBClearer {

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
     * The key of the property that specifies which synonyms should not be dropped
     */
    public static final String PROPKEY_PRESERVE_SYNONYMS = "dbMaintainer.preserve.synonyms";

    /**
     * The key of the property that specifies which sequences should not be dropped
     */
    public static final String PROPKEY_PRESERVE_SEQUENCES = "dbMaintainer.preserve.sequences";


    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearer.class);

    /**
     * Names of schemas that should left untouched.
     */
    protected Set<String> schemasToPreserve;

    /**
     * Names of tables that should not be dropped.
     */
    protected Set<String> tablesToPreserve;

    /**
     * Names of views that should not be dropped.
     */
    protected Set<String> viewsToPreserve;

    /**
     * Names of synonyms that should not be dropped.
     */
    protected Set<String> synonymsToPreserve;

    /**
     * Names of sequences that should not be dropped.
     */
    protected Set<String> sequencesToPreserve;


    /**
     * Initializes the the DBClearer. The list of database items that should be preserved is retrieved from the given
     * <code>Configuration</code> object.
     *
     * @param configuration the config, not null
     */
    @Override
    protected void doInit(Properties configuration) {
        schemasToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SCHEMAS, configuration, false);
        tablesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_TABLES, configuration, true);
        viewsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_VIEWS, configuration, true);
        synonymsToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SYNONYMS, configuration, true);
        sequencesToPreserve = getItemsToPreserve(PROPKEY_PRESERVE_SEQUENCES, configuration, true);
    }


    /**
     * Clears the database schemas. This means, all the tables, views, constraints, triggers and sequences are
     * dropped, so that the database schema is empty. The database items that are configured as items to preserve, are
     * left untouched.
     */
    public void clearSchemas() {
        for (DbSupport dbSupport : dbSupports) {
            // check whether schema needs to be preserved
            if (schemasToPreserve.contains(dbSupport.getSchemaName())) {
                continue;
            }
            logger.info("Clearing (dropping) database schema " + dbSupport.getSchemaName());
            dropTables(dbSupport);
            dropViews(dbSupport);
            dropSynonyms(dbSupport);
            dropSequences(dbSupport);
        }
    }


    /**
     * Drops all tables.
     *
     * @param dbSupport The database support, not null
     */
    protected void dropTables(DbSupport dbSupport) {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            // check whether table needs to be preserved
            if (tablesToPreserve.contains(dbSupport.getSchemaName() + "." + tableName)) {
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
        for (String viewName : viewNames) {
            // check whether view needs to be preserved
            if (viewsToPreserve.contains(dbSupport.getSchemaName() + "." + viewName)) {
                continue;
            }
            logger.debug("Dropping view " + viewName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropView(viewName);
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
        for (String synonymName : synonymNames) {
            // check whether table needs to be preserved
            if (synonymsToPreserve.contains(dbSupport.getSchemaName() + "." + synonymName)) {
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
        for (String sequenceName : sequenceNames) {
            // check whether sequence needs to be preserved
            if (sequencesToPreserve.contains(dbSupport.getSchemaName() + "." + sequenceName)) {
                continue;
            }
            logger.debug("Dropping sequence " + sequenceName + " in database schema " + dbSupport.getSchemaName());
            dbSupport.dropSequence(sequenceName);
        }
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