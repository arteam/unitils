/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;

/**
 * TODO test me
 * TODO javadoc me
 * TODO currently only the FK constraints are handled
 *
 * @author BaVe
 */
public class MySQLConstraintsDisabler implements ConstraintsDisabler {

    /**
     * The DataSource
     */
    private DataSource dataSource;

    /**
     * The StatementHandler
     */
    private StatementHandler statementHandler;

    /**
     * @see ConstraintsDisabler#init(javax.sql.DataSource,
     *      org.unitils.dbmaintainer.handler.StatementHandler)
     */
    public void init(DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#enableConstraints()
     */
    public void enableConstraints() {
        try {
            statementHandler.handle("SET FOREIGN_KEY_CHECKS = 1");
        } catch (StatementHandlerException e) {
            throw new RuntimeException("Error while enabling contstraints", e);
        }
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() {
        try {
            statementHandler.handle("SET FOREIGN_KEY_CHECKS = 0");
        } catch (StatementHandlerException e) {
            throw new RuntimeException("Error while disabling contstraints", e);
        }
    }
}
