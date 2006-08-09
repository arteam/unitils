package be.ordina.unitils.db.clear;

import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public interface DBClearer {

    /**
     * Initializes the DBClearer
     *
     * @param properties
     * @param dataSource
     * @param statementHandler
     */
    void init(Properties properties, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers, sequences, ... are
     * dropped, so that the database schema is completely empty.
     */
    void clearDatabase() throws StatementHandlerException;

}
