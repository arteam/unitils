package be.ordina.unitils.db.handler;

import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class LoggingStatementHandlerDecorator implements StatementHandler {

    private static final Logger logger = Logger.getLogger(StatementHandler.class);

    private StatementHandler decoratedStatementHandler;

    public LoggingStatementHandlerDecorator(StatementHandler decoratedStatementHandler) {
        this.decoratedStatementHandler = decoratedStatementHandler;
    }

    public void init(Properties properties, DataSource dataSource) {
        decoratedStatementHandler.init(properties, dataSource);
    }

    public void handle(String statement) throws StatementHandlerException {
        logger.info(statement);
        decoratedStatementHandler.handle(statement);
    }
}
