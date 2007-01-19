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

import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.dbmaintainer.script.impl.SQLScriptParser;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class that implements a number of common operations on a database schema. Operations that can be implemented
 * using general JDBC or ANSI SQL constructs, are impelemented in this base abstract class. Operations that are DBMS
 * specific are abstract, and their implementation is left to DBMS specific subclasses.
 *
 * @author Filip Neven
 * @author Frederick Beernaert
 * @author Tim Ducheyne
 */
abstract public class DbSupport {

    /**
     * The name of the database schema
     */
    protected String schemaName;

    /**
     * StatementHandler by which all updates to the database are handled
     */
    protected StatementHandler statementHandler;

    /**
     * Gives access to the database
     */
    protected DataSource dataSource;


    /**
     * Creates a new, unconfigured instance. To have a instance that can be used, the {@link #init} method must be
     * called first.
     */
    protected DbSupport() {
    }


    /**
     * Initializes this DbSupport object with the given schemaName, statementHandler and dataSource
     *
     * @param dataSource       the data source, not null
     * @param schemaName       the database schema, not null
     * @param statementHandler the statement executor, not null
     */
    public void init(DataSource dataSource, String schemaName, StatementHandler statementHandler) {
        this.schemaName = schemaName;
        this.statementHandler = statementHandler;
        this.dataSource = dataSource;
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return the names of all tables in the database (in uppercase)
     */
    public Set<String> getTableNames() throws SQLException {
        return getTableNames(new String[]{"TABLE"});
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return the names of all views in the database (in uppercase)
     */
    public Set<String> getViewNames() throws SQLException {
        return getTableNames(new String[]{"VIEW"});
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database (in uppercase)
     */
    abstract public Set<String> getSequenceNames() throws SQLException;


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database (in uppercase)
     */
    abstract public Set<String> getTriggerNames() throws SQLException;


    /**
     * Checks whether the table with the given name exists
     *
     * @param tableName the name of the table
     * @return true if the table with the given name exists, false otherwise
     */
    public boolean tableExists(String tableName) throws SQLException {
        if (tableName == null) {
            return false;
        }
        return getTableNames().contains(tableName.toUpperCase());
    }


    /**
     * Checks whether the table with the given name exists
     *
     * @param viewName the name of the view
     * @return true if the view with the given name exists, false otherwise
     */
    public boolean viewExists(String viewName) throws SQLException {
        if (viewName == null) {
            return false;
        }
        return getViewNames().contains(viewName.toUpperCase());
    }


    /**
     * Checks whether the trigger with the given name exists
     *
     * @param triggerName the name of the trigger
     * @return true if the trigger with the given name exists, false otherwise
     */
    public boolean triggerExists(String triggerName) throws SQLException {
        if (triggerName == null) {
            return false;
        }
        return getTriggerNames().contains(triggerName.toUpperCase());
    }


    /**
     * Checks whether the sequence with the given name exists
     *
     * @param sequenceName the name of the sequence
     * @return true if the sequence with the given name exists, false otherwise
     */
    public boolean sequenceExists(String sequenceName) throws SQLException {
        if (sequenceName == null || !supportsSequences()) {
            return false;
        }
        return getSequenceNames().contains(sequenceName.toUpperCase());
    }


    /**
     * Removes the view with the given name from the database
     *
     * @param viewName the view to drop, not null
     */
    public void dropView(String viewName) throws StatementHandlerException {
        String dropTableSQL = "drop view " + viewName;
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Removes the table with the given name from the database
     *
     * @param tableName the table to drop, not null
     */
    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + tableName;
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Drops the sequence with the given name from the database
     *
     * @param sequenceName the sequence to drop, not null
     */
    public void dropSequence(String sequenceName) throws StatementHandlerException {
        if (supportsSequences()) {
            statementHandler.handle("drop sequence " + sequenceName);
        }
    }


    /**
     * Drops the trigger with the given name from the database
     *
     * @param triggerName the trigger to drop, not null
     */
    public void dropTrigger(String triggerName) throws StatementHandlerException {
        if (supportsTriggers()) {
            statementHandler.handle("drop trigger " + triggerName);
        }
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName the sequence, not null
     * @return the value of the sequence with the given name
     */
    abstract public long getCurrentValueOfSequence(String sequenceName) throws SQLException;


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     the sequence, not null
     * @param newSequenceValue the value to set
     */
    abstract public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException, SQLException;


    /**
     * Indicates whether the underlying DBMS supports sequences
     *
     * @return true if sequences are supported, false otherwise
     */
    abstract public boolean supportsSequences();


    /**
     * Indicates whether the underlying DBMS supports triggers
     *
     * @return true if triggers are supported, false otherwise
     */
    abstract public boolean supportsTriggers();


    /**
     * Indicates whether the underlying DBMS supports identity columns
     *
     * @return true if identity is supported, false otherwise
     */
    abstract public boolean supportsIdentityColumns();


    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName the table, not null
     * @return The names of the primary key columns of the table with the given name
     */
    public Set<String> getPrimaryKeyColumnNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rset = metaData.getPrimaryKeys(null, schemaName, tableName);
            Set<String> primaryKeyColumnNames = new HashSet<String>();
            while (rset.next()) {
                primaryKeyColumnNames.add(rset.getString("COLUMN_NAME"));
            }
            return primaryKeyColumnNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName            the table with the identity column, not null
     * @param primaryKeyColumnName the column, not null
     * @param identityValue        the new value
     */
    abstract public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue);


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection the database connection, not null
     */
    abstract public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection);


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  the table with the column, not null
     * @param columnName the column to remove constraints from, not null
     */
    abstract public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException;


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName the table, not null
     * @return the set of column names, not null
     */
    public Set<String> getNotNullColummnNames(String tableName) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, schemaName, tableName, null);
            Set<String> notNullColumnNames = new HashSet<String>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                // Check if the column is not the primary key column

                boolean nullable = rs.getBoolean("NULLABLE");
                if (!nullable) {
                    notNullColumnNames.add(columnName);
                }
            }
            return notNullColumnNames;

        } finally {
            DbUtils.closeQuietly(conn, null, rs);
        }
    }


    /**
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName the table, not null
     * @return the set of constraint names, not null
     */
    abstract public Set<String> getTableConstraintNames(String tableName) throws SQLException;


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      the table with the constraint, not null
     * @param constraintName the constraint, not null
     */
    abstract public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException;


    /**
     * @return Column type suitable to store values of the Java <code>java.lang.Long</code> type
     */
    abstract public String getLongDataType();


    /**
     * @param tableName the table, not null
     * @return The number of records in the table with the given name
     */
    public long getRecordCount(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select count(*) from " + tableName);
            rs.next();
            return rs.getLong(1);

        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }


    /**
     * Parses all statements out of the given sql script.
     * <p/>
     * All statements should be separated with a semicolon (;). The last statement will be
     * added even if it does not end with a semicolon. The semicolons will not be included in the returned statements.
     * <p/>
     * All comments in-line (--comment) and block (/ * comment * /) are removed from the statements.
     * This parser also takes quoted literals and double quoted text into account when parsing the statements and treating
     * the comments.
     * <p/>
     * New line charactars in the statements will be replaced by spaces.
     *
     * @param script The sql script, not null
     * @return The statements, not null
     */
    public List<String> parseStatements(String script) {
        SQLScriptParser sqlScriptParser = new SQLScriptParser();
        return sqlScriptParser.parseStatements(script);
    }


    /**
     * Returns the names of the tables with the given types
     *
     * @param types The type of the tables as an array of Strings, e.g. "TABLE" or "VIEW"
     * @return the names, not null
     */
    protected Set<String> getTableNames(String[] types) throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            Set<String> tableNames = new HashSet<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName, null, types);
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;

        } finally {
            DbUtils.closeQuietly(conn, null, rset);
        }
    }

}