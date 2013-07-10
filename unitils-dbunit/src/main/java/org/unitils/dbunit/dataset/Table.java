/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.dbunit.dataset.comparison.RowDifference;
import org.unitils.dbunit.dataset.comparison.TableDifference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A data set table
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class Table {

    /* The name of the table */
    protected String name;
    /* The data set rows */
    protected List<Row> rows = new ArrayList<Row>();


    /**
     * Creates a data set table.
     *
     * @param name The name of the table, not null
     */
    public Table(String name) {
        this.name = name;
    }

    /**
     * @return The name of the table, not null
     */
    public String getName() {
        return name;
    }

    /**
     * @return The data set rows, not null
     */
    public List<Row> getRows() {
        return rows;
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
        rows.add(row);
    }

    /**
     * Compares the table with the given actual table.
     *
     * @param actualTable The table to compare with, not null
     * @return The difference, null if none found
     */
    public TableDifference compare(Table actualTable) {
        TableDifference result = new TableDifference(this, actualTable);

        if (isEmpty()) {
            if (actualTable.isEmpty()) {
                return null;
            }
            return result;
        }

        compareRows(rows, actualTable, result);
        if (result.isMatch()) {
            return null;
        }
        return result;
    }

    /**
     * Compares the given rows with the columns of the actual table.
     *
     * @param rows        The rows to compare, not null
     * @param actualTable The rows to compare with, not null
     * @param result      The result to add the differences to, not null
     */
    protected void compareRows(List<Row> rows, Table actualTable, TableDifference result) {
        List<Row> rowsWithoutMatch = new ArrayList<Row>(rows);
        for (Row actualRow : actualTable.getRows()) {
            Iterator<Row> rowIterator = rowsWithoutMatch.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.hasDifferentPrimaryKeyColumns(actualRow)) {
                    continue;
                }
                RowDifference rowDifference = row.compare(actualRow);
                if (rowDifference == null) {
                    result.setMatchingRow(row, actualRow);
                    rowIterator.remove();
                    break;
                } else {
                    result.setIfBestRowDifference(rowDifference);
                }
            }
        }

        for (Row row : rowsWithoutMatch) {
            if (result.getBestRowDifference(row) == null) {
                result.addMissingRow(row);
            }
        }
    }
}
