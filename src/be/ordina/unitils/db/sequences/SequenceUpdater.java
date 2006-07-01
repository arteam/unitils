package be.ordina.unitils.db.sequences;

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
     */
    void init(Properties properties, DataSource dataSource);

    /**
     * Updates the database sequences
     */
    void updateSequences();

}
