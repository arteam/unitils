/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.core.util.StoredIdentifierCase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import static org.unitils.core.util.StoredIdentifierCase.*;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import static org.unitils.util.PropertyUtils.getString;

/**
 * todo remove
 * <p/>
 * Helper class that implements a number of common operations on a database schema. Operations that can be implemented
 * using general JDBC or ANSI SQL constructs, are impelemented in this base abstract class. Operations that are DBMS
 * specific are abstract, and their implementation is left to DBMS specific subclasses.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Frederick Beernaert
 */
abstract public class DbSupport {

    /**
     * Property key for the default identifier casing (lower_case, upper_case, mixed_case, auto)
     */
    public static final String PROPKEY_STORED_IDENTIFIER_CASE = "database.storedIndentifierCase";

    /**
     * Property key for the default identifier quote string (empty value for not supported, auto)
     */
    public static final String PROPKEY_IDENTIFIER_QUOTE_STRING = "database.identifierQuoteString";


    /* The name of the DBMS implementation that is supported by this implementation */
    private String databaseDialect;

    /* The name of the database schema */
    private String schemaName;

    /* Gives access to the database */
    private SQLHandler sqlHandler;

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
     * @param sqlHandler    The sql handler, not null
     * @param schemaName    The name of the database schema
     */
    public void init(Properties configuration, SQLHandler sqlHandler, String schemaName) {
        this.sqlHandler = sqlHandler;

        String identifierQuoteStringProperty = getString(PROPKEY_IDENTIFIER_QUOTE_STRING + "." + getDatabaseDialect(), configuration);
        String storedIdentifierCaseValue = getString(PROPKEY_STORED_IDENTIFIER_CASE + "." + getDatabaseDialect(), configuration);

        this.identifierQuoteString = determineIdentifierQuoteString(identifierQuoteStringProperty);
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
     * Gets the sql handler.
     *
     * @return the data source, not null
     */
    public SQLHandler getSQLHandler() {
        return sqlHandler;
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

        if (identifierQuoteString != null && identifier.startsWith(identifierQuoteString) && identifier.endsWith(identifierQuoteString)) {
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
            return LOWER_CASE;
        } else if ("upper_case".equals(storedIdentifierCase)) {
            return UPPER_CASE;
        } else if ("mixed_case".equals(storedIdentifierCase)) {
            return MIXED_CASE;
        } else if (!"auto".equals(storedIdentifierCase)) {
            throw new UnitilsException("Unknown value " + storedIdentifierCase + " for property " + PROPKEY_STORED_IDENTIFIER_CASE + ". It should be one of lower_case, upper_case, mixed_case or auto.");
        }

        Connection connection = null;
        try {
            connection = getSQLHandler().getDataSource().getConnection();

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
     * @param identifierQuoteStringProperty The string to quote identifiers, 'none' if quoting is not supported, 'auto' for auto detection
     * @return The quote string, null if quoting is not supported
     */
    private String determineIdentifierQuoteString(String identifierQuoteStringProperty) {
        if ("none".equals(identifierQuoteStringProperty)) {
            return null;
        } else if (!"auto".equals(identifierQuoteStringProperty)) {
            return identifierQuoteStringProperty;
        }

        Connection connection = null;
        try {
            connection = getSQLHandler().getDataSource().getConnection();

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


}