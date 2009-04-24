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
    private String tableName;

    /* The data set rows */
    private List<Row> rows = new ArrayList<Row>();


    /**
     * Creates a data set table.
     *
     * @param tableName The name of the table, not null
     */
    public Table(String tableName) {
        this.tableName = tableName;
    }


    /**
     * @return The name of the table, not null
     */
    public String getTableName() {
        return tableName;
    }


    /**
     * @return The data set rows, not null
     */
    public List<Row> getRows() {
        return rows;
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
        TableDifference tableDifference = new TableDifference(this, actualTable);

        List<Row> rowsWithoutMatch = new ArrayList<Row>(rows);
        for (Row actualRow : actualTable.getRows()) {
            Iterator<Row> rowIterator = rowsWithoutMatch.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (!row.canCompare(actualRow)) {
                    continue;
                }
                RowDifference rowDifference = row.compare(actualRow);
                if (rowDifference == null) {
                    tableDifference.setMatchingRow(row, actualRow);
                    rowIterator.remove();
                    break;
                } else {
                    tableDifference.setIfBestRowDifference(rowDifference);
                }
            }
        }

        for (Row row : rowsWithoutMatch) {
            if (tableDifference.getBestRowDifference(row) == null) {
                tableDifference.addMissingRow(row);
            }
        }

        if (tableDifference.isMatch()) {
            return null;
        }
        return tableDifference;
    }
}
