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
package org.unitils.core;

import org.apache.commons.dbcp.BasicDataSource;
import static org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.util.StoredIdentifierCase;
import org.unitils.database.datasource.UnitilsDataSource;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.PropertyUtils.containsProperty;
import static org.unitils.util.PropertyUtils.getString;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Factory that initializes and loads all unitils datasources. Each datasource can have multiple
 * schemas. If there is more than one datasource, the datasources must be identified by a logical
 * name.
 */
public class UnitilsDataSourceFactory {

    public static final String PROPERTY_DATABASE_NAMES = "databases.names";

    private static final String PROPERTY_DATABASE_START = "database";

    private static final String PROPERTY_DRIVERCLASSNAME_END = "driverClassName";

    private static final String PROPERTY_URL_END = "url";

    private static final String PROPERTY_USERNAME_END = "userName";

    private static final String PROPERTY_PASSWORD_END = "password";

    private static final String PROPERTY_INCLUDED_END = "included";

    /**
     * Property key of the SQL dialect of the underlying DBMS implementation
     */
    public static final String PROPERTY_DIALECT_END = "dialect";

    /**
     * Property key for the database schema names
     */
    public static final String PROPERTY_SCHEMANAMES_END = "schemaNames";

    private static final Log logger = LogFactory.getLog(UnitilsDataSourceFactory.class);
    
    /** Property key of the SQL dialect of the underlying DBMS implementation */
    public static final String PROPKEY_DATABASE_DIALECT = "database.dialect";

    /** Property key for the database schema names */
    public static final String PROPKEY_DATABASE_SCHEMA_NAMES = "database.schemaNames";

    /** Indicates the default identifier case (lower_case, upper_case, mixed_case, auto) */
    public static final String PROPERTY_STORED_IDENTIFIER_CASE = "database.storedIndentifierCase";

    /** Indicates the default identifier quote string (empty value for not supported, auto) */
    public static final String PROPERTY_IDENTIFIER_QUOTE_STRING = "database.identifierQuoteString";

    private Properties configuration;

    private UnitilsDataSource defaultUnitilsDataSource;

    /* Cache of created db support instance, per schema name */
    private Map<String, UnitilsDataSource> nameUnitilsDataSourceMap = new HashMap<String, UnitilsDataSource>();


    public UnitilsDataSourceFactory(Properties configuration) {
        this.configuration = configuration;
        initUnitilsDataSources();
    }

    /**
     * Returns the {@link UnitilsDataSource} as configured in the given <code>Configuration</code> for the
     * default database. If there's more than one configured database, the default database is the first database listed
     * in the property {@link #PROPERTY_DATABASE_NAMES}.
     *
     * @return The {@link UnitilsDataSource}, not null
     */
    public UnitilsDataSource getDefaultUnitilsDataSource() {
        return defaultUnitilsDataSource;
    }


    public Map<String, UnitilsDataSource> getNameUnitilsDataSourceMap() {
        return nameUnitilsDataSourceMap;
    }


    protected void initUnitilsDataSources() {
        nameUnitilsDataSourceMap = new HashMap<String, UnitilsDataSource>();
        List<String> databaseNames = PropertyUtils.getStringList(PROPERTY_DATABASE_NAMES, configuration);
        if (databaseNames.isEmpty()) {
            defaultUnitilsDataSource = createUnnamedUnitilsDataSource();
            nameUnitilsDataSourceMap.put(null, defaultUnitilsDataSource);
        } else {
            for (String databaseName : databaseNames) {
                UnitilsDataSource unitilsDataSource = null;
                if (isDatabaseIncluded(databaseName)) {
                    unitilsDataSource = createUnitilsDataSource(databaseName);
                }
                nameUnitilsDataSourceMap.put(databaseName, unitilsDataSource);
                if (defaultUnitilsDataSource == null) {
                    defaultUnitilsDataSource = unitilsDataSource;
                }
            }
        }
    }


    /**
     * @return an instance of UnitilsDataSource that is not named: this means there's only one database and consequently only one
     * UnitilsDataSource.
     */
    protected UnitilsDataSource createUnnamedUnitilsDataSource() {
        DataSource dataSource = createUnnamedDataSource();

        String databaseDialect = getString(PROPERTY_DATABASE_START + '.' + PROPERTY_DIALECT_END, configuration);
        List<String> schemaNamesList = PropertyUtils.getStringList(PROPERTY_DATABASE_START + '.' + PROPERTY_SCHEMANAMES_END, configuration);
        if (schemaNamesList.isEmpty()) {
            throw new UnitilsException("No value found for property " + PROPERTY_DATABASE_START + '.' + PROPERTY_SCHEMANAMES_END);
        }
        String defaultSchemaName = schemaNamesList.get(0);
        Set<String> schemaNames = new HashSet<String>(schemaNamesList);
        String identifierQuoteString = determineIdentifierQuoteString(dataSource, databaseDialect);
        StoredIdentifierCase storedIdentifierCase = determineStoredIdentifierCase(dataSource, databaseDialect);
        return new UnitilsDataSource(dataSource, databaseDialect, defaultSchemaName, schemaNames, identifierQuoteString,
                storedIdentifierCase);
    }


