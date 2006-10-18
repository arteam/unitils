/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.dtd;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;

/**
 * Defines the contract for implementations that create a DTD that describes a database. This DTD can 
 * be used in XML files containing test data for a unit test database
 */
public interface DtdGenerator {

    /**
     * Initializes the connection to the database, and some other properties
     * @param configuration
     * @param dataSource
     */
    void init(Configuration configuration, DataSource dataSource);

    /**
     * Generates the DTD
     */
    void generateDtd();

}
