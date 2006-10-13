package org.unitils.dbmaintainer.sequences;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 * Interface for implementation classes that update the sequence of the unit testing database to a sufficiently
 * high value, so that test data can be inserted without problems.
 */
public interface SequenceUpdater {

    /**
     * Initializes the VersionSource
     *
     * @param dataSource
     * @param statementHandler
     */
    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Updates the database sequences
     */
    void updateSequences() throws StatementHandlerException;

}
