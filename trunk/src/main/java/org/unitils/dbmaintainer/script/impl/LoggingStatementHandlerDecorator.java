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
package org.unitils.dbmaintainer.script.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.dbmaintainer.script.StatementHandler;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Decorator for another implementation of {@link StatementHandler}. Statements are written to log4j log, and
 * passed on to the {@link StatementHandler} that is decorated.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class LoggingStatementHandlerDecorator implements StatementHandler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(StatementHandler.class);

    /* The StatementHandler that is decorated */
    private StatementHandler decoratedStatementHandler;


    /**
     * Creates a new instance that decorates the given instance.
     *
     * @param decoratedStatementHandler the handler, not null
     */
    public LoggingStatementHandlerDecorator(StatementHandler decoratedStatementHandler) {
        this.decoratedStatementHandler = decoratedStatementHandler;
    }


    /**
     * Initializes the decorated instance.
     *
     * @see StatementHandler#init(Properties,DataSource)
     */
    public void init(Properties configuration, DataSource dataSource) {
        decoratedStatementHandler.init(configuration, dataSource);
    }


    /**
     * Handles the given statement, i.e. logs it and passes it through to the decorated {@link StatementHandler}
     *
     * @see StatementHandler#handle(String)
     */
    public void handle(String statement) throws StatementHandlerException {
        logger.info(statement);
        decoratedStatementHandler.handle(statement);
    }
}
