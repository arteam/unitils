/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.config;

import javax.sql.DataSource;

/**
 * Interface for different sorts of Factories of a DataSource.
 * <p/>
 * todo javadoc
 */
public interface DataSourceFactory {

    /**
     * Initializes itself using the properties in the given <code>Properties</code> object.
     *
     * @throws IllegalArgumentException When the given <code>Properties</code> misses one or more required properties.
     */
    public void init();


    /**
     * Retrieve the DataSource
     *
     * @return the DataSource
     */
    public DataSource createDataSource();

}
