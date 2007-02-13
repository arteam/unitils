package org.unitils.dbmaintainer.clean;

import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

/**
 * Defines the contract for implementations that clear all database source code from a database schema, to avoid problems
 * on redeployment of database source code by the {@link org.unitils.dbmaintainer.DBMaintainer}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DBCodeClearer {

    /**
     * Clears all code from the database schema.
     */
    void clearSchemaCode() throws StatementHandlerException;

}
