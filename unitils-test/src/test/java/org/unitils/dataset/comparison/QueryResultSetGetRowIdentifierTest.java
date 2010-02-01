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
import java.util.HashSet;
import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class QueryResultSetGetRowIdentifierTest extends UnitilsJUnit4 {

    /* Tested object */
    private QueryResultSet queryResultSetWithPrimaryKeys;
    private QueryResultSet queryResultSetWithoutPrimaryKeys;

    private Mock<ResultSet> resultSet;

    @Before
    public void initialize() {
        queryResultSetWithPrimaryKeys = new QueryResultSet(null, null, resultSet.getMock(), new LinkedHashSet<String>(asList("pk1", "pk2")));
        queryResultSetWithoutPrimaryKeys = new QueryResultSet(null, null, resultSet.getMock(), new HashSet<String>());
    }

    @Test
    public void getRowIdentifier() throws Exception {
        resultSet.onceReturns("1").getString("pk1");
        resultSet.onceReturns("2").getString("pk2");
        resultSet.onceReturns("3").getString("pk1");
        resultSet.onceReturns("4").getString("pk2");

        queryResultSetWithoutPrimaryKeys.next();
        assertEquals("#pk1=1,pk2=2#", queryResultSetWithPrimaryKeys.getRowIdentifier());
        queryResultSetWithoutPrimaryKeys.next();
        assertEquals("#pk1=3,pk2=4#", queryResultSetWithPrimaryKeys.getRowIdentifier());
    }

    @Test
    public void getRowIdentifier_noPrimaryKeys() throws Exception {
        queryResultSetWithoutPrimaryKeys.next();
        assertEquals("1", queryResultSetWithoutPrimaryKeys.getRowIdentifier());
        queryResultSetWithoutPrimaryKeys.next();
        assertEquals("2", queryResultSetWithoutPrimaryKeys.getRowIdentifier());
    }
}