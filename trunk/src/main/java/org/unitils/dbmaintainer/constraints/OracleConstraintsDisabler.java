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
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementation of {@link ConstraintsDisabler} for an Oracle database.
 */
public class OracleConstraintsDisabler implements ConstraintsDisabler {

    /**
     * SQL statement to select the database constraints
     */
    private static final String SELECT_CONSTRAINTS_SQL = "select table_name, constraint_name "
            + " from user_constraints where constraint_type <> 'P'";

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
     * @see ConstraintsDisabler#enableConstraints()
     */
    public void enableConstraints() {
        generateConstraintsScript("enable");
    }

    /**
     * @see ConstraintsDisabler#enableConstraintsOnConnection(java.sql.Connection)
     */
    public void enableConstraintsOnConnection(Connection conn) {
    }

    /**
     * @see ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() {
        generateConstraintsScript("disable");
    }

    /**
     * @see ConstraintsDisabler#disableConstraintsOnConnection(java.sql.Connection)
     */
    public void disableConstraintsOnConnection(Connection conn) {
    }

    private void generateConstraintsScript(String enableDisable) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SELECT_CONSTRAINTS_SQL);
            output(resultSet, enableDisable);
        } catch (SQLException e) {
            throw new UnitilsException("Error while " + enableDisable + "ing constraints", e);
        } catch (StatementHandlerException e) {
            throw new UnitilsException("Error while " + enableDisable + "ing constraints", e);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
    }

    private void output(ResultSet resultSet, String enableDisable) throws SQLException, StatementHandlerException {
        while (resultSet.next()) {
            StringBuffer buf = new StringBuffer();
            buf.append("alter table ");
            buf.append(resultSet.getString("table_name"));
            buf.append(" ");
            buf.append(enableDisable);
            buf.append(" constraint ");
            buf.append(resultSet.getString("constraint_name"));
            statementHandler.handle(buf.toString());
        }
    }

}
