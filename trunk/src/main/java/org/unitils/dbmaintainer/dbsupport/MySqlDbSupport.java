/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.dbsupport;

import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for a MySql database
 *
 * @author Frederick Beernaert
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MySqlDbSupport extends DbSupport {


    /**
     * Creates support for MySql databases.
     */
    public MySqlDbSupport() {
        super("mysql");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '" + schemaName + "' and TABLE_TYPE = 'BASE TABLE'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up table names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '" + schemaName + "' and TABLE_TYPE = 'VIEW'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TABLE_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up view names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        Connection conn = null;
        ResultSet rset = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rset = st.executeQuery("select TRIGGER_NAME from INFORMATION_SCHEMA.TRIGGERS where TRIGGER_SCHEMA = '" + schemaName + "'");
            Set<String> names = new HashSet<String>();
            while (rset.next()) {
                names.add(rset.getString("TRIGGER_NAME"));
            }
            return names;

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking trigger names", e);
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Identity columns are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    @Override
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        try {
            statementHandler.handle("alter table " + qualified(tableName) + " AUTO_INCREMENT = " + identityValue);

        } catch (StatementHandlerException e) {
            throw new UnitilsException(e);
        }
    }


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection The database connection, not null
     */
    @Override
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");

        } catch (SQLException e) {
            throw new UnitilsException("Error while disabling constraints", e);
        } finally {
            DbUtils.closeQuietly(statement);
        }
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    @Override
    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        String type = getColumnType(tableName, columnName);
        statementHandler.handle("alter table " + qualified(tableName) + " change column " + columnName + " " + columnName + " " + type + " NULL ");
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + qualified(tableName) + " disable constraint " + constraintName);
    }


    /**
     * todo is this correct?
     * <p/>
     * This method is overwritten, since Mysql doesn't support quoted database object names
     *
     * @return false
     */
    @Override
    public boolean supportsQuotedDatabaseObjectNames() {
        return false;
    }


    /**
     * todo is this correct?
     * <p/>
     * This method is overwritten, since Mysql doesn't support schema qualification
     *
     * @return false
     */
    @Override
    public boolean supportsSchemaQualification() {
        return false;
    }


    /**
     * Gets the type of the column with the given name in the given table. An exception is thrown if
     * the column could not be found
     *
     * @param tableName  The table, not null
     * @param columnName The column, not null
     * @return The type, not null
     */
    protected String getColumnType(String tableName, String columnName) {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select COLUMN_TYPE from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA " + " = '" + schemaName + "'" +
                    " and TABLE_NAME = '" + tableName + "' and COLUMN_NAME = '" + columnName + "'");

            if (resultSet.next()) {
                return resultSet.getString("COLUMN_TYPE");
            }
            throw new UnitilsException("Could not get type of column " + columnName + " in table " + tableName + ": column not found.");

        } catch (SQLException e) {
            throw new UnitilsException("Error while getting type of column " + columnName + " in table " + tableName, e);
        } finally {
            DbUtils.closeQuietly(connection, statement, resultSet);
        }
    }

}