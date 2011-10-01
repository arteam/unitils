/*
 * Copyright Unitils.org
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
package org.unitils.database.manager;

import org.dbmaintain.database.DatabaseConnection;
import org.dbmaintain.database.DatabaseException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.UnitilsDataSource;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.isNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceManagerGetDataSourceFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDataSourceManager unitilsDataSourceManager;

    protected Mock<DbMaintainManager> dbMaintainManager;
    protected Mock<DatabaseConnection> databaseConnection;
    @Dummy
    protected DataSource dataSource;


    @Before
    public void initialize() {
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, dbMaintainManager.getMock());
        databaseConnection.returns(dataSource).getDataSource();
        databaseConnection.returns(asSet("schema")).getDatabaseInfo().getSchemaNames();
    }


    @Test
    public void dataSourceForDatabaseWithName() throws Exception {
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection("database1");

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource("database1", null);
        assertSame(dataSource, result.getDataSource());
        assertLenientEquals(asSet("schema"), result.getSchemaNames());
    }

    @Test
    public void defaultDataSource() throws Exception {
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(isNull(String.class));

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, null);
        assertSame(dataSource, result.getDataSource());
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            dbMaintainManager.raises(DatabaseException.class).getDatabaseConnection("xxxx");

            unitilsDataSourceManager.getUnitilsDataSource("xxxx", null);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            // expected
        }
    }

    @Test
    public void dataSourceWrappedInTransactionAwareProxy() throws Exception {
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, null);
        assertTrue(result.getDataSource() instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void disableWrappingInTransactionAwareProxy() throws Exception {
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, null);
        assertFalse(result.getDataSource() instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void sameWrappedDataSourceReturned() throws Exception {
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        UnitilsDataSource result1 = unitilsDataSourceManager.getUnitilsDataSource(null, null);
        UnitilsDataSource result2 = unitilsDataSourceManager.getUnitilsDataSource(null, null);
        assertSame(result1, result2);
    }
}
