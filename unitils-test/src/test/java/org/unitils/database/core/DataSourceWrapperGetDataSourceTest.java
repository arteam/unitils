/*
 * Copyright 2013,  Unitils.org
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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperGetDataSourceTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapper dataSourceWrapper;

    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dataSourceWrapper = new DataSourceWrapper(dataSource, null);
    }


    @Test
    public void getDataSource() {
        DataSource result = dataSourceWrapper.getDataSource(false);
        assertSame(dataSource, result);
    }

    @Test
    public void wrapDataSourceInTransactionalProxy() {
        DataSource result = dataSourceWrapper.getDataSource(true);
        assertTrue(result instanceof TransactionAwareDataSourceProxy);
        assertSame(dataSource, ((TransactionAwareDataSourceProxy) result).getTargetDataSource());
    }

    @Test
    public void alwaysReturnSameTransactionAwareDataSourceProxy() {
        DataSource result1 = dataSourceWrapper.getDataSource(true);
        DataSource result2 = dataSourceWrapper.getDataSource(true);

        assertTrue(result1 instanceof TransactionAwareDataSourceProxy);
        assertTrue(result2 instanceof TransactionAwareDataSourceProxy);
        assertSame(result1, result2);
    }
}
