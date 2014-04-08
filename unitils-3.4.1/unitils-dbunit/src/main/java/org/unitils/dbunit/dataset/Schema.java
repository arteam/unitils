/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dbunit.dataset;

import org.unitils.core.UnitilsException;
import org.unitils.dbunit.dataset.comparison.SchemaDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;

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

    /* The tables in the schema, not null */
    private List<Table> tables = new ArrayList<Table>();


    /**
     * Creates a data set schema.
     *
     * @param name The name of the schema, not null
     */
    public Schema(String name) {
        this.name = name;
    }


    /**
     * @return The name of the data set schema, not null
     */
    public String getName() {
        return name;
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
            if (tableName.equalsIgnoreCase(table.getName())) {
                return table;
            }
        }
        return null;
    }


    /**
     * Adds a table to the schema. Only one table with a same name can be added.
     *
     * @param table The table to add, not null
     * @throws UnitilsException When a table with the same name was already added
     */
    public void addTable(Table table) {
        Table existingTable = getTable(table.getName());
        if (existingTable != null) {
            throw new UnitilsException("Unable to add table to data set. A table with name " + table.getName() + " already exists.");
        }
        tables.add(table);
    }


    /**
     * Compares the schema with the given actual schema.
     *
     * @param actualSchema The schema to compare with, not null
     * @return The difference, null if none found
     */
    public SchemaDifference compare(Schema actualSchema) {
        SchemaDifference schemaDifference = new SchemaDifference(this, actualSchema);

        for (Table table : getTables()) {
            Table actualTable = actualSchema.getTable(table.getName());
            if (actualTable == null) {
                schemaDifference.addMissingTable(table);
            } else {
                TableDifference tableDifference = table.compare(actualTable);
                if (tableDifference != null) {
                    schemaDifference.addTableDifference(tableDifference);
                }
            }
        }
        if (schemaDifference.isMatch()) {
            return null;
        }
        return schemaDifference;
    }

}