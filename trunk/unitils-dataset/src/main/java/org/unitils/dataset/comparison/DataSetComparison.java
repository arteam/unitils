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
package org.unitils.dataset.comparison;

import org.unitils.dataset.comparison.SchemaComparison;

import java.util.ArrayList;
import java.util.List;

/**
 * The comparison result of 2 data sets.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetComparison {

    /* The differences between the schema's of the data set, empty if none found */
    private List<SchemaComparison> schemaComparisons = new ArrayList<SchemaComparison>();


    public List<SchemaComparison> getSchemaComparisons() {
        return schemaComparisons;
    }

    /**
     * Adds a result of a schema comparison
     *
     * @param schemaComparison The schema comparison, not null
     */
    public void addSchemaComparison(SchemaComparison schemaComparison) {
        schemaComparisons.add(schemaComparison);
    }

    /**
     * @return True if both data sets are a match
     */
    public boolean isMatch() {
        for (SchemaComparison schemaComparison : schemaComparisons) {
            if (!schemaComparison.isMatch()) {
                return false;
            }
        }
        return true;
    }
}