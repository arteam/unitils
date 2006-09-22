/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.maintainer.version;

import javax.sql.DataSource;

/**
 * Interface that gives access to the version of a database, and a means to increment this version
 */
public interface VersionSource {

    /**
     * Initializes the VersionSource
     *
     * @param dataSource
     */
    void init(DataSource dataSource);

    /**
     * Returns the current version of the underlying database
     *
     * @return The current version of the underlying database
     */
    Version getDbVersion();

    /**
     * Updates the version of the database to the given value
     *
     * @param version The new version that the database should be updated to
     */
    void setDbVersion(Version version);
}
