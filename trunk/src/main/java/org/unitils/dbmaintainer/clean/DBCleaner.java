package org.unitils.dbmaintainer.clean;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;

/**
 *
 */
public interface DBCleaner {

    void init(DataSource dataSource, StatementHandler statementHandler);

    void cleanDatabase() throws StatementHandlerException;

}
