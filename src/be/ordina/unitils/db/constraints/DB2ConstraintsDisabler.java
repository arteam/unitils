/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.constraints;

import be.ordina.unitils.db.handler.StatementHandler;
import be.ordina.unitils.db.handler.StatementHandlerException;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TODO test me
 * TODO javadoc me
 * @author BaVe
 *
 */
public class DB2ConstraintsDisabler implements ConstraintsDisabler {

    /* CONSTRAINT_TYPE
      * P --> primary key
      * R --> foreign key
      * C --> check constraint (NOT_NULL)
      * V --> view constraint (???)
      */
    private static final String DISABLE_CONSTRAINTS_SQL = "select TABNAME, CONSTNAME from SYSCAT.TABCONST";

    /**
     * The DataSource
     */
    private DataSource dataSource;

    /**
     * The StatementHandler
     */
    private StatementHandler statementHandler;

    /**
     * @see ConstraintsDisabler#init(javax.sql.DataSource, be.ordina.unitils.db.handler.StatementHandler)
     */
    public void init(DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;
    }

    /**
     * @see be.ordina.unitils.db.constraints.ConstraintsDisabler#enableConstraints()
     */
    public void enableConstraints() {
        generateConstraintsScript("enable");
    }

    /**
     * @see be.ordina.unitils.db.constraints.ConstraintsDisabler#disableConstraints()
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
            buf.append(";\n");
            statementHandler.handle(buf.toString());
        }
    }

}
