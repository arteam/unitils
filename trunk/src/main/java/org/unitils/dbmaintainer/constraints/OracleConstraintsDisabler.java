/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleConstraintsDisabler implements ConstraintsDisabler {

    /**
     * SQL statement to select the database constraints
     */
    private static final String DISABLE_CONSTRAINTS_SQL = "select table_name, constraint_name "
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
     * @see ConstraintsDisabler#init(javax.sql.DataSource, org.unitils.dbmaintainer.handler.StatementHandler)
     */
    public void init(DataSource dataSource, StatementHandler statementHandler) {
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
     * @see ConstraintsDisabler#disableConstraints()
     */
    public void disableConstraints() {
        generateConstraintsScript("disable");
    }

    private void generateConstraintsScript(String enableDisable) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(DISABLE_CONSTRAINTS_SQL);
            output(resultSet, enableDisable);
        } catch (SQLException e) {
            throw new RuntimeException("Error while disabling constraints", e);
        } catch (StatementHandlerException e) {
            throw new RuntimeException("Error while disabling constraints", e);
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
