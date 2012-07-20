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
import org.springframework.context.ApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSourceBean;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.spring.SpringTestManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class SpringApplicationContextDataSourceProviderGetDataSourceWrapperTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringApplicationContextDataSourceProvider springApplicationContextDataSourceProvider;

    private Mock<SpringTestManager> springTestManagerMock;
    private Mock<ApplicationContext> applicationContextMock;
    @Dummy
    private DataSource dataSource;

    private UnitilsDataSourceBean unitilsDataSourceBean1;
    private UnitilsDataSourceBean unitilsDataSourceBean2;

    private Map<String, UnitilsDataSourceBean> unitilsDataSourceBeans = new HashMap<String, UnitilsDataSourceBean>();

    @Before
    public void initialize() {
        springApplicationContextDataSourceProvider = new SpringApplicationContextDataSourceProvider(springTestManagerMock.getMock());

        springTestManagerMock.returns(applicationContextMock).getApplicationContext();

        unitilsDataSourceBean1 = new UnitilsDataSourceBean();
        unitilsDataSourceBean1.setDataSource(dataSource);
        unitilsDataSourceBean2 = new UnitilsDataSourceBean();
        unitilsDataSourceBean2.setDataSource(dataSource);

        applicationContextMock.returns("1").getId();
        applicationContextMock.returns(unitilsDataSourceBeans).getBeansOfType(UnitilsDataSourceBean.class);
    }


    @Test
    public void getDataSourceWrapper() throws Exception {
        unitilsDataSourceBean1.setDialect("dialect");
        unitilsDataSourceBean1.setSchemaNames(asList("schema1", "schema2"));
        unitilsDataSourceBean1.setUpdateEnabled(true);
        unitilsDataSourceBean1.setDefaultDatabase(true);
        unitilsDataSourceBeans.put("name", unitilsDataSourceBean1);

        DataSourceWrapper result = springApplicationContextDataSourceProvider.getDataSourceWrapper("name");

        assertSame(dataSource, result.getWrappedDataSource());

        DatabaseConfiguration databaseConfiguration = result.getDatabaseConfiguration();
        assertEquals("name", databaseConfiguration.getDatabaseName());
        assertEquals("dialect", databaseConfiguration.getDialect());
        assertNull(databaseConfiguration.getDriverClassName());
        assertNull(databaseConfiguration.getUserName());
        assertNull(databaseConfiguration.getPassword());
        assertNull(databaseConfiguration.getUrl());
        assertEquals("schema1", databaseConfiguration.getDefaultSchemaName());
        assertEquals(asList("schema1", "schema2"), databaseConfiguration.getSchemaNames());
        assertTrue(databaseConfiguration.isUpdateDisabled());
        assertTrue(databaseConfiguration.isDefaultDatabase());
    }

    @Test
    public void emptyUnitilsDataSourceBean() throws Exception {
        unitilsDataSourceBeans.put("name", unitilsDataSourceBean1);

        DataSourceWrapper result = springApplicationContextDataSourceProvider.getDataSourceWrapper("name");

        assertSame(dataSource, result.getWrappedDataSource());

        DatabaseConfiguration databaseConfiguration = result.getDatabaseConfiguration();
        assertEquals("name", databaseConfiguration.getDatabaseName());
        assertNull(databaseConfiguration.getDialect());
        assertNull(databaseConfiguration.getDriverClassName());
        assertNull(databaseConfiguration.getUserName());
        assertNull(databaseConfiguration.getPassword());
        assertNull(databaseConfiguration.getUrl());
        assertNull(databaseConfiguration.getDefaultSchemaName());
        assertTrue(databaseConfiguration.getSchemaNames().isEmpty());
        assertFalse(databaseConfiguration.isUpdateDisabled());
        assertTrue(databaseConfiguration.isDefaultDatabase());
    }

    @Test
    public void defaultDatabaseWhenNullName() throws Exception {
        unitilsDataSourceBean1.setDefaultDatabase(false);
        unitilsDataSourceBean2.setDefaultDatabase(true);
        unitilsDataSourceBeans.put("name 1", unitilsDataSourceBean1);
        unitilsDataSourceBeans.put("name 2", unitilsDataSourceBean2);

        DataSourceWrapper result = springApplicationContextDataSourceProvider.getDataSourceWrapper(null);

        assertEquals("name 2", result.getDatabaseConfiguration().getDatabaseName());
        assertTrue(result.getDatabaseConfiguration().isDefaultDatabase());
    }

    @Test
    public void defaultDatabaseWhenBlankName() throws Exception {
        unitilsDataSourceBean1.setDefaultDatabase(false);
        unitilsDataSourceBean2.setDefaultDatabase(true);
        unitilsDataSourceBeans.put("name 1", unitilsDataSourceBean1);
        unitilsDataSourceBeans.put("name 2", unitilsDataSourceBean2);

        DataSourceWrapper result = springApplicationContextDataSourceProvider.getDataSourceWrapper("");

        assertEquals("name 2", result.getDatabaseConfiguration().getDatabaseName());
        assertTrue(result.getDatabaseConfiguration().isDefaultDatabase());
    }

    @Test
    public void alwaysDefaultDatabaseWhenThereIsOnlyOneUnitilsDataSourceBean() throws Exception {
        unitilsDataSourceBean1.setDefaultDatabase(false);
        unitilsDataSourceBeans.put("name", unitilsDataSourceBean1);

        DataSourceWrapper result = springApplicationContextDataSourceProvider.getDataSourceWrapper(null);

        assertEquals("name", result.getDatabaseConfiguration().getDatabaseName());
        assertTrue(result.getDatabaseConfiguration().isDefaultDatabase());
    }

    @Test
    public void exceptionWhenNullNameButNoDefaultDatabase() throws Exception {
        unitilsDataSourceBean1.setDefaultDatabase(false);
        unitilsDataSourceBean2.setDefaultDatabase(false);
        unitilsDataSourceBeans.put("name 1", unitilsDataSourceBean1);
        unitilsDataSourceBeans.put("name 2", unitilsDataSourceBean2);
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get default data source from application context.\n" +
                    "Reason: Unable to determine default database. More than one bean of type UnitilsDataSourceBean found in test application context. Please mark one of these beans as default database by setting its defaultDatabase property to true.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullNameAndMoreThanOneDefaultDatabase() throws Exception {
        unitilsDataSourceBean1.setDefaultDatabase(true);
        unitilsDataSourceBean2.setDefaultDatabase(true);
        unitilsDataSourceBeans.put("name 1", unitilsDataSourceBean1);
        unitilsDataSourceBeans.put("name 2", unitilsDataSourceBean2);
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get default data source from application context.\n" +
                    "Reason: Unable to determine default database. More than one bean of type UnitilsDataSourceBean found in test application context that is marked as default database. Only one of these beans can have the defaultDatabase property set to true.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        unitilsDataSourceBeans.put("name", unitilsDataSourceBean1);
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get data source for database name 'xxx' from application context.\n" +
                    "Reason: No bean with id 'xxx' of type UnitilsDataSourceBean found in test application context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoDataSource() throws Exception {
        unitilsDataSourceBean1.setDataSource(null);
        unitilsDataSourceBeans.put("name", unitilsDataSourceBean1);
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper("name");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get data source for database name 'name' from application context.\n" +
                    "Reason: No dataSource configured for UnitilsDataSourceBean.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoUnitilsDataSourceBeansFound() throws Exception {
        unitilsDataSourceBeans.clear();
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper("name");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get data source for database name 'name' from application context.\n" +
                    "Reason: No beans of type UnitilsDataSourceBean found in test application context.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoApplicationContextFound() {
        springTestManagerMock.onceReturns(null).getApplicationContext();
        try {
            springApplicationContextDataSourceProvider.getDataSourceWrapper("name");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get data source for database name 'name' from application context.\n" +
                    "Reason: No test application context found.", e.getMessage());
        }

    }
}
