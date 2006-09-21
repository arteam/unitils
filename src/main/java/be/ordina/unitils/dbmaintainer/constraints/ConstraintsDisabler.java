/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.dbmaintainer.constraints;

import be.ordina.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * 
 */
public interface ConstraintsDisabler {

    /**
     * Initializes the ConstraintsDisabler
     *
     * @param dataSource
     * @param statementHandler
     */
    void init(DataSource dataSource, StatementHandler statementHandler);

    /**
     * Generates statements to enable the constraints of the associated database, and passes them to the
     * associated statementHandler
     */
    void enableConstraints();

    /**
     * enerates statements to disable the constraints of the associated database, and passes them to the
     * associated statementHandler
     */
    void disableConstraints();

}
