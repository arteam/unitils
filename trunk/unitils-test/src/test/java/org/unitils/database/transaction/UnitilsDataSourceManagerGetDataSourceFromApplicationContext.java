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
package org.unitils.database.transaction;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceManagerGetDataSourceFromApplicationContext extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDataSourceManager unitilsDataSourceManager;

    @Dummy
    protected DataSource dataSource1;
    @Dummy
    protected DataSource dataSource2;
    @Dummy
    protected TransactionAwareDataSourceProxy transactionAwareDataSourceProxy;

    private StaticApplicationContext staticApplicationContext;

    @Before
    public void initialize() {
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, null);
        staticApplicationContext = new StaticApplicationContext();
    }


    @Test
    public void withDatabaseName() throws Exception {
        registerSpringBean("database1", dataSource1);
        registerSpringBean("database2", dataSource2);

        DataSource result = unitilsDataSourceManager.getDataSource("database1", staticApplicationContext);
        assertSame(dataSource1, result);
    }

    @Test
    public void defaultDataSourceIfOnlyOneDataSourceDefined() throws Exception {
        registerSpringBean("name", dataSource1);

        DataSource result = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        assertSame(dataSource1, result);
    }

    @Test
    public void defaultDataSourceWhenMultipleDataSourcesDefined() throws Exception {
        registerSpringBean("database1", dataSource1);
        registerSpringBean("database2", dataSource2);

        try {
            unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
            fail("NoSuchBeanDefinitionException expected");
        } catch (NoSuchBeanDefinitionException e) {
            assertEquals("No unique bean of type [javax.sql.DataSource] is defined: expected single bean but found 2: database1,database2", e.getMessage());
        }
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            unitilsDataSourceManager.getDataSource("xxxx", staticApplicationContext);
            fail("NoSuchBeanDefinitionException expected");
        } catch (NoSuchBeanDefinitionException e) {
            assertEquals("No bean named 'xxxx' is defined", e.getMessage());
        }
    }

    @Test
    public void unknownDefaultDatabase() throws Exception {
        try {
            unitilsDataSourceManager.getDataSourceFromApplicationContext(null, staticApplicationContext);
            fail("NoSuchBeanDefinitionException expected");
        } catch (NoSuchBeanDefinitionException e) {
            assertEquals("No unique bean of type [javax.sql.DataSource] is defined: expected single bean but found 0: ", e.getMessage());
        }
    }

    @Test
    public void dataSourceWrappedInTransactionAwareProxy() throws Exception {
        registerSpringBean("name", dataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        DataSource dataSource = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        assertTrue(dataSource instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void disableWrappingInTransactionAwareProxy() throws Exception {
        registerSpringBean("name", dataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, null);

        DataSource dataSource = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        assertFalse(dataSource instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void sameWrappedDataSourceReturned() throws Exception {
        registerSpringBean("name", dataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        DataSource dataSource1 = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        DataSource dataSource2 = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        assertSame(dataSource1, dataSource2);
    }

    @Test
    public void doNotWrapIfAlreadyTransactionAwareDataSourceProxy() throws Exception {
        registerSpringBean("name", transactionAwareDataSourceProxy);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        DataSource dataSource = unitilsDataSourceManager.getDataSource(null, staticApplicationContext);
        assertSame(transactionAwareDataSourceProxy, dataSource);
    }

    private void registerSpringBean(String name, Object bean) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, bean);
    }
}
