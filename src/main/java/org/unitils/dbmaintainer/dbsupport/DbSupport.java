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

import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.SQLScriptParser;
import org.unitils.dbmaintainer.util.SQLCodeScriptParser;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;

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
     * @param dataSource       The data source, not null
     * @param schemaName       The database schema, not null
     * @param statementHandler The statement executor, not null
     */
    public void init(DataSource dataSource, String schemaName, StatementHandler statementHandler) {
        this.schemaName = schemaName;
        this.statementHandler = statementHandler;
        this.dataSource = dataSource;
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    public Set<String> getTableNames() throws SQLException {
        return getIdentifiers("TABLE");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    public Set<String> getViewNames() throws SQLException {
        return getIdentifiers("VIEW");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    abstract public Set<String> getSequenceNames() throws SQLException;


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    abstract public Set<String> getTriggerNames() throws SQLException;

    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    abstract public Set<String> getTypeNames() throws SQLException;


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table \"" + tableName + "\" cascade";
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) throws StatementHandlerException {
        String dropTableSQL = "drop view \"" + viewName + "\" cascade";
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Drops the sequence with the given name from the database
     * Note: the sequence name is surrounded with quotes, making it case-sensitive.
     *
     * @param sequenceName The sequence to drop (case-sensitive), not null
     */
    public void dropSequence(String sequenceName) throws StatementHandlerException {
        if (supportsSequences()) {
            statementHandler.handle("drop sequence \"" + sequenceName + "\"");
        }
    }


    /**
     * Drops the trigger with the given name from the database
     * Note: the trigger name is surrounded with quotes, making it case-sensitive.
     *
     * @param triggerName The trigger to drop (case-sensitive), not null
     */
    public void dropTrigger(String triggerName) throws StatementHandlerException {
        if (supportsTriggers()) {
            statementHandler.handle("drop trigger \"" + triggerName + "\"");
        }
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    public void dropType(String typeName) throws StatementHandlerException {
        if (supportsTypes()) {
            statementHandler.handle("drop type \"" + typeName + "\"");
        }
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    abstract public long getCurrentValueOfSequence(String sequenceName) throws SQLException;


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    abstract public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException, SQLException;


    /**
     * Indicates whether the underlying DBMS supports sequences
     *
     * @return True if sequences are supported, false otherwise
     */
    abstract public boolean supportsSequences();


    /**
     * Indicates whether the underlying DBMS supports triggers
     *
     * @return True if triggers are supported, false otherwise
     */
    abstract public boolean supportsTriggers();


    /**
     * Indicates whether the underlying DBMS supports identity columns
     *
     * @return True if identity is supported, false otherwise
     */
    abstract public boolean supportsIdentityColumns();

    /**
     * Indicates whether the underlying DBMS supports database types
     *
     * @return True if types are supported, false otherwise
     */
    abstract public boolean supportsTypes();

    /**
     * Gets the names of all primary columns of the given table.
     *
     * @param tableName The table, not null
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
            closeQuietly(conn, st, rset);
        }
    }


    /**
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName            The table with the identity column, not null
     * @param primaryKeyColumnName The column, not null
     * @param identityValue        The new value
     */
    abstract public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue);


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection The database connection, not null
     */
    abstract public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection);


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    abstract public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException;


    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName The table, not null
     * @return The set of column names, not null
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
            closeQuietly(conn, null, rs);
        }
    }


    /**
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    abstract public Set<String> getTableConstraintNames(String tableName) throws SQLException;


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    abstract public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException;


    /**
     * Gets the column type suitable to store values of the Java <code>java.lang.Long</code> type.
     *
     * @return The column type
     */
    public String getLongDataType() {
        return "BIGINT";
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

    public List<String> parseCodeStatements(String script) {

        SQLCodeScriptParser sqlCodeScriptParser = new SQLCodeScriptParser();
        return sqlCodeScriptParser.parseStatements(script);
    }


    /**
     * Returns the the idendtifiers for the given type (table names, view names...)
     *
     * @param type The type of identifier: TABLE, GLOBAL TEMPORARY, LOCAL TEMPORARY, ALIAS or SYNONYM
     * @return The names, not null
     */
    protected Set<String> getIdentifiers(String type) throws SQLException {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            Set<String> identifiers = new HashSet<String>();
            DatabaseMetaData databaseMetadata = connection.getMetaData();
            resultSet = databaseMetadata.getTables(null, schemaName, null, new String[]{type});
            while (resultSet.next()) {
                String identifier = resultSet.getString("TABLE_NAME");
                identifiers.add(identifier);
            }
            return identifiers;

        } finally {
            closeQuietly(connection, null, resultSet);
        }
    }

}