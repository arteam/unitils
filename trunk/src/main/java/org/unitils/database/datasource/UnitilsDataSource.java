/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.database.datasource;

import org.unitils.core.util.StoredIdentifierCase;

import javax.sql.DataSource;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a test DataSource as configured in unitils. Stores the DataSource itself, the configured schema names
 * and the default schema name.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 24-feb-2009
 */
public class UnitilsDataSource {

    private DataSource dataSource;
    private String dialect;
    private String identifierQuoteString;
    private StoredIdentifierCase storedIdentifierCase;
    private String defaultSchemaName;
    private Set<String> schemaNames;


    /**
     * Constructs a new instance
     * @param dataSource connects to the test database, not null
     * @param dialect defines the dbms type, not null
     * @param defaultSchemaName the name of the default schema, not null
     * @param schemaNames all configured schemas, contains at least the default schema, not null
     * @param identifierQuoteString string used for quoting database object identifiers
     * @param storedIdentifierCase case that is used in metadata tables of the dbms to store the names of database object identifiers
     */
    public UnitilsDataSource(DataSource dataSource, String dialect, String defaultSchemaName, Set<String> schemaNames,
                             String identifierQuoteString, StoredIdentifierCase storedIdentifierCase) {
        this.dialect = dialect;
        if (!schemaNames.contains(defaultSchemaName)) {
            throw new IllegalArgumentException("The default schema name must be one of the schema names");
        }
        this.dataSource = dataSource;
        this.identifierQuoteString = identifierQuoteString;
        this.storedIdentifierCase = storedIdentifierCase;
        this.defaultSchemaName = toCorrectCaseIdentifier(defaultSchemaName);
        this.schemaNames = toCorrectCaseIdentifier(schemaNames);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getDialect() {
        return dialect;
    }

    public String getDefaultSchemaName() {
        return defaultSchemaName;
    }

    public Set<String> getSchemaNames() {
        return schemaNames;
    }


    /**
     * @param schemaNames a set of database object identifiers
     * @return a new set of database object identifiers with the correct stored identifier case
     */
    private Set<String> toCorrectCaseIdentifier(Set<String> schemaNames) {
        Set<String> result = new HashSet<String>(schemaNames.size());
        for (String schemaName : schemaNames) {
            result.add(toCorrectCaseIdentifier(schemaName));
        }
        return result;
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
        if (storedIdentifierCase == StoredIdentifierCase.UPPER_CASE) {
            return identifier.toUpperCase();
        } else if (storedIdentifierCase == StoredIdentifierCase.LOWER_CASE) {
            return identifier.toLowerCase();
        } else {
            return identifier;
        }
    }

    public String getIdentifierQuoteString() {
        return identifierQuoteString;
    }
}
