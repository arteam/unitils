/*
 * Copyright Unitils.org
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
package org.unitils.dataset.comparison.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The comparison result of 2 data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetComparison {

    /* The comparisons between the tables of the data set */
    private List<TableComparison> tableComparisons = new ArrayList<TableComparison>();


    public List<TableComparison> getTableComparisons() {
        return tableComparisons;
    }

    /**
     * Adds a result of a table comparison
     *
     * @param tableComparison The table comparison, not null
     */
    public void addTableComparison(TableComparison tableComparison) {
        tableComparisons.add(tableComparison);
    }

    /**
     * @return True if both data sets are a match
     */
    public boolean isMatch() {
        for (TableComparison tableComparison : tableComparisons) {
            if (!tableComparison.isMatch()) {
                return false;
            }
        }
        return true;
    }
}