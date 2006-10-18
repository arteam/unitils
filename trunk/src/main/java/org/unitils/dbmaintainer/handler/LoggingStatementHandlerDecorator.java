/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.handler;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * Decorator for another implementation of {@link StatementHandler}. Statements are written to log4j log, and
 * passed on to the {@link StatementHandler} that is decorated.
 */
public class LoggingStatementHandlerDecorator implements StatementHandler {

    /* The log4j Logger to which all statements are logged */
    private static final Logger logger = Logger.getLogger(StatementHandler.class);

    /* The StatementHandler that is decorated */
    private StatementHandler decoratedStatementHandler;

    /**
     * Creates a new instance that decorates the given instance.
     * @param decoratedStatementHandler
     */
    public LoggingStatementHandlerDecorator(StatementHandler decoratedStatementHandler) {
        this.decoratedStatementHandler = decoratedStatementHandler;
    }

    /**
     * Initializes the decorated instance.
     * @see org.unitils.dbmaintainer.handler.StatementHandler#init(org.apache.commons.configuration.Configuration, javax.sql.DataSource)
     */
    public void init(Configuration configuration, DataSource dataSource) {
        decoratedStatementHandler.init(configuration, dataSource);
    }

    /**
     * Handles the given statement, i.e. logs it and passes it through to the decorated {@link StatementHandler}
     * @see org.unitils.dbmaintainer.handler.StatementHandler#handle(java.lang.String)
     */
    public void handle(String statement) throws StatementHandlerException {
        logger.info(statement);
        decoratedStatementHandler.handle(statement);
    }
}
