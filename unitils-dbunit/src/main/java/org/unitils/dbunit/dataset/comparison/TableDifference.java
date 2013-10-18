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

import org.unitils.dbunit.dataset.Row;
import org.unitils.dbunit.dataset.Table;

import java.util.*;

/**
 * The differences between 2 data set tables.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TableDifference {

    /* The expected table, not null */
    private Table table;

    /* The actual table, not null */
    private Table actualTable;

    /* The rows for which no other row was found in the actual table, empty if none found */
    private List<Row> missingRows = new ArrayList<Row>();

    /* The best differences of the comparisons between the rows of the tables */
    private Map<Row, RowDifference> bestRowDifferences = new HashMap<Row, RowDifference>();


    /**
     * Creates a table difference.
     *
     * @param table       The expected table, not null
     * @param actualTable The actual table, not null
     */
    public TableDifference(Table table, Table actualTable) {
        this.table = table;
        this.actualTable = actualTable;
    }


    /**
     * @return The expected table, not null
     */
    public Table getTable() {
        return table;
    }


    /**
     * @return The actual table, null if the table was not found
     */
    public Table getActualTable() {
        return actualTable;
    }


    /**
     * @return The rows for which no other row was found in the actual table, empty if none found
     */
    public List<Row> getMissingRows() {
        return missingRows;
    }


    /**
     * Adds a rows for which no other row was found in the actual table.
     *
     * @param missingRow The missing row, not null
     */
    public void addMissingRow(Row missingRow) {
        missingRows.add(missingRow);
    }


    /**
     * @return The best results in the comparison between the rows, not null
     */
    public List<RowDifference> getBestRowDifferences() {
        return new ArrayList<RowDifference>(bestRowDifferences.values());
    }


    /**
     * @param row The row to get the difference for, not null
     * @return The best difference, null if not found or if there was a match
     */
    public RowDifference getBestRowDifference(Row row) {
        return bestRowDifferences.get(row);
    }


    /**
     * Indicates a match for the given row.
     *
     * @param row       The row, not null
     * @param actualRow The matching actual row, not null
     */
    public void setMatchingRow(Row row, Row actualRow) {
        bestRowDifferences.remove(row);
        Iterator<RowDifference> iterator = bestRowDifferences.values().iterator();
        while (iterator.hasNext()) {
            RowDifference bestRowDifference = iterator.next();
            if (bestRowDifference.getActualRow() == actualRow) {
                iterator.remove();
            }
        }
    }


    /**
     * Sets the given difference as best row difference if it is better than the current best row difference.
     *
     * @param rowDifference The difference, null for a match
     */
    public void setIfBestRowDifference(RowDifference rowDifference) {
        RowDifference bestRowComparison = bestRowDifferences.get(rowDifference.getRow());
        if (bestRowComparison == null || rowDifference.isBetterMatch(bestRowComparison)) {
            bestRowDifferences.put(rowDifference.getRow(), rowDifference);
        }
    }


    /**
     * @return True if both tables are a match
     */
    public boolean isMatch() {
        return missingRows.isEmpty() && bestRowDifferences.isEmpty();
    }

}