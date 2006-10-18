/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.constraints;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * TODO test me
 * TODO javadoc me
 *
 * Implementation of {@link ConstraintsDisabler} for an DB2 database.
 *
 * @author BaVe
 */
public class DB2ConstraintsDisabler implements ConstraintsDisabler {

    /* CONSTRAINT_TYPE
      * P --> primary key
      * R --> foreign key
      * C --> check constraint (NOT_NULL)
      * V --> view constraint (???)
      */
    private static final String SELECT_CONSTRAINTS_SQL = "select TABNAME, CONSTNAME from SYSCAT.TABCONST";

    /**
     * The TestDataSource
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
     * @see org.unitils.dbmaintainer.constraints.ConstraintsDisabler#disableConstraints()
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