    /**
     * Returns specific {@link UnitilsDataSource} as configured with the givfen databaseName
     *
     * @param databaseName logical name that identifies this database
     * @return The instance of {@link UnitilsDataSource}, not null
     */
    protected UnitilsDataSource createUnitilsDataSource(String databaseName) {
        DataSource dataSource = createDataSource(databaseName);

        String databaseDialectPropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_DIALECT_END;
        String customDatabaseDialectPropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_DIALECT_END;
        String databaseDialect = containsProperty(customDatabaseDialectPropertyName, configuration) ?
                getString(customDatabaseDialectPropertyName, configuration) : getString(databaseDialectPropertyName, configuration);
        String schemaNamesListPropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_SCHEMANAMES_END;
        String customSchemaNamesListPropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_SCHEMANAMES_END;
        List<String> schemaNamesList = containsProperty(customSchemaNamesListPropertyName, configuration) ?
                PropertyUtils.getStringList(customSchemaNamesListPropertyName, configuration) : PropertyUtils.getStringList(schemaNamesListPropertyName, configuration);
        if (schemaNamesList.isEmpty()) {
            throw new UnitilsException("No value found for property " + schemaNamesListPropertyName);
        }
        String defaultSchemaName = schemaNamesList.get(0);
        Set<String> schemaNames = new HashSet<String>(schemaNamesList);
        String identifierQuoteString = determineIdentifierQuoteString(dataSource, databaseDialect);
        StoredIdentifierCase storedIdentifierCase = determineStoredIdentifierCase(dataSource, databaseDialect);

        return new UnitilsDataSource(dataSource, databaseDialect, defaultSchemaName, schemaNames, identifierQuoteString, 
                storedIdentifierCase);
    }


    /**
     * @param databaseName the logical name that identifies the database
     * @return whether the database with the given name is included in the set of database to be updated by dbmaintain
     */
    protected boolean isDatabaseIncluded(String databaseName) {
        return PropertyUtils.getBoolean(PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_INCLUDED_END, true, configuration);
    }


    protected DataSource createUnnamedDataSource() {
        String driverClassName = getString(PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END, configuration);
        String url = getString(PROPERTY_DATABASE_START + '.' + PROPERTY_URL_END, configuration);
        String userName = getString(PROPERTY_DATABASE_START + '.' + PROPERTY_USERNAME_END, configuration);
        String password = getString(PROPERTY_DATABASE_START + '.' + PROPERTY_PASSWORD_END, "", configuration);
        return createDataSource(driverClassName, url, userName, password);
    }


    /**
     * @param databaseName The name that identifies the database, not null
     * @return a DataSource that connects with the database as configured for the given database name
     */
    protected DataSource createDataSource(String databaseName) {
        String driverClassNamePropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END;
        String urlPropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END;
        String userNamePropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END;
        String passwordPropertyName = PROPERTY_DATABASE_START + '.' + PROPERTY_DRIVERCLASSNAME_END;
        String customDriverClassNamePropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_DRIVERCLASSNAME_END;
        String customUrlPropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_URL_END;
        String customUserNamePropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_USERNAME_END;
        String customPasswordPropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_PASSWORD_END;
        String customSchemaNamesPropertyName = PROPERTY_DATABASE_START + '.' + databaseName + '.' + PROPERTY_SCHEMANAMES_END;

        if (!(containsProperty(customDriverClassNamePropertyName, configuration) ||
                containsProperty(customUrlPropertyName, configuration) ||
                containsProperty(customUserNamePropertyName, configuration) ||
                containsProperty(customPasswordPropertyName, configuration) ||
                containsProperty(customSchemaNamesPropertyName, configuration))) {
            throw new UnitilsException("No custom database properties defined for database " + databaseName);
        }
        String driverClassName = containsProperty(customDriverClassNamePropertyName, configuration) ?
                getString(customDriverClassNamePropertyName, configuration) : getString(driverClassNamePropertyName, configuration);
        String url = containsProperty(customUrlPropertyName, configuration) ?
                getString(customUrlPropertyName, configuration) : getString(urlPropertyName, configuration);
        String userName = containsProperty(customUserNamePropertyName, configuration) ?
                getString(customUserNamePropertyName, configuration) : getString(userNamePropertyName, configuration);
        String password = containsProperty(customPasswordPropertyName, configuration) ?
                getString(customPasswordPropertyName, configuration) : getString(passwordPropertyName, configuration);
        return createDataSource(driverClassName, url, userName, password);
    }


