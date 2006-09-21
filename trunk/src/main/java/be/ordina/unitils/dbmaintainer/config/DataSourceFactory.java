/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.config;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface for different sorts of Factories of a DataSource.
 */
public interface DataSourceFactory {

    /**
     * Initializes itself using the properties in the given <code>Properties</code> object.
     *
     * @param properties The properties
     * @throws IllegalArgumentException When the given <code>Properties</code> misses one or more required properties.
     */
    public void init(Properties properties);


    /**
     * Retrieve the DataSource
     *
     * @return the DataSource
     */
    public DataSource createDataSource();

}
