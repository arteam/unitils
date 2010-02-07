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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.Column;
import org.unitils.dataset.core.Row;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonIsBetterMatchTest extends UnitilsJUnit4 {

    private RowComparison rowComparison1 = new RowComparison(new Row());
    private RowComparison rowComparison2 = new RowComparison(new Row());

    private ColumnComparison equal;
    private ColumnComparison equalPk;
    private ColumnComparison different;
    private ColumnComparison differentPk;

    @Before
    public void initialize() {
        Column column = new Column("name", "value1", false);
        equal = new ColumnComparison(column, "value1", "value1", false);
        equalPk = new ColumnComparison(column, "value1", "value1", true);
        different = new ColumnComparison(column, "value1", "xxxx", false);
        differentPk = new ColumnComparison(column, "value1", "xxxx", true);
    }

    @Test
    public void lessDifferences() throws Exception {
        rowComparison1.addColumnComparison(equal);
        rowComparison1.addColumnComparison(different);
        rowComparison2.addColumnComparison(different);
        rowComparison2.addColumnComparison(different);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void moreDifferences() throws Exception {
        rowComparison1.addColumnComparison(different);
        rowComparison1.addColumnComparison(different);
        rowComparison2.addColumnComparison(different);
        rowComparison2.addColumnComparison(equal);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void noDifferences() throws Exception {
        rowComparison1.addColumnComparison(equal);
        rowComparison1.addColumnComparison(equal);
        rowComparison2.addColumnComparison(equal);
        rowComparison2.addColumnComparison(equal);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void lessPkDifferences() throws Exception {
        rowComparison1.addColumnComparison(equalPk);
        rowComparison1.addColumnComparison(differentPk);
        rowComparison2.addColumnComparison(differentPk);
        rowComparison2.addColumnComparison(differentPk);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void morePkDifferences() throws Exception {
        rowComparison1.addColumnComparison(differentPk);
        rowComparison1.addColumnComparison(differentPk);
        rowComparison2.addColumnComparison(differentPk);
        rowComparison2.addColumnComparison(equalPk);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void noPkDifferences() throws Exception {
        rowComparison1.addColumnComparison(equalPk);
        rowComparison1.addColumnComparison(equalPk);
        rowComparison2.addColumnComparison(equalPk);
        rowComparison2.addColumnComparison(equalPk);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void betterMatchBecauseOfBetterMatchingPk() throws Exception {
        rowComparison1.addColumnComparison(equalPk);
        rowComparison1.addColumnComparison(equalPk);
        rowComparison1.addColumnComparison(different);
        rowComparison1.addColumnComparison(different);

        rowComparison2.addColumnComparison(equalPk);
        rowComparison2.addColumnComparison(differentPk);
        rowComparison2.addColumnComparison(equal);
        rowComparison2.addColumnComparison(equal);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }
}