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
import org.apache.commons.lang.StringUtils;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.SQLScriptParser;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.dbmaintainer.util.SQLCodeScriptParser;
import org.unitils.core.UnitilsException;

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

    /* Indicates whether database object names are stored in uppercase in system metadata tables */
    private Boolean storesUpperCaseIdentifiers;

    /* Indicates whether database object names are stored in lowercase in system metadata tables */
    private Boolean storesLowerCaseIdentifiers;

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
    public Set<String> getTableNames() {
        return getIdentifiers("TABLE");
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    public Set<String> getViewNames() {
        return getIdentifiers("VIEW");
    }


    /**
     * Retrieves the names of all the synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    abstract public Set<String> getSynonymNames();


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    abstract public Set<String> getSequenceNames();


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    abstract public Set<String> getTriggerNames();


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    abstract public Set<String> getTypeNames();


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    public void dropTable(String tableName) throws StatementHandlerException {
        String dropTableSQL = "drop table " + qualified(tableName) + " cascade";
        statementHandler.handle(dropTableSQL);
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) throws StatementHandlerException {
        String dropTableSQL = "drop view " + qualified(viewName) + " cascade";
        statementHandler.handle(dropTableSQL);
    }

    /**
     * Removes the synonym with the given name from the database
     * Note: the synonym name is surrounded with quotes, making it case-sensitive.
     *
     * @param synonymName The synonym to drop (case-sensitive), not null
     */
    public void dropSynonym(String synonymName) throws StatementHandlerException {
        String dropSynonymSQL = "drop synonym " + qualified(synonymName);
        statementHandler.handle(dropSynonymSQL);
    }


    /**
     * Drops the sequence with the given name from the database
     * Note: the sequence name is surrounded with quotes, making it case-sensitive.
     *
     * @param sequenceName The sequence to drop (case-sensitive), not null
     */
    public void dropSequence(String sequenceName) throws StatementHandlerException {
        if (supportsSequences()) {
            statementHandler.handle("drop sequence " + qualified(sequenceName));
        } else {
            throw new UnsupportedOperationException("Triggers are not supported for " + getDbmsName());
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
            statementHandler.handle("drop trigger " + qualified(triggerName));
        } else {
            throw new UnsupportedOperationException("Triggers are not supported for " + getDbmsName());
        }
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    abstract public void dropType(String typeName) throws StatementHandlerException;


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    abstract public long getCurrentValueOfSequence(String sequenceName);


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    abstract public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException;


    /**
     * Indicates whether the underlying DBMS supports synonyms
     *
     * @return True if synonyms are supported, false otherwise
     */
    abstract public boolean supportsSynonyms();


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
    public Set<String> getPrimaryKeyColumnNames(String tableName) {
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
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up primary key column names", e);
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
    public Set<String> getNotNullColummnNames(String tableName) {
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

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up not null column names", e);
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
    abstract public Set<String> getTableConstraintNames(String tableName);


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
     * This parser also takes quotedOrEmpty literals and double quotedOrEmpty text into account when parsing the statements and treating
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
     * Parses the given string containing database code into a list of individual souce code statement. The way in which
     * individual pieces of code are recognized depends fully on the implementation. The resulting strings must be
     * individually applyable to the database.
     *
     * @param script
     * @return A <code>List</code> containing individual pieces of database code, each individually applyable to the
     * database.
     */
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
    protected Set<String> getIdentifiers(String type) {
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

        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up identifiers", e);
        } finally {
            closeQuietly(connection, null, resultSet);
        }
    }


    /**
     * @return The name of the DBMS implementation that is supported by this implementation of {@link DbSupport}
     */
    public abstract String getDbmsName();

    /**
     * Qualifies the given database object name with the name of the database schema. Quotes are put around both
     * schemaname and object name. If the schemaName is not supplied, the database object is returned surrounded with
     * quotes. If the DBMS doesn't support quoted database object names, no quotes are put around neither schema name
     * nor database object name.
     *
     * @param databaseObjectName The database object name to be qualified
     * @return The qualified database object name
     */
    public String qualified(String databaseObjectName) {
        return ((supportsSchemaQualification() && StringUtils.isNotEmpty(schemaName)) ? quoted(schemaName) : "") + "." + quoted(databaseObjectName);
    }

    /**
     * Put quotes around the given databaseObjectName, if the underlying DBMS supports quoted database object names.
     * If not, the databaseObjectName is returned unchanged.
     *
     * @param databaseObjectName
     * @return Quoted version of the given databaseObjectName, if supported by the underlying DBMS
     */
    public String quoted(String databaseObjectName) {
        if (supportsQuotedDatabaseObjectNames()) {
            return "\"" + databaseObjectName + "\"";
        } else {
            return databaseObjectName;
        }
    }

    /**
     * Indicates whether the underlying DBMS supports database object names that are qualified by the schema name.
     *
     * @return true by default. If the underlying DBMS doesn't support qualified database object names,
     * this method should be overwritten
     */
    public boolean supportsSchemaQualification() {
        return true;
    }

    /**
     * Indicates whether the underlying DBMS supports quoted database object names.
     *
     * @return true by default. If the underlying DBMS doesn't support quoted database object names,
     * this method should be overwritten
     */
    public boolean supportsQuotedDatabaseObjectNames() {
        return true;
    }

    /**
     * Converts the given identifier to uppercase/lowercase depending on the DBMS. If a value is surrounded with double
     * quotes (") and the DBMS supports quoted database object names, the case is left untouched and the double quotes
     * are stripped. These values are treated as case sensitive names.
     *
     * @param identifier The identifier, not null
     * @return The name converted to correct case if needed, not null
     */
    public String toCorrectCaseIdentifier(String identifier) {
        identifier = identifier.trim();
        if (identifier.startsWith("\"") && identifier.endsWith("\"")) {
            identifier = identifier.substring(1, identifier.length() - 1);
            if (supportsQuotedDatabaseObjectNames()) {
                return identifier;
            }
        }
        if (isStoresUpperCaseIdentifiers()) {
            return identifier.toUpperCase();
        } else if (isStoresLowerCaseIdentifiers()) {
            return identifier.toLowerCase();
        } else {
            return identifier;
        }
    }

    /**
     * @return True if database object names are stored in uppercase in database metadata tables, false otherwise.
     */
    public boolean isStoresUpperCaseIdentifiers() {
        if (storesUpperCaseIdentifiers == null) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                storesUpperCaseIdentifiers = connection.getMetaData().storesUpperCaseIdentifiers();
            } catch (SQLException e) {
                throw new UnitilsException("Unable to convert identifiers to correct case.", e);
            } finally {
                closeQuietly(connection, null, null);
            }
        }
        return storesUpperCaseIdentifiers;
    }

    /**
     * @return True if database object names are stored in uppercase in database metadata tables, false otherwise.
     */
    public boolean isStoresLowerCaseIdentifiers() {
        if (storesLowerCaseIdentifiers == null) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                storesLowerCaseIdentifiers = connection.getMetaData().storesLowerCaseIdentifiers();
            } catch (SQLException e) {
                throw new UnitilsException("Unable to convert identifiers to correct case.", e);
            } finally {
                closeQuietly(connection, null, null);
            }
        }
        return storesLowerCaseIdentifiers;
    }


}