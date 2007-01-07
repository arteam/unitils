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
package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Base class for a task that can be performed on a database schema.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DatabaseTask {

    /* Property key for the database schema name */
    public static final String PROPKEY_SCHEMANAME = "dataSource.schemaName";

    /* Implementation of DbSupport, for executing all sorts of database operations and queries */
    protected DbSupport dbSupport;

    /* Provides connections to the unit test database*/
    protected DataSource dataSource;

    /* Name of the unit test database schema */
    protected String schemaName;

    /* Handles update statements that are issued to the database */
    protected StatementHandler statementHandler;


    /**
     * Initializes the database operation class with the given {@link Configuration}, {@link DbSupport}, {@link DataSource}
     * and {@link StatementHandler}
     *
     * @param configuration    The configuration, not null
     * @param dbSupport        The database type specific support instance, not null
     * @param dataSource       The datasource, not null
     * @param statementHandler The statement executor, not null
     */
    public void init(Configuration configuration, DbSupport dbSupport, DataSource dataSource, StatementHandler statementHandler) {
        this.dbSupport = dbSupport;
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        schemaName = configuration.getString(PROPKEY_SCHEMANAME).toUpperCase();

        doInit(configuration);
    }


    /**
     * Allows subclasses to perform some extra configuration using the given <code>Configuration</code> object
     *
     * @param configuration The configuration, not null
     */
    abstract protected void doInit(Configuration configuration);

}
