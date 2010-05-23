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
import org.unitils.dataset.core.DatabaseColumnWithValue;
import org.unitils.dataset.core.DatabaseRow;

import static java.sql.Types.VARCHAR;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RowComparisonGetNrOfPrimaryKeyDifferencesTest extends UnitilsJUnit4 {


    @Test
    public void equal() throws Exception {
        DatabaseRow expectedDatabaseRow = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow = createRow(1, 2, 3, 4);
        RowComparison rowComparison = new RowComparison(expectedDatabaseRow, actualDatabaseRow);

        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(0, result);
    }

    @Test
    public void noPrimaryKeys() throws Exception {
        RowComparison rowComparison = new RowComparison(new DatabaseRow("schema.table"), new DatabaseRow("schema.table"));
        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(0, result);
    }

    @Test
    public void differences() throws Exception {
        DatabaseRow expectedDatabaseRow = createRow(1, 2, 3, 4);
        DatabaseRow actualDatabaseRow = createRow(888, 999, 3, 4);
        RowComparison rowComparison = new RowComparison(expectedDatabaseRow, actualDatabaseRow);

        int result = rowComparison.getNrOfPrimaryKeyDifferences();
        assertEquals(2, result);
    }


    private DatabaseRow createRow(Object pk1, Object pk2, Object value1, Object value2) {
        DatabaseRow row = new DatabaseRow("schema.table");
        row.addDatabaseColumnWithValue(new DatabaseColumnWithValue("pk1", pk1, VARCHAR, false, true));
        row.addDatabaseColumnWithValue(new DatabaseColumnWithValue("pk2", pk2, VARCHAR, false, true));
        row.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column1", value1, VARCHAR, false, false));
        row.addDatabaseColumnWithValue(new DatabaseColumnWithValue("column2", value2, VARCHAR, false, false));
        return row;
    }


}