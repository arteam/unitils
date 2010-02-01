/*
 * Copyright 2009,  Unitils.org
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
package org.unitils.dataset.core;

import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.List;

/**
 * A data set schema
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Schema {

    /* The name of the data set schema, not null */
    private String name;

    /* True if the schema name is case sensitive */
    private boolean caseSensitive;

    /* The tables in the schema, not null */
    private List<Table> tables = new ArrayList<Table>();


    /**
     * Creates a data set schema.
     *
     * @param name          The name of the schema, not null
     * @param caseSensitive True if the name of the schema is case sensitive
     */
    public Schema(String name, boolean caseSensitive) {
        this.name = name;
        this.caseSensitive = caseSensitive;
    }


    /**
     * @return The name of the data set schema, not null
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the quoted column name if it is a case-sensitive name.
     *
     * @param identifierQuoteString The quote string to use, null if quoting is not supported
     * @return The quoted name or the original name if quoting is not supported or not case sensitive
     */
    public String getQuotedNameIfCaseSensitive(String identifierQuoteString) {
        if (identifierQuoteString == null || !caseSensitive) {
            return name;
        }
        return identifierQuoteString + name + identifierQuoteString;
    }

    /**
     * @return True if the name of the schema is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @return The tables of the schema, not null
     */
    public List<Table> getTables() {
        return tables;
    }

    /**
     * @return The names of the tables of the schema, not null
     */
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<String>();
        for (Table table : tables) {
            tableNames.add(table.getName());
        }
        return tableNames;
    }

    /**
     * Gets the table for the given name. The name is case insensitive.
     *
     * @param tableName The table name to look for, not null
     * @return The table, null if not found
     */
    public Table getTable(String tableName) {
        for (Table table : tables) {
            if (table.hasName(tableName)) {
                return table;
            }
        }
        return null;
    }

    /**
     * Adds a table to the schema. Only one table with a same name can be added.
     *
     * @param table The table to add, not null
     * @throws org.unitils.core.UnitilsException
     *          When a table with the same name was already added
     */
    public void addTable(Table table) {
        Table existingTable = getTable(table.getName());
        if (existingTable != null) {
            throw new UnitilsException("Unable to add table to data set. A table with name " + table.getName() + " already exists.");
        }
        table.setSchema(this);
        tables.add(table);
    }

    /**
     * @param schemaName The name to compare with, not null
     * @return True if this schema has the same name respecting case sensitivity
     */
    public boolean hasName(String schemaName) {
        if (caseSensitive) {
            return name.equals(schemaName);
        }
        return name.equalsIgnoreCase(schemaName);
    }

}