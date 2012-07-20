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
import org.dbmaintain.database.DatabaseInfo;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.core.DataSourceProvider;
import org.unitils.database.core.DataSourceProviderManager;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainDatabaseConnectionManagerGetDatabaseConnectionTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager;

    private Mock<DataSourceProviderManager> dataSourceProviderManagerMock;
    private Mock<DataSourceProvider> dataSourceProviderMock;
    @Dummy
    private DbMaintainSQLHandler dbMaintainSQLHandler;
    private Mock<DataSourceWrapper> dataSourceWrapperMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainDatabaseConnectionManager = new DbMaintainDatabaseConnectionManager(dataSourceProviderManagerMock.getMock(), dbMaintainSQLHandler);
        dataSourceProviderManagerMock.returns(dataSourceProviderMock).getDataSourceProvider();

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("myDatabase", "myDialect", "myDriver", "myUrl", "myUser", "myPass", "schema1", asList("schema1", "schema2"), false, true);
        dataSourceWrapperMock.returns(databaseConfiguration).getDatabaseConfiguration();
        dataSourceWrapperMock.returns(dataSource).getWrappedDataSource();
    }


    @Test
    public void namedDatabase() {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper("myDatabase");

        DatabaseConnection result = dbMaintainDatabaseConnectionManager.getDatabaseConnection("myDatabase");
        DatabaseInfo databaseInfo = result.getDatabaseInfo();
        assertEquals("myDatabase", databaseInfo.getName());
        assertEquals("myDialect", databaseInfo.getDialect());
        assertEquals("myDriver", databaseInfo.getDriverClassName());
        assertEquals("myUrl", databaseInfo.getUrl());
        assertEquals("myUser", databaseInfo.getUserName());
        assertEquals("myPass", databaseInfo.getPassword());
        assertLenientEquals(asList("schema1", "schema2"), databaseInfo.getSchemaNames());
        assertFalse(databaseInfo.isDisabled());
        assertTrue(databaseInfo.isDefaultDatabase());
        assertSame(dataSource, result.getDataSource());
        assertSame(dbMaintainSQLHandler, result.getSqlHandler());
    }

    @Test
    public void nullDatabaseName() {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper(null);

        DatabaseConnection result = dbMaintainDatabaseConnectionManager.getDatabaseConnection(null);
        assertNotNull(result.getDatabaseInfo());
        assertSame(dataSource, result.getDataSource());
        assertSame(dbMaintainSQLHandler, result.getSqlHandler());
    }

    @Test
    public void blankDatabaseNameSameAsNullDatabaseName() {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper(null);

        DatabaseConnection result = dbMaintainDatabaseConnectionManager.getDatabaseConnection("");
        assertNotNull(result.getDatabaseInfo());
        assertSame(dataSource, result.getDataSource());
        assertSame(dbMaintainSQLHandler, result.getSqlHandler());
    }

    @Test
    public void databaseConnectionsAreCached() {
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper(null);

        DatabaseConnection result1 = dbMaintainDatabaseConnectionManager.getDatabaseConnection(null);
        DatabaseConnection result2 = dbMaintainDatabaseConnectionManager.getDatabaseConnection(null);
        assertSame(result1, result2);
    }

    @Test
    public void exceptionWhenDefaultDatabaseIsDisabled() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration("myDatabase", "myDialect", "myDriver", "myUrl", "myUser", "myPass", "schema1", asList("schema1", "schema2"), true, true);
        dataSourceWrapperMock.onceReturns(databaseConfiguration).getDatabaseConfiguration();
        dataSourceProviderMock.returns(dataSourceWrapperMock).getDataSourceWrapper(null);
        try {
            dbMaintainDatabaseConnectionManager.getDatabaseConnection(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid DbMaintain configuration. Default database cannot be disabled.", e.getMessage());
        }
    }
}
