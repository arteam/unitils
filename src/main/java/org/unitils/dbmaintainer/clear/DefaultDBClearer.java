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

/**
 * Base implementation of {@link DBClearer}. This implementation uses plain JDBC and standard SQL
 * for most of the work, and defers DBMS specific work to its subclasses.
 */
public class DefaultDBClearer extends DatabaseTask implements DBClearer {

    /*
     * The key of the property that specifies the name of the datase table in which the DB version
     * is stored. This table should not be deleted
     */
    public static final String PROPKEY_VERSION_TABLE_NAME = "dbMaintainer.dbVersionSource.tableName";

    public DefaultDBClearer() {
        System.out.println("DefaultDBClearer.DefaultDBClearer");
    }

    /**
     * @param configuration
     */
    protected void doInit(Configuration configuration) {
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
        List<String> viewNames = dbSupport.getViewNames();
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
        for (String triggerName : triggerNames) {
            dbSupport.dropTrigger(triggerName);
        }
    }

}
