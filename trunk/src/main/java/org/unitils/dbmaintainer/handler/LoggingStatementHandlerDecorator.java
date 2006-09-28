package org.unitils.dbmaintainer.handler;

import org.apache.log4j.Logger;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 */
public class LoggingStatementHandlerDecorator implements StatementHandler {

    private static final Logger logger = Logger.getLogger(StatementHandler.class);

    private StatementHandler decoratedStatementHandler;

    public LoggingStatementHandlerDecorator(StatementHandler decoratedStatementHandler) {
        this.decoratedStatementHandler = decoratedStatementHandler;
    }

    public void init(DataSource dataSource) {
        decoratedStatementHandler.init(dataSource);
    }

    public void handle(String statement) throws StatementHandlerException {
        logger.info(statement);
        decoratedStatementHandler.handle(statement);
    }
}
