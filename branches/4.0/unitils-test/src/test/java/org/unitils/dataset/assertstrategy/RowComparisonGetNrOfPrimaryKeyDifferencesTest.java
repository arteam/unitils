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
import org.unitils.dataset.model.database.Value;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.assertEquals;
import static org.unitils.dataset.util.DataSetTestUtils.createRow;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonGetNrOfPrimaryKeyDifferencesTest extends UnitilsJUnit4 {


    @Test
    public void equal() throws Exception {
        Row expectedRow = createRowWithPks(1, 2, 3, 4);
        Row actualRow = createRowWithPks(1, 2, 3, 4);
        RowComparison rowComparison = new RowComparison(expectedRow, actualRow);

        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(0, result);
    }

    @Test
    public void noPrimaryKeys() throws Exception {
        Row row1 = createRow();
        Row row2 = createRow();
        RowComparison rowComparison = new RowComparison(row1, row2);
        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(0, result);
    }

    @Test
    public void differences() throws Exception {
        Row expectedRow = createRowWithPks(1, 2, 3, 4);
        Row actualRow = createRowWithPks(888, 999, 3, 4);
        RowComparison rowComparison = new RowComparison(expectedRow, actualRow);

        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(2, result);
    }


    private Row createRowWithPks(Object pk1, Object pk2, Object value1, Object value2) {
        Row row = createRow();
        row.addValue(new Value(pk1, false, new Column("pk1", VARCHAR, true)));
        row.addValue(new Value(pk2, false, new Column("pk2", VARCHAR, true)));
        row.addValue(new Value(value1, false, new Column("column1", VARCHAR, false)));
        row.addValue(new Value(value2, false, new Column("column2", VARCHAR, false)));
        return row;
    }


}