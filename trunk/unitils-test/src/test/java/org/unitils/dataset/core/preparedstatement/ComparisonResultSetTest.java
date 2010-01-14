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
package org.unitils.dataset.core.preparedstatement;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ComparisonResultSetTest extends UnitilsJUnit4 {

    /* Tested object */
    private ComparisonResultSet comparisonResultSet;

    private Mock<ResultSet> resultSet;

    @Before
    public void initialize() {
        Set<String> primaryKeyColumnNames = new HashSet<String>(asList("pk1", "pk2"));
        comparisonResultSet = new ComparisonResultSet(resultSet.getMock(), primaryKeyColumnNames);
    }

    @Test
    public void getExpectedAndActualValues() throws Exception {
        resultSet.returns("1").getString(1);
        resultSet.returns("2").getString(2);
        resultSet.returns("3").getString(3);
        resultSet.returns("4").getString(4);

        assertEquals("1", comparisonResultSet.getActualValue(0));
        assertEquals("2", comparisonResultSet.getExpectedValue(0));
        assertEquals("3", comparisonResultSet.getActualValue(1));
        assertEquals("4", comparisonResultSet.getExpectedValue(1));
    }
}