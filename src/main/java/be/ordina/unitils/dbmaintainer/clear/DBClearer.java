package be.ordina.unitils.dbmaintainer.clear;

import be.ordina.unitils.dbmaintainer.handler.StatementHandler;
import be.ordina.unitils.dbmaintainer.handler.StatementHandlerException;

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
