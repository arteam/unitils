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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.dbsupport.DatabaseTask;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Base implementation of {@link DBClearer}. This implementation uses plain JDBC and standard SQL
 * for most of the work, and defers DBMS specific work to its subclasses.
 */
public class DefaultDBClearer extends DatabaseTask implements DBClearer {

    /* The key of the property that specifies which database items should not be deleted when clearing the database */
    public static final String PROPKEY_ITEMSTOPRESERVE = "dbMaintainer.clearDb.itemsToPreserve";

    /* Names of database items (tables, views, sequences or triggers) that should not be deleted when clearning the
        database */
    private Set<String> itemsToPreserve = new HashSet<String>();

    /**
     * @param configuration
     */
    protected void doInit(Configuration configuration) {
        String[] itemsToPreserveArray = configuration.getStringArray(PROPKEY_ITEMSTOPRESERVE);
        for (String itemToPreserve : itemsToPreserveArray) {
            itemsToPreserve.add(itemToPreserve.toUpperCase());
        }
    }

    /**
     * Clears the database schema.
     *
     * @throws StatementHandlerException
     */
    public void clearDatabase() throws StatementHandlerException {
        try {
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
     *
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void dropViews() throws SQLException, StatementHandlerException {
        Set<String> viewNames = dbSupport.getViewNames();
        viewNames.removeAll(itemsToPreserve);
        for (String viewName : viewNames) {
            dbSupport.dropView(viewName);
        }
    }

    /**
     * Drops all tables.
     *
     * @throws SQLException
     * @throws StatementHandlerException
     */
    private void dropTables() throws SQLException, StatementHandlerException {
        Set<String> tableNames = dbSupport.getTableNames();
        tableNames.removeAll(itemsToPreserve);
        for (String tableName : tableNames) {
            dbSupport.dropTable(tableName);
        }
    }

    /**
     * Drops all sequences in the database
     *
     * @throws StatementHandlerException
     * @throws SQLException
     */
    private void dropSequences() throws StatementHandlerException,
            SQLException {
        Set<String> sequenceNames = dbSupport.getSequenceNames();
        sequenceNames.removeAll(itemsToPreserve);
        for (String sequenceName : sequenceNames) {
            dbSupport.dropSequence(sequenceName);
        }
    }

    /**
     * Drops all database triggers
     *
     * @throws StatementHandlerException
     * @throws SQLException
     */
    private void dropTriggers() throws StatementHandlerException,
            SQLException {
        Set<String> triggerNames = dbSupport.getTriggerNames();
        triggerNames.removeAll(itemsToPreserve);
        for (String triggerName : triggerNames) {
            dbSupport.dropTrigger(triggerName);
        }
    }

}
