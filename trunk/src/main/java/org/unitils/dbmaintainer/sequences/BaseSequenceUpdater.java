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
package org.unitils.dbmaintainer.sequences;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Base convenience implementation of {@link SequenceUpdater}
 */
abstract public class BaseSequenceUpdater implements SequenceUpdater {

    /* Property key for the lowest acceptacle sequence value */
    public static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    /* Property key for the name of the database schema */
    public static final String PROPKEY_SCHEMA_NAME = "dataSource.schemaName";

    /* The <code>TestDataSource</code> that provides the connection to the database */
    protected DataSource dataSource;

    /* The StatementHandler on which the sequence update statements will be executed */
    protected StatementHandler statementHandler;

    /* The lowest acceptable sequence value */
    protected long lowestAcceptableSequenceValue;

    /* The name of the database schema */
    protected String schemaName;

    /**
     * @see SequenceUpdater#init(Configuration,DataSource,StatementHandler)
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        lowestAcceptableSequenceValue = configuration.getLong(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE);
        schemaName = configuration.getString(PROPKEY_SCHEMA_NAME).toUpperCase();
    }

}