    protected DataSource createDataSource(String driverClassName, String url, String userName, String password) {
        logger.info("Creating data source. Driver: " + driverClassName + ", url: " + url + ", user: " + userName
                + ", password: <not shown>");
        BasicDataSource dataSource = getNewDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }


    /**
     * Determines the case the database uses to store non-quoted identifiers. This will use the connections
     * database metadata to determine the correct case.
     *
     * @param dataSource the datasource, not null
     * @param databaseDialect the database dialect, not null
     * @return The stored case, not null
     */
    private StoredIdentifierCase determineStoredIdentifierCase(DataSource dataSource, String databaseDialect) {
        StoredIdentifierCase customStoredIdentifierCase = getCustomStoredIdentifierCase(databaseDialect);
        if (customStoredIdentifierCase != null) {
            return customStoredIdentifierCase;
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData.storesUpperCaseIdentifiers()) {
                return StoredIdentifierCase.UPPER_CASE;
            } else if (databaseMetaData.storesLowerCaseIdentifiers()) {
                return StoredIdentifierCase.LOWER_CASE;
            } else {
                return StoredIdentifierCase.MIXED_CASE;
            }
        } catch (SQLException e) {
            throw new UnitilsException("Unable to determine stored identifier case.", e);
        } finally {
            closeQuietly(connection, null, null);
        }
    }


    protected StoredIdentifierCase getCustomStoredIdentifierCase(String databaseDialect) {
        String storedIdentifierCasePropertyValue = org.dbmaintain.config.PropertyUtils.getString(PROPERTY_STORED_IDENTIFIER_CASE + "." + databaseDialect, configuration);
        if ("lower_case".equals(storedIdentifierCasePropertyValue)) {
            return StoredIdentifierCase.LOWER_CASE;
        } else if ("upper_case".equals(storedIdentifierCasePropertyValue)) {
            return StoredIdentifierCase.UPPER_CASE;
        } else if ("mixed_case".equals(storedIdentifierCasePropertyValue)) {
            return StoredIdentifierCase.MIXED_CASE;
        } else if ("auto".equals(storedIdentifierCasePropertyValue)) {
            return null;
        }
        throw new UnitilsException("Unknown value " + storedIdentifierCasePropertyValue + " for property " + PROPERTY_STORED_IDENTIFIER_CASE
                + ". It should be one of lower_case, upper_case, mixed_case or auto.");
    }


    /**
     * Determines the string used to quote identifiers to make them case-sensitive. This will use the connections
     * database metadata to determine the quote string.
     *
     * specified by the JDBC DatabaseMetaData object
     * @param dataSource the datasource, not null
     * @param dialect the database dialect, not null
     * @return The quote string, null if quoting is not supported
     */
    protected String determineIdentifierQuoteString(DataSource dataSource, String dialect) {
        String customIdentifierQuoteString = getCustomIdentifierQuoteString(dialect);
        if (customIdentifierQuoteString != null) {
            return StringUtils.trimToNull(customIdentifierQuoteString);
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();

            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String quoteString = databaseMetaData.getIdentifierQuoteString();
            return StringUtils.trimToNull(quoteString);

        } catch (SQLException e) {
            throw new UnitilsException("Unable to determine identifier quote string.", e);
        } finally {
            closeQuietly(connection, null, null);
        }
    }


    protected String getCustomIdentifierQuoteString(String databaseDialect) {
        String identifierQuoteStringPropertyValue = org.dbmaintain.config.PropertyUtils.getString(PROPERTY_IDENTIFIER_QUOTE_STRING + '.' + databaseDialect, configuration);
        if ("none".equals(identifierQuoteStringPropertyValue)) {
            return "";
        }
        if ("auto".equals(identifierQuoteStringPropertyValue)) {
            return null;
        }
        return identifierQuoteStringPropertyValue;
    }


    /**
     * Returns a concrete instance of <code>BasicDataSource</code>. This method may be overridden e.g. to return a mock
     * instance for testing
     *
     * @return An instance of <code>BasicDataSource</code>
     */
    protected BasicDataSource getNewDataSource() {
        return new BasicDataSource();
    }
}
