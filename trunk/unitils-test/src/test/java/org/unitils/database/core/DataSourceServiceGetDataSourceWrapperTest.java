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
import org.unitils.UnitilsJUnit4;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.dbmaintain.DbMaintainWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DataSourceServiceGetDataSourceWrapperTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceService dataSourceService;

    private Mock<DataSourceProviderManager> dataSourceProviderManagerMock;
    private Mock<DataSourceProvider> dataSourceProviderMock;
    private Mock<DbMaintainWrapper> dbMaintainWrapperMock;

    @Dummy
    private DatabaseConfiguration databaseConfiguration;
    private Mock<DataSourceWrapper> dataSourceWrapperMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dataSourceService = new DataSourceService(dataSourceProviderManagerMock.getMock(), dbMaintainWrapperMock.getMock());
        dataSourceProviderManagerMock.returns(dataSourceProviderMock).getDataSourceProvider();
    }


    @Test
    public void getDataSourceWrapper() throws Exception {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper("databaseName");
        dataSourceWrapperMock.returns(dataSource).getWrappedDataSource();

        DataSourceWrapper result = dataSourceService.getDataSourceWrapper("databaseName");

        assertSame(dataSourceWrapperMock.getMock(), result);
        dbMaintainWrapperMock.assertInvoked().updateDatabaseIfNeeded();
    }

    @Test
    public void nullDatabaseName() throws Exception {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper(isNull(String.class));
        dataSourceWrapperMock.returns(dataSource).getWrappedDataSource();

        DataSourceWrapper result = dataSourceService.getDataSourceWrapper(null);

        assertSame(dataSourceWrapperMock.getMock(), result);
        dbMaintainWrapperMock.assertInvoked().updateDatabaseIfNeeded();
    }
}
