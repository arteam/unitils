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

package org.unitils.database.core.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.config.DatabaseConfigurations;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.database.core.DataSourceWrapperFactory;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DefaultDataSourceProviderGetDataSourceWrapperTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSourceProvider defaultDatabaseProvider;

    private Mock<DatabaseConfigurations> databaseConfigurationsMock;
    private Mock<DataSourceWrapperFactory> dataSourceWrapperFactoryMock;

    @Dummy
    private DatabaseConfiguration databaseConfiguration;
    private Mock<DataSourceWrapper> dataSourceWrapperMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        defaultDatabaseProvider = new DefaultDataSourceProvider(databaseConfigurationsMock.getMock(), dataSourceWrapperFactoryMock.getMock());
    }


    @Test
    public void getDataSourceWrapper() throws Exception {
        databaseConfigurationsMock.returns(databaseConfiguration).getDatabaseConfiguration("myDatabase");
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = defaultDatabaseProvider.getDataSourceWrapper("myDatabase");

        assertSame(dataSourceWrapperMock.getMock(), result);
    }

    @Test
    public void dataSourceWrappersAreCached() throws Exception {
        databaseConfigurationsMock.returns(databaseConfiguration).getDatabaseConfiguration("myDatabase");
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result1 = defaultDatabaseProvider.getDataSourceWrapper("myDatabase");
        DataSourceWrapper result2 = defaultDatabaseProvider.getDataSourceWrapper("myDatabase");

        assertSame(result1, result2);
    }

    @Test
    public void nullDatabaseName() throws Exception {
        databaseConfigurationsMock.returns(databaseConfiguration).getDatabaseConfiguration(isNull(String.class));
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = defaultDatabaseProvider.getDataSourceWrapper(null);

        assertSame(dataSourceWrapperMock.getMock(), result);
    }

    @Test
    public void emptyDatabaseNameSameAsNullDatabaseName() throws Exception {
        databaseConfigurationsMock.onceReturns(databaseConfiguration).getDatabaseConfiguration("");
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = defaultDatabaseProvider.getDataSourceWrapper("");

        assertSame(dataSourceWrapperMock.getMock(), result);
    }
}
