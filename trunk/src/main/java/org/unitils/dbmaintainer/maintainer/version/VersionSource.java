/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.version;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 * Interface that gives access to the version of a database, and a means to increment this version
 */
public interface VersionSource {

    /**
     * Initializes the VersionSource
     *
     * @param configuration
     * @param dataSource
     * @param statementHandler
     */
    void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler);

    /**
     * Returns the current version of the underlying database
     *
     * @return The current version of the underlying database
     */
    Version getDbVersion() throws StatementHandlerException;

    /**
     * Updates the version of the database to the given value
     *
     * @param version The new version that the database should be updated to
     */
    void setDbVersion(Version version) throws StatementHandlerException;

    /**
     * Tells us whether the last database version update succeeded or not
     *
     * @return true if the last database version update succeeded, false otherwise
     */
    boolean lastUpdateSucceeded();

    /**
     * Notifies the VersionSource of the fact that the lastest version update has succeeded (succeeded == true) or
     * failed (succeeded = false)
     */
    void registerUpdateSucceeded(boolean succeeded) throws StatementHandlerException;
}
