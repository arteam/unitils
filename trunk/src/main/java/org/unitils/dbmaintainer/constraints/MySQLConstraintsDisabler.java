/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.core.UnitilsException;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * TODO test me
 * TODO javadoc me
 * TODO currently only the FK constraints are handled
 *
 * Implementation of {@link ConstraintsDisabler} for an MySQL database.
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
     * @see ConstraintsDisabler#init(org.apache.commons.configuration.Configuration,javax.sql.DataSource,org.unitils.dbmaintainer.handler.StatementHandler)
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#enableConstraints()
     */
    public void enableConstraints() {

    }

    /**
     * @see ConstraintsDisabler#enableConstraintsOnConnection(java.sql.Connection)
     */
    public void enableConstraintsOnConnection(Connection conn) {
        try {
            statementHandler.handle("SET FOREIGN_KEY_CHECKS = 1");
        } catch (StatementHandlerException e) {
            throw new UnitilsException("Error while enabling contstraints", e);
        }
    }

    /**
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() {

    }

    /**
     * @see ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)
     */
    public void disableConstraintsOnConnection(Connection conn) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling contstraints", e);
        }
    }
}
