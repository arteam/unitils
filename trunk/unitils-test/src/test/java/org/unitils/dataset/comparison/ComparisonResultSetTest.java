/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.dataset.comparison.impl.ComparisonResultSet;
import org.unitils.dataset.core.ProcessedColumn;
import org.unitils.mock.Mock;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ComparisonResultSetTest extends UnitilsJUnit4 {

    /* Tested object */
    private ComparisonResultSet comparisonResultSet;

    private Mock<ResultSet> resultSet;

    private ProcessedColumn processedColumn;
    private ProcessedColumn processedPkColumn;
    private Set<String> emptyPrimaryKeyColumnNames = new HashSet<String>();


    @Before
    public void initialize() {
        processedColumn = new ProcessedColumn("column1", "value", false, false, null);
        processedPkColumn = new ProcessedColumn("column2", "value", false, true, null);
    }


    @Test
    public void getRowComparisonWith2Columns() throws Exception {
        List<ProcessedColumn> processedColumns = asList(processedColumn, processedPkColumn);
        resultSet.returns("1").getString(1);
        resultSet.returns("2").getString(2);
        resultSet.returns("3").getString(3);
        resultSet.returns("4").getString(4);

        comparisonResultSet = new ComparisonResultSet(processedColumns, null, null, resultSet.getMock(), emptyPrimaryKeyColumnNames);
        RowComparison rowComparison = comparisonResultSet.getRowComparison(null);

        ColumnComparison columnComparison1 = rowComparison.getColumnComparisons().get(0);
        assertEquals("1", columnComparison1.getActualValue());
        assertEquals("2", columnComparison1.getExpectedValue());
        assertFalse(columnComparison1.isPrimaryKey());

        ColumnComparison columnComparison2 = rowComparison.getColumnComparisons().get(1);
        assertEquals("3", columnComparison2.getActualValue());
        assertEquals("4", columnComparison2.getExpectedValue());
        assertTrue(columnComparison2.isPrimaryKey());
    }

    @Test
    public void noColumns() throws Exception {
        List<ProcessedColumn> emptyProcessedColumns = new ArrayList<ProcessedColumn>();
        comparisonResultSet = new ComparisonResultSet(emptyProcessedColumns, null, null, resultSet.getMock(), emptyPrimaryKeyColumnNames);

        RowComparison rowComparison = comparisonResultSet.getRowComparison(null);
        assertTrue(rowComparison.getColumnComparisons().isEmpty());
    }
}