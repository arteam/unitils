package be.ordina.unitils.dbmaintainer.sequences;

import be.ordina.unitils.dbmaintainer.handler.StatementHandler;
import be.ordina.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface for implementation classes that update the sequence of the unit testing database to a sufficiently
 * high value, so that test data can be inserted without problems.
 */
public interface SequenceUpdater {

    /**
     * Initializes the VersionSource
     *
     * @param properties
     * @param dataSource
     * @param statementHandler
     */
    void init(Properties properties, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Updates the database sequences
     */
    void updateSequences() throws StatementHandlerException;

}
