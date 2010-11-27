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
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.UnitilsDataSource;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceManagerGetDatabaseNamesTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDataSourceManager unitilsDataSourceManager;

    protected Mock<DbMaintainManager> dbMaintainManager;
    protected Mock<DatabaseConnection> databaseConnection1;
    protected Mock<DatabaseConnection> databaseConnection2;

    @Dummy
    protected UnitilsDataSource unitilsDataSource;

    private StaticApplicationContext staticApplicationContext;


    @Before
    public void initialize() {
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, dbMaintainManager.getMock());
        databaseConnection1.returns("database1").getDatabaseInfo().getName();
        databaseConnection2.returns("database2").getDatabaseInfo().getName();

        staticApplicationContext = new StaticApplicationContext();
    }


    @Test
    public void fromProperties() throws Exception {
        dbMaintainManager.returns(asList(databaseConnection1.getMock(), databaseConnection2.getMock())).getDatabaseConnections();
        dbMaintainManager.returns(databaseConnection1).getDatabaseConnection("database1");
        dbMaintainManager.returns(databaseConnection2).getDatabaseConnection("database2");

        List<String> result = unitilsDataSourceManager.getDatabaseNames(null);
        assertLenientEquals(asList("database1", "database2"), result);
    }

    @Test
    public void fromApplicationContext() throws Exception {
        registerSpringBean("databaseA", unitilsDataSource);
        registerSpringBean("databaseB", unitilsDataSource);

        List<String> result = unitilsDataSourceManager.getDatabaseNames(staticApplicationContext);
        assertLenientEquals(asList("databaseA", "databaseB"), result);
    }


    private void registerSpringBean(String name, Object bean) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, bean);
    }
}
