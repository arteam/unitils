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
package org.unitils.core.dbsupport;

import org.unitils.core.UnitilsException;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import org.unitils.core.util.StoredIdentifierCase;
import static org.unitils.core.util.StoredIdentifierCase.*;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
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
     * Property key for the default identifier casing (lower_case, upper_case, mixed_case, auto)
     */
    public static final String PROPKEY_STORED_IDENTIFIER_CASE = "database.storedIndentifierCase";

    /**
     * Property key for the default identifier quote string (empty value for not supported, auto)
     */
    public static final String PROPKEY_IDENTIFIER_QUOTE_STRING = "database.indentifierQuoteString";


    /* The name of the DBMS implementation that is supported by this implementation */
    private String databaseDialect;

    /* The name of the database schema */
    private String schemaName;

    /* Gives access to the database */
    private DataSource dataSource;

    /* Indicates whether database identifiers are stored in lowercase, uppercase or mixed case */
    private StoredIdentifierCase storedIdentifierCase;

    /* The string that is used to quote identifiers to make them case sensitive, e.g. ", null means quoting not supported*/
    private String identifierQuoteString;


    /**
     * Creates a new, unconfigured instance. To have a instance that can be used, the {@link #init} method must be
     * called first.
     *
     * @param databaseDialect The name of the DBMS implementation that is supported by this implementation, not null
     */
    protected DbSupport(String databaseDialect) {
        this.databaseDialect = databaseDialect;
    }


    /**
     * Initializes this DbSupport object with the given schemaName and dataSource.
     * If the storedIdentifierCase or identifierQuoteString is set to null, the metadata of the connection will be used to determine the
     * correct value.
     *
     * @param configuration The config, not null
     * @param dataSource    The data source, not null
     * @param schemaName    The name of the database schema
     */
    public void init(Properties configuration, DataSource dataSource, String schemaName) {
        this.dataSource = dataSource;

        String identifierQuoteString = PropertyUtils.getString(PROPKEY_IDENTIFIER_QUOTE_STRING + "." + getDatabaseDialect(), configuration);
        String storedIdentifierCaseValue = PropertyUtils.getString(PROPKEY_STORED_IDENTIFIER_CASE + "." + getDatabaseDialect(), configuration);

        this.identifierQuoteString = determineIdentifierQuoteString(identifierQuoteString);
        this.storedIdentifierCase = determineStoredIdentifierCase(storedIdentifierCaseValue);

        this.schemaName = toCorrectCaseIdentifier(schemaName);
    }


    /**
     * Gets the database dialect.
     *
     * @return the supported dialect, not null
     */
    public String getDatabaseDialect() {
        return databaseDialect;
    }


    /**
     * Gets the schema name.
     *
     * @return the schema name, not null
     */
    public String getSchemaName() {
        return schemaName;
    }


    /**
     * Gets the identifier quote string.
     *
     * @return the quote string, null if not supported
     */
    public String getIdentifierQuoteString() {
        return identifierQuoteString;
    }


    /**
     * Gets the stored identifier case.
     *
     * @return the case, not null
     */
    public StoredIdentifierCase getStoredIdentifierCase() {
        return storedIdentifierCase;
    }


    /**
     * Gets the data source.
     *
     * @return the data source, not null
     */
    public DataSource getDataSource() {
        return dataSource;
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    public abstract Set<String> getTableNames();


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    public abstract Set<String> getViewNames();


    /**
     * Retrieves the names of all the synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    public Set<String> getSynonymNames() {
        throw new UnsupportedOperationException("Synonyms not supported.");
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    public Set<String> getSequenceNames() {
        throw new UnsupportedOperationException("Sequences not supported.");
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    public Set<String> getTriggerNames() {
        throw new UnsupportedOperationException("Triggers not supported.");
    }


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    public Set<String> getTypeNames() {
        throw new UnsupportedOperationException("Types not supported.");
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    public void dropTable(String tableName) {
        executeUpdate("drop table " + qualified(tableName) + " cascade", dataSource);
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    public void dropView(String viewName) {
        executeUpdate("drop view " + qualified(viewName) + " cascade", dataSource);
    }

    /**
     * Removes the synonym with the given name from the database
     * Note: the synonym name is surrounded with quotes, making it case-sensitive.
     *
     * @param synonymName The synonym to drop (case-sensitive), not null
     */
    public void dropSynonym(String synonymName) {
        executeUpdate("drop synonym " + qualified(synonymName), dataSource);
    }


    /**
     * Drops the sequence with the given name from the database
     * Note: the sequence name is surrounded with quotes, making it case-sensitive.
     *
     * @param sequenceName The sequence to drop (case-sensitive), not null
     */
    public void dropSequence(String sequenceName) {
        executeUpdate("drop sequence " + qualified(sequenceName), dataSource);
    }


    /**
     * Drops the trigger with the given name from the database
     * Note: the trigger name is surrounded with quotes, making it case-sensitive.
     *
     * @param triggerName The trigger to drop (case-sensitive), not null
     */
    public void dropTrigger(String triggerName) {
        executeUpdate("drop trigger " + qualified(triggerName), dataSource);
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    public void dropType(String typeName) {
        throw new UnsupportedOperationException("Types are not supported for " + getDatabaseDialect());
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    public long getCurrentValueOfSequence(String sequenceName) {
        throw new UnsupportedOperationException("Sequences not supported for " + getDatabaseDialect());
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        throw new UnsupportedOperationException("Sequences not supported for " + getDatabaseDialect());
    }


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
     * Gets the names of all columns of the given table.
     *
     * @param tableName The table, not null
     * @return The names of the columns of the table with the given name
     */
    public Set<String> getColumnNames(String tableName) {
        Connection conn = null;
        Statement st = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rset = metaData.getColumns(null, schemaName, tableName, null);
            Set<String> columnNames = new HashSet<String>();
            while (rset.next()) {
                columnNames.add(rset.getString("COLUMN_NAME"));
            }
            return columnNames;
        } catch (SQLException e) {
            throw new UnitilsException("Error while looking up column names", e);
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
    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        throw new UnsupportedOperationException("Identity columns not supported for " + getDatabaseDialect());
    }


    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     *
     * @param connection The database connection, not null
     */
    public void disableForeignKeyConstraintsCheckingOnConnection(Connection connection) {
        throw new UnsupportedOperationException("Disabling foreign key contstraints on connection not supported for " + getDatabaseDialect());
    }


    /**
     * Removes the not-null constraint on the specified column and table
     *
     * @param tableName  The table with the column, not null
     * @param columnName The column to remove constraints from, not null
     */
    public void removeNotNullConstraint(String tableName, String columnName) {
        throw new UnsupportedOperationException("Remove not null constraints not supported for " + getDatabaseDialect());
    }


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
    public Set<String> getTableConstraintNames(String tableName) {
        throw new UnsupportedOperationException("Retrieval of table constraints not supported for " + getDatabaseDialect());
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    public void disableConstraint(String tableName, String constraintName) {
        throw new UnsupportedOperationException("Disabling of constraints not supported for " + getDatabaseDialect());
    }


    /**
     * Gets the column type suitable to store values of the Java <code>java.lang.Long</code> type.
     *
     * @return The column type
     */
    public String getLongDataType() {
        return "BIGINT";
    }


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
        return quoted(schemaName) + "." + quoted(databaseObjectName);
    }


    /**
     * Put quotes around the given databaseObjectName, if the underlying DBMS supports quoted database object names.
     * If not, the databaseObjectName is returned unchanged.
     *
     * @param databaseObjectName The name, not null
     * @return Quoted version of the given databaseObjectName, if supported by the underlying DBMS
     */
    public String quoted(String databaseObjectName) {
        if (identifierQuoteString == null) {
            return databaseObjectName;
        }
        return identifierQuoteString + databaseObjectName + identifierQuoteString;
    }


    /**
     * Converts the given identifier to uppercase/lowercase depending on the DBMS. If a value is surrounded with double
     * quotes (") and the DBMS supports quoted database object names, the case is left untouched and the double quotes
     * are stripped. These values are treated as case sensitive names.
     * <p/>
     * Identifiers can be prefixed with schema names. These schema names will be converted in the same way as described
     * above. Quoting the schema name will make it case sensitive.
     * Examples:
     * <p/>
     * mySchema.myTable -> MYSCHEMA.MYTABLE
     * "mySchema".myTable -> mySchema.MYTABLE
     * "mySchema"."myTable" -> mySchema.myTable
     *
     * @param identifier The identifier, not null
     * @return The name converted to correct case if needed, not null
     */
    public String toCorrectCaseIdentifier(String identifier) {
        identifier = identifier.trim();

        int index = identifier.indexOf('.');
        if (index != -1) {
            String schemaNamePart = identifier.substring(0, index);
            String identifierPart = identifier.substring(index + 1);
            return toCorrectCaseIdentifier(schemaNamePart) + "." + toCorrectCaseIdentifier(identifierPart);
        }

        if (identifier.startsWith(identifierQuoteString) && identifier.endsWith(identifierQuoteString)) {
            return identifier.substring(1, identifier.length() - 1);
        }
        if (storedIdentifierCase == UPPER_CASE) {
            return identifier.toUpperCase();
        } else if (storedIdentifierCase == LOWER_CASE) {
            return identifier.toLowerCase();
        } else {
            return identifier;
        }
    }


    /**
     * Determines the case the database uses to store non-quoted identifiers. This will use the connections
     * database metadata to determine the correct case.
     *
     * @param storedIdentifierCase The stored case: possible values 'lower_case', 'upper_case', 'mixed_case' and 'auto'
     * @return The stored case, not null
     */
    private StoredIdentifierCase determineStoredIdentifierCase(String storedIdentifierCase) {
        if ("lower_case".equals(storedIdentifierCase)) {
            return StoredIdentifierCase.LOWER_CASE;
        } else if ("upper_case".equals(storedIdentifierCase)) {
            return StoredIdentifierCase.UPPER_CASE;
        } else if ("mixed_case".equals(storedIdentifierCase)) {
            return StoredIdentifierCase.MIXED_CASE;
        } else if (!"auto".equals(storedIdentifierCase)) {
            throw new UnitilsException("Unknown value " + storedIdentifierCase + " for property " + PROPKEY_STORED_IDENTIFIER_CASE + ". It should be one of lower_case, upper_case, mixed_case or auto.");
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData.storesUpperCaseIdentifiers()) {
                return UPPER_CASE;
            } else if (databaseMetaData.storesLowerCaseIdentifiers()) {
                return LOWER_CASE;
            } else {
                return MIXED_CASE;
            }
        } catch (SQLException e) {
            throw new UnitilsException("Unable to determine stored identifier case.", e);
        } finally {
            closeQuietly(connection, null, null);
        }
    }


    /**
     * Determines the string used to quote identifiers to make them case-sensitive. This will use the connections
     * database metadata to determine the quote string.
     *
     * @param identifierQuoteString The string to quote identifiers, 'none' if quoting is not supported, 'auto' for auto detection
     * @return The quote string, null if quoting is not supported
     */
    private String determineIdentifierQuoteString(String identifierQuoteString) {
        if ("none".equals(identifierQuoteString)) {
            return null;
        } else if (!"auto".equals(identifierQuoteString)) {
            return identifierQuoteString;
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String quoteString = databaseMetaData.getIdentifierQuoteString();
            if (quoteString == null || "".equals(quoteString.trim())) {
                return null;
            }
            return quoteString;

        } catch (SQLException e) {
            throw new UnitilsException("Unable to determine identifier quote string.", e);
        } finally {
            closeQuietly(connection, null, null);
        }
    }


    /**
     * Indicates whether the underlying DBMS supports synonyms
     *
     * @return True if synonyms are supported, false otherwise
     */
    public boolean supportsSynonyms() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports sequences
     *
     * @return True if sequences are supported, false otherwise
     */
    public boolean supportsSequences() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports triggers
     *
     * @return True if triggers are supported, false otherwise
     */
    public boolean supportsTriggers() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports database types
     *
     * @return True if types are supported, false otherwise
     */
    public boolean supportsTypes() {
        return false;
    }

    /**
     * Indicates whether the underlying DBMS supports identity columns
     *
     * @return True if identity is supported, false otherwise
     */
    public boolean supportsIdentityColumns() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports getting table constraint names.
     *
     * @return True if getting constraint names is supported, false otherwise
     */
    public boolean supportsGetTableConstraintNames() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports removing not null constraint.
     *
     * @return True if removing not null constraint is supported, false otherwise
     */
    public boolean supportsRemoveNotNullConstraint() {
        return false;
    }


    /**
     * Indicates whether the underlying DBMS supports disabling FK constraints on the connection.
     *
     * @return True if disabling FK constraints on the connection is supported, false otherwise
     */
    public boolean supportsDisableForeignKeyConstraintsCheckingOnConnection() {
        return false;
    }


}