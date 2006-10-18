/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.config;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;

/**
 * Interface for different sorts of Factories of a TestDataSource.
 * <p/>
 * todo javadoc
 */
public interface DataSourceFactory {

    /**
     * Initializes itself using the properties in the given <code>Configuration</code> object.

     * @param configuration 
     */
    public void init(Configuration configuration);


    /**
     * Creates a new <code>TestDataSource</code>
     *
     * @return the TestDataSource
     */
    public DataSource createDataSource();

}
