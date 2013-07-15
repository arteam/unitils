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
package org.unitils.database.transaction.impl;

import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DefaultTransactionProviderGetPlatformTransactionManagerTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultTransactionProvider defaultTransactionProvider = new DefaultTransactionProvider();

    @Dummy
    private DataSource dataSource1;
    @Dummy
    private DataSource dataSource2;


    @Test
    public void getPlatformTransactionManager() {
        PlatformTransactionManager result = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource1);

        assertTrue(result instanceof DataSourceTransactionManager);
        assertSame(dataSource1, ((DataSourceTransactionManager) result).getDataSource());
    }

    @Test
    public void transactionManagerCachedPerDataSource() {
        PlatformTransactionManager result1 = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource1);
        PlatformTransactionManager result2 = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource2);
        PlatformTransactionManager result3 = defaultTransactionProvider.getPlatformTransactionManager(null, dataSource1);

        assertSame(result1, result3);
        assertNotSame(result1, result2);
    }
}
