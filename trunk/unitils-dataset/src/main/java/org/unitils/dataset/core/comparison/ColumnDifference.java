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
package org.unitils.dataset.core.comparison;

import org.unitils.dataset.core.Column;

/**
 * The difference between 2 column values.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ColumnDifference {

    /* The expected column, not null */
    private Column column;

    /* The actual column, not null */
    private Column actualColumn;


    /**
     * Creates a column difference.
     *
     * @param column       The expected column, not null
     * @param actualColumn The actual column, not null
     */
    public ColumnDifference(Column column, Column actualColumn) {
        this.column = column;
        this.actualColumn = actualColumn;
    }


    /**
     * @return The expected value, not null
     */
    public Column getColumn() {
        return column;
    }


    /**
     * @return The actual value, null if the value was not found
     */
    public Column getActualColumn() {
        return actualColumn;
    }

}