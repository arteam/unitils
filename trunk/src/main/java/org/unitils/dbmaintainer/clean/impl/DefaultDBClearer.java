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

import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.util.PropertyUtils.getStringList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Implementation of {@link DBClearer}. This implementation individually drops every table, view, constraint,
 * trigger and sequence in the database. A list of tables, views, ... that should be preserverd can be specified
 * using the property {@link #PROPKEY_ITEMSTOPRESERVE}.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultDBClearer extends DatabaseTask implements DBClearer {

    /**
     * The key of the property that specifies which database items should not be deleted when clearing the database
     */
    public static final String PROPKEY_ITEMSTOPRESERVE = "dbMaintainer.clearDb.itemsToPreserve";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearer.class);

    /**
     * Names of database items (tables, views, sequences or triggers) that should not be deleted when clearning the database
     */
    protected Set<String> itemsToPreserve = new HashSet<String>();


    /**
     * Initializes the the DBClearer. The list of database items that should be preserved is retrieved from the given
     * <code>Configuration</code> object.
     *
     * @param configuration the config, not null
     */
    protected void doInit(Properties configuration) {
        itemsToPreserve.addAll(toCorrectCaseIdentifiers(getStringList(PROPKEY_ITEMSTOPRESERVE, configuration)));
    }


    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers and sequences are
     * dropped, so that the database schema is empty. The database items that are configured as items to preserve, are
     * left untouched.
     */
    public void clearSchema() throws StatementHandlerException {
        logger.info("Clearing (dropping) the unit test database.");
        dropViews();
        dropTables();
        // todo test dropping synonmys
        dropSynonyms();
        dropSequences();
        dropTriggers();
        dropTypes();
    }


    /**
     * Drops all views.
     */
    protected void dropViews() throws StatementHandlerException {
        Set<String> viewNames = dbSupport.getViewNames();
        for (String viewName : viewNames) {
            // check whether view needs to be preserved
            if (itemsToPreserve.contains(viewName)) {
                continue;
            }
            logger.debug("Dropping database view: " + viewName);
            dbSupport.dropView(viewName);
        }
    }


    /**
     * Drops all tables.
     */
    protected void dropTables() throws StatementHandlerException {
        Set<String> tableNames = dbSupport.getTableNames();
        for (String tableName : tableNames) {
            // check whether table needs to be preserved
            if (itemsToPreserve.contains(tableName)) {
                continue;
            }
            logger.debug("Dropping database table: " + tableName);
            dbSupport.dropTable(tableName);
        }
    }


    /**
     * Drops all synonyms
     */
    protected void dropSynonyms() throws StatementHandlerException {
        if (dbSupport.supportsSynonyms()) {
            Set<String> synonymNames = dbSupport.getSynonymNames();
            for (String synonymName : synonymNames) {
                // check whether table needs to be preserved
                if (itemsToPreserve.contains(synonymName)) {
                    continue;
                }
                logger.debug("Dropping database table: " + synonymName);
                dbSupport.dropSynonym(synonymName);
            }
        }
    }


    /**
     * Drops all sequences
     */
    protected void dropSequences() throws StatementHandlerException {
        if (dbSupport.supportsSequences()) {
            Set<String> sequenceNames = dbSupport.getSequenceNames();
            for (String sequenceName : sequenceNames) {
                // check whether sequence needs to be preserved
                if (itemsToPreserve.contains(sequenceName)) {
                    continue;
                }
                logger.debug("Dropping database sequence: " + sequenceName);
                dbSupport.dropSequence(sequenceName);
            }
        }
    }


    /**
     * Drops all triggers
     */
    protected void dropTriggers() throws StatementHandlerException {
        if (dbSupport.supportsTriggers()) {
            Set<String> triggerNames = dbSupport.getTriggerNames();
            for (String triggerName : triggerNames) {
                // check whether trigger needs to be preserved
                if (itemsToPreserve.contains(triggerName)) {
                    continue;
                }
                logger.debug("Dropping database trigger: " + triggerName);
                dbSupport.dropTrigger(triggerName);
            }
        }
    }


    /**
     * Drops all types.
     */
    protected void dropTypes() throws StatementHandlerException {
        if (dbSupport.supportsTypes()) {
            Set<String> typeNames = dbSupport.getTypeNames();
            for (String typeName : typeNames) {
                // check whether type needs to be preserved
                if (itemsToPreserve.contains(typeName)) {
                    continue;
                }
                logger.debug("Dropping database type: " + typeName);
                dbSupport.dropType(typeName);
            }
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