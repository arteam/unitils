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
import org.unitils.dataset.comparison.impl.QueryResultSet;
import org.unitils.mock.Mock;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class QueryResultSetColumnsTest extends UnitilsJUnit4 {

    /* Tested object */
    private QueryResultSet queryResultSet;

    private Mock<ResultSet> resultSet;
    private Mock<ResultSetMetaData> resultSetMetaData;

    @Before
    public void initialize() throws Exception {
        resultSet.returns(resultSetMetaData).getMetaData();
        queryResultSet = new QueryResultSet(null, null, resultSet.getMock(), new HashSet<String>());
    }

    @Test
    public void getColumnNames() throws Exception {
        resultSetMetaData.returns(2).getColumnCount();
        resultSetMetaData.returns("column1").getColumnName(1);
        resultSetMetaData.returns("column2").getColumnName(2);

        List<String> result = queryResultSet.getColumnNames();
        assertLenientEquals(asList("column1", "column2"), result);
    }

}