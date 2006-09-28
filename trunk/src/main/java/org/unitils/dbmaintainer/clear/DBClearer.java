package org.unitils.dbmaintainer.clear;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;

/**
 * @author Filip Neven
 */
public interface DBClearer {

    /**
     * Initializes the DBClearer
     *
     * @param dataSource
     * @param statementHandler
     */
    void init(DataSource dataSource, StatementHandler statementHandler);

    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers, sequences, ... are
     * dropped, so that the database schema is completely empty.
     */
    void clearDatabase() throws StatementHandlerException;

}
