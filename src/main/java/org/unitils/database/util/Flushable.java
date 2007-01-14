package org.unitils.database.util;

/**
 * Marks a module as being flushable. This means that {@link #flushDatabaseUpdates()} will be called on the module
 * when a flush is requested on the DatabaseModule (by calling its {@link #flushDatabaseUpdates()} method).
 * <p/>
 * An example of when a module could need to be flushable is the HibernateModule. Hibernate sometimes stores
 * updates in the session (in memory) without performing them on the database. If you want to be sure that every such
 * update was performed on the database, you need to flush the hibernate session.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface Flushable {

    /**
     * Flush all cached database operations.
     */
    void flushDatabaseUpdates();
}
