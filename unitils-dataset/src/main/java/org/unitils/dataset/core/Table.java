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
package org.unitils.dataset.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A data set table
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Table {

    /* The name of the table */
    private String name;

    /* The schema this table belongs to */
    private Schema schema;

    private boolean caseSensitive;

    /* The data set rows */
    private List<Row> rows = new ArrayList<Row>();


    /**
     * Creates a data set table.
     *
     * @param name          The name of the table, not null
     * @param caseSensitive True if the name of the table is case sensitive
     */
    public Table(String name, boolean caseSensitive) {
        this.name = name;
        this.caseSensitive = caseSensitive;
    }


    /**
     * @return The name of the table, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @param schema The schema this table belongs to, not null
     */
    void setSchema(Schema schema) {
        this.schema = schema;
    }

    /**
     * @return The schema this table belongs to, not null
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * @return True if the name of the table is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * @return The data set rows, not null
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * @param index the row index
     * @return The data set row, not null
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public Row getRow(int index) {
        return rows.get(index);
    }

    /**
     * @return The nr of rows in the table >= 0
     */
    public int getNrOfRows() {
        return rows.size();
    }

    /**
     * @return True if the table does not contain any rows
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    /**
     * Adds a data set row
     *
     * @param row The row to add, not null
     */
    public void addRow(Row row) {
        row.setTable(this);
        rows.add(row);
    }

    /**
     * @param tableName The name to compare with, not null
     * @return True if this table has the same name respecting case sensitivity
     */
    public boolean hasName(String tableName) {
        if (caseSensitive) {
            return name.equals(tableName);
        }
        return name.equalsIgnoreCase(tableName);
    }

    @Override
    public String toString() {
        return schema.getName() + "." + name;
    }
}