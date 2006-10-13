package org.unitils.dbmaintainer.clean;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 *
 */
public interface DBCleaner {

    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    void cleanDatabase() throws StatementHandlerException;

}
