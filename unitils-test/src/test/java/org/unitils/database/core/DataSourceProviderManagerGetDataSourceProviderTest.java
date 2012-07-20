/*
 * Copyright 2012,  Unitils.org
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
import org.unitils.database.core.impl.DefaultDataSourceProvider;
import org.unitils.database.core.impl.SpringApplicationContextDataSourceProvider;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.spring.SpringTestManager;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class DataSourceProviderManagerGetDataSourceProviderTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceProviderManager dataSourceProviderManager;

    private Mock<SpringTestManager> springTestManagerMock;
    @Dummy
    private DefaultDataSourceProvider defaultDatabaseProvider;
    @Dummy
    private SpringApplicationContextDataSourceProvider springApplicationContextDatabaseProvider;


    @Before
    public void initialize() {
        dataSourceProviderManager = new DataSourceProviderManager(springTestManagerMock.getMock(), defaultDatabaseProvider, springApplicationContextDatabaseProvider);
    }


    @Test
    public void defaultDatabaseProviderWhenTestDoesNotHaveApplicationContext() throws Exception {
        springTestManagerMock.returns(false).isTestWithApplicationContext();

        DataSourceProvider result = dataSourceProviderManager.getDataSourceProvider();

        assertSame(defaultDatabaseProvider, result);
    }

    @Test
    public void springApplicationContextDatabaseProviderWhenTestHasApplicationContext() throws Exception {
        springTestManagerMock.returns(true).isTestWithApplicationContext();

        DataSourceProvider result = dataSourceProviderManager.getDataSourceProvider();

        assertSame(springApplicationContextDatabaseProvider, result);
    }
}
