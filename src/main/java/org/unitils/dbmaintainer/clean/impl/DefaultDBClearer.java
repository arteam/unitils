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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

import java.sql.SQLException;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

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
    protected void doInit(Configuration configuration) {
        itemsToPreserve.addAll(dbSupport.toUpperCaseNames(asList(configuration.getStringArray(PROPKEY_ITEMSTOPRESERVE))));
    }


    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers and sequences are
     * dropped, so that the database schema is empty. The database items that are configured as items to preserve, are
     * left untouched.
     */
    public void clearDatabase() throws StatementHandlerException {
        try {
            logger.info("Clearing (dropping) the unit test database");
            dropViews();
            dropTables();
            dropSequences();
            dropTriggers();
        } catch (SQLException e) {
            throw new UnitilsException("Error while clearing database", e);
        }
    }


    /**
     * Drops all views.
     */
    protected void dropViews() throws SQLException, StatementHandlerException {
        Set<String> viewNames = dbSupport.getViewNames();
        viewNames.removeAll(itemsToPreserve);
        for (String viewName : viewNames) {
            dbSupport.dropView(viewName);
        }
    }


    /**
     * Drops all tables.
     */
    protected void dropTables() throws SQLException, StatementHandlerException {
        Set<String> tableNames = dbSupport.getTableNames();
        tableNames.removeAll(itemsToPreserve);
        for (String tableName : tableNames) {
            dbSupport.dropTable(tableName);
        }
    }


    /**
     * Drops all sequences
     */
    protected void dropSequences() throws StatementHandlerException, SQLException {
        if (dbSupport.supportsSequences()) {
            Set<String> sequenceNames = dbSupport.getSequenceNames();
            sequenceNames.removeAll(itemsToPreserve);
            for (String sequenceName : sequenceNames) {
                dbSupport.dropSequence(sequenceName);
            }
        }
    }


    /**
     * Drops all triggers
     */
    protected void dropTriggers() throws StatementHandlerException, SQLException {
        if (dbSupport.supportsTriggers()) {
            Set<String> triggerNames = dbSupport.getTriggerNames();
            triggerNames.removeAll(itemsToPreserve);
            for (String triggerName : triggerNames) {
                dbSupport.dropTrigger(triggerName);
            }
        }
    }

}