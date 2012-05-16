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

package org.unitilsnew.database.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.database.config.DatabaseConfiguration;
import org.unitilsnew.database.config.DatabaseConfigurations;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperManagerGetDataSourceWrapperTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapperManager dataSourceWrapperManager;

    protected Mock<DatabaseConfigurations> databaseConfigurationsMock;
    protected Mock<DataSourceWrapperFactory> dataSourceWrapperFactoryMock;
    protected Mock<TransactionManager> transactionManagerMock;

    @Dummy
    private DatabaseConfiguration databaseConfiguration;
    private Mock<DataSourceWrapper> dataSourceWrapperMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dataSourceWrapperManager = new DataSourceWrapperManager(databaseConfigurationsMock.getMock(), dataSourceWrapperFactoryMock.getMock(), transactionManagerMock.getMock());

        DataSourceWrapperManager.dataSourceWrappers.clear();
    }

    @After
    public void cleanup() {
        DataSourceWrapperManager.dataSourceWrappers.clear();
    }


    @Test
    public void createDataSourceWrapper() throws Exception {
        databaseConfigurationsMock.returns(databaseConfiguration).getDatabaseConfiguration("myDatabase");
        dataSourceWrapperFactoryMock.returns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = dataSourceWrapperManager.getDataSourceWrapper("myDatabase");

        assertSame(dataSourceWrapperMock.getMock(), result);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
    }

    @Test
    public void dataSourceWrappersAreCached() throws Exception {
        databaseConfigurationsMock.onceReturns(databaseConfiguration).getDatabaseConfiguration("myDatabase");
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result1 = dataSourceWrapperManager.getDataSourceWrapper("myDatabase");
        DataSourceWrapper result2 = dataSourceWrapperManager.getDataSourceWrapper("myDatabase");

        assertSame(result1, result2);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
    }

    @Test
    public void nullDatabaseName() throws Exception {
        databaseConfigurationsMock.onceReturns(databaseConfiguration).getDatabaseConfiguration(isNull(String.class));
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = dataSourceWrapperManager.getDataSourceWrapper(null);

        assertSame(dataSourceWrapperMock.getMock(), result);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
    }

    @Test
    public void emptyDatabaseNameSameAsNullDatabaseName() throws Exception {
        databaseConfigurationsMock.onceReturns(databaseConfiguration).getDatabaseConfiguration(isNull(String.class));
        dataSourceWrapperFactoryMock.onceReturns(dataSourceWrapperMock).create(databaseConfiguration);
        dataSourceWrapperMock.returns(dataSource).getDataSource(false);

        DataSourceWrapper result = dataSourceWrapperManager.getDataSourceWrapper("");

        assertSame(dataSourceWrapperMock.getMock(), result);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
    }
}
