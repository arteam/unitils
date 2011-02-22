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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Ignore
public class UnitilsDataSourceManagerGetDataSourceFromApplicationContext extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDataSourceManager unitilsDataSourceManager;

    @Dummy
    protected DataSource dataSource;
    @Dummy
    protected TransactionAwareDataSourceProxy transactionAwareDataSourceProxy;

    protected UnitilsDataSource unitilsDataSource1;
    protected UnitilsDataSource unitilsDataSource2;


    private StaticApplicationContext staticApplicationContext;

    @Before
    public void initialize() {
        unitilsDataSource1 = new UnitilsDataSource(dataSource, "schema");
        unitilsDataSource2 = new UnitilsDataSource(transactionAwareDataSourceProxy, "schema");

        unitilsDataSourceManager = new UnitilsDataSourceManager(false, null);
        staticApplicationContext = new StaticApplicationContext();
    }


    @Test
    public void withDatabaseName() throws Exception {
        registerSpringBean("database1", unitilsDataSource1);
        registerSpringBean("database2", unitilsDataSource2);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource("database1", staticApplicationContext);
        assertSame(unitilsDataSource1, result);
    }

    @Test
    public void defaultDataSourceIfOnlyOneDataSourceDefined() throws Exception {
        registerSpringBean("name", unitilsDataSource1);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        assertSame(unitilsDataSource1, result);
    }

    @Test
    public void defaultDataSourceWhenMultipleDataSourcesDefined() throws Exception {
        registerSpringBean("database1", unitilsDataSource1);
        registerSpringBean("database2", unitilsDataSource2);

        try {
            unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine default unitils data source: more than one bean of type UnitilsDataSource found in test application context. Please specify the id or name of the bean.", e.getMessage());
        }
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            unitilsDataSourceManager.getUnitilsDataSource("xxxx", staticApplicationContext);
            fail("NoSuchBeanDefinitionException expected");
        } catch (NoSuchBeanDefinitionException e) {
            assertEquals("No bean named 'xxxx' is defined", e.getMessage());
        }
    }

    @Test
    public void unknownDefaultDatabase() throws Exception {
        try {
            unitilsDataSourceManager.getUnitilsDataSourceFromApplicationContext(null, staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine default unitils data source: no bean of type UnitilsDataSource found in test application context.", e.getMessage());
        }
    }

    @Test
    public void dataSourceWrappedInTransactionAwareProxy() throws Exception {
        registerSpringBean("name", unitilsDataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        assertTrue(result.getDataSource() instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void disableWrappingInTransactionAwareProxy() throws Exception {
        registerSpringBean("name", unitilsDataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(false, null);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        assertFalse(result.getDataSource() instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void sameDataSourceReturned() throws Exception {
        registerSpringBean("name", unitilsDataSource1);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        UnitilsDataSource result1 = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        UnitilsDataSource result2 = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        assertSame(result1, result2);
    }

    @Test
    public void doNotWrapIfAlreadyTransactionAwareDataSourceProxy() throws Exception {
        registerSpringBean("name", unitilsDataSource2);
        unitilsDataSourceManager = new UnitilsDataSourceManager(true, null);

        UnitilsDataSource result = unitilsDataSourceManager.getUnitilsDataSource(null, staticApplicationContext);
        assertSame(transactionAwareDataSourceProxy, result.getDataSource());
    }

    private void registerSpringBean(String name, Object bean) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, bean);
    }
}
