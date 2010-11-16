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
package org.unitils.dataset.assertstrategy;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.dataset.assertstrategy.model.RowComparison;
import org.unitils.dataset.model.database.Column;
import org.unitils.dataset.model.database.Row;
import org.unitils.dataset.model.database.TableName;
import org.unitils.dataset.model.database.Value;

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
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(1, 2, 3, 999);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(1, 2, 888, 999);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void moreDifferences() throws Exception {
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(1, 2, 888, 999);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(1, 2, 3, 999);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void lessPkDifferences() throws Exception {
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(1, 999, 3, 4);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(888, 999, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }

    @Test
    public void morePkDifferences() throws Exception {
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(888, 999, 3, 4);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(888, 2, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void noDifferences() throws Exception {
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(1, 2, 3, 4);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(1, 2, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertFalse(result);
    }

    @Test
    public void betterMatchBecauseOfBetterMatchingPk() throws Exception {
        Row expectedRow1 = createRow(1, 2, 3, 4);
        Row actualRow1 = createRow(1, 2, 888, 999);
        Row expectedRow2 = createRow(1, 2, 3, 4);
        Row actualRow2 = createRow(1, 777, 3, 4);

        RowComparison rowComparison1 = new RowComparison(expectedRow1, actualRow1);
        RowComparison rowComparison2 = new RowComparison(expectedRow2, actualRow2);

        boolean result = rowComparison1.isBetterMatch(rowComparison2);
        assertTrue(result);
    }


    private Row createRow(Object pk1, Object pk2, Object value1, Object value2) {
        Row row = new Row(new TableName("schema", "table", "schema.table"));
        row.addValue(new Value(pk1, false, new Column("pk1", VARCHAR, true)));
        row.addValue(new Value(pk2, false, new Column("pk2", VARCHAR, true)));
        row.addValue(new Value(value1, false, new Column("column1", VARCHAR, false)));
        row.addValue(new Value(value2, false, new Column("column2", VARCHAR, false)));
        return row;
    }

}