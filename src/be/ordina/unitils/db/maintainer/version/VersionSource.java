/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.version;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface that gives access to the version of a database, and a means to increment this version
 */
public interface VersionSource {

    /**
     * Initializes the VersionSource
     *
     * @param properties
     * @param dataSource
     */
    void init(Properties properties, DataSource dataSource);

    /**
     * Returns the current version of the underlying database
     *
     * @return The current version of the underlying database
     */
    long getDbVersion();

    /**
     * Updates the version of the database to the given value
     *
     * @param version
     */
    void setDbVersion(long version);
}
