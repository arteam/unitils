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

package org.unitils.database.dbmaintain;

import org.dbmaintain.database.DatabaseConnection;
import org.junit.Before;
import org.junit.Test;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.core.DataSourceProvider;
import org.unitils.database.core.DataSourceProviderManager;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainDatabaseConnectionManagerGetDatabaseConnectionsTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager;

    private Mock<DataSourceProviderManager> dataSourceProviderManagerMock;
    private Mock<DataSourceProvider> dataSourceProviderMock;
    @Dummy
    private DbMaintainSQLHandler dbMaintainSQLHandler;
    private Mock<DataSourceWrapper> dataSourceWrapperMock1;
    private Mock<DataSourceWrapper> dataSourceWrapperMock2;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainDatabaseConnectionManager = new DbMaintainDatabaseConnectionManager(dataSourceProviderManagerMock.getMock(), dbMaintainSQLHandler);
        dataSourceProviderManagerMock.returns(dataSourceProviderMock).getDataSourceProvider();

        DatabaseConfiguration databaseConfiguration1 = new DatabaseConfiguration("database1", null, null, null, null, null, null, Collections.<String>emptyList(), false, true);
        dataSourceWrapperMock1.returns(databaseConfiguration1).getDatabaseConfiguration();
        dataSourceWrapperMock1.returns(dataSource).getWrappedDataSource();

        DatabaseConfiguration databaseConfiguration2 = new DatabaseConfiguration("database2", null, null, null, null, null, null, Collections.<String>emptyList(), false, true);
        dataSourceWrapperMock2.returns(databaseConfiguration2).getDatabaseConfiguration();
        dataSourceWrapperMock2.returns(dataSource).getWrappedDataSource();
    }


    @Test
    public void namedDatabase() {
        dataSourceProviderMock.returns(asList("database1", "database2")).getDatabaseNames();
        dataSourceProviderMock.returns(dataSourceWrapperMock1).getDataSourceWrapper("database1");
        dataSourceProviderMock.returns(dataSourceWrapperMock2).getDataSourceWrapper("database2");

        List<DatabaseConnection> result = dbMaintainDatabaseConnectionManager.getDatabaseConnections();
        assertEquals(2, result.size());
        assertEquals("database1", result.get(0).getDatabaseInfo().getName());
        assertEquals("database2", result.get(1).getDatabaseInfo().getName());
    }

    @Test
    public void defaultDatabaseWhenThereAreNoNamedDatabases() {
        dataSourceProviderMock.returns(Collections.<String>emptyList()).getDatabaseNames();
        dataSourceProviderMock.returns(dataSourceWrapperMock1).getDataSourceWrapper(isNull(String.class));

        List<DatabaseConnection> result = dbMaintainDatabaseConnectionManager.getDatabaseConnections();
        assertEquals(1, result.size());
        assertEquals("database1", result.get(0).getDatabaseInfo().getName());
    }
}
