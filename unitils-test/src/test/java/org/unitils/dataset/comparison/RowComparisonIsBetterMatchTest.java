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
package org.unitils.dataset.comparison;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.core.DatabaseColumn;
import org.unitils.dataset.core.DatabaseRow;
import org.unitils.dataset.core.Value;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonIsBetterMatchTest extends UnitilsJUnit4 {


    @Test
    public void lessDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(1, 2, 3, 999);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(1, 2, 888, 999);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void moreDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(1, 2, 888, 999);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(1, 2, 3, 999);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void lessPkDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(1, 999, 3, 4);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(888, 999, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void morePkDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(888, 999, 3, 4);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(888, 2, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void noDifferences() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(1, 2, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void betterMatchBecauseOfBetterMatchingPk() throws Exception {
        DatabaseRow expectedDatabaseRow1 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow1 = createRow(1, 2, 888, 999);
        DatabaseRow expectedDatabaseRow2 = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow2 = createRow(1, 777, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedDatabaseRow1, actualDatabaseRow1);
        RowComparison rowComparison2 = new RowComparison(expectedDatabaseRow2, actualDatabaseRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }


    private DatabaseRow createRow(Object pk1, Object pk2, Object value1, Object value2) {
        DatabaseRow row = new DatabaseRow("schema.table");
        row.addDatabaseColumnWithValue(new Value(pk1, false, new DatabaseColumn("pk1", VARCHAR, true)));
        row.addDatabaseColumnWithValue(new Value(pk2, false, new DatabaseColumn("pk2", VARCHAR, true)));
        row.addDatabaseColumnWithValue(new Value(value1, false, new DatabaseColumn("column1", VARCHAR, false)));
        row.addDatabaseColumnWithValue(new Value(value2, false, new DatabaseColumn("column2", VARCHAR, false)));
        return row;
    }

}