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
package org.unitils.dbunit.dataset.comparison;

import org.unitils.dbunit.dataset.Schema;
import org.unitils.dbunit.dataset.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * The differences between 2 data set schemas.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SchemaDifference {

    /* The expected schema, not null */
    private Schema schema;

    /* The actual schema, not null */
    private Schema actualSchema;

    /* The tables that were not found in the actual schema, empty if none found */
    private List<Table> missingTables = new ArrayList<Table>();

    /* The differences between the tables of the schemas, empty if none found */
    private List<TableDifference> tableDifferences = new ArrayList<TableDifference>();


    /**
     * Create a schema difference.
     *
     * @param schema       The expected schema, not null
     * @param actualSchema The actual schema, not null
     */
    public SchemaDifference(Schema schema, Schema actualSchema) {
        this.schema = schema;
        this.actualSchema = actualSchema;
    }


    /**
     * @return The expected schema, not null
     */
    public Schema getSchema() {
        return schema;
    }


    /**
     * @return The actual schema, not null
     */
    public Schema getActualSchema() {
        return actualSchema;
    }


    /**
     * @return The differences between the tables of the schema, empty if none found
     */
    public List<TableDifference> getTableDifferences() {
        return tableDifferences;
    }


    /**
     * @return The tables that were not found in the actual schema, empty if none found
     */
    public List<Table> getMissingTables() {
        return missingTables;
    }


    /**
     * Adds a result of a table comparison
     *
     * @param tableDifference The table comparison, not null
     */
    public void addTableDifference(TableDifference tableDifference) {
        this.tableDifferences.add(tableDifference);
    }


    /**
     * Adds a table that was not found in the actual schema
     *
     * @param table The missing table, not null
     */
    public void addMissingTable(Table table) {
        missingTables.add(table);
    }


    /**
     * @return True if both schemas are a match
     */
    public boolean isMatch() {
        return missingTables.isEmpty() && tableDifferences.isEmpty();
    }

}