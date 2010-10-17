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
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;
import org.unitils.database.datasource.impl.DefaultDataSourceFactory;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.database.transaction.UnitilsDatabaseManager.PROPERTY_UPDATEDATABASESCHEMA_ENABLED;
import static org.unitils.database.transaction.UnitilsDatabaseManager.PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManagerGetDataSourceFromApplicationContext extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDatabaseManager unitilsDatabaseManager;

    @Dummy
    protected DataSource dataSource1;
    @Dummy
    protected DataSource dataSource2;

    private StaticApplicationContext staticApplicationContext;

    @Before
    public void initialize() {
        Properties configuration = new Properties();
        configuration.setProperty(PROPERTY_UPDATEDATABASESCHEMA_ENABLED, "false");
        configuration.setProperty(PROPERTY_WRAP_DATASOURCE_IN_TRANSACTIONAL_PROXY, "true");

        staticApplicationContext = new StaticApplicationContext();

        unitilsDatabaseManager = new UnitilsDatabaseManager(configuration, new DefaultDataSourceFactory());
    }


    @Test
    public void withDatabaseName() throws Exception {
        registerSpringBean("1", new UnitilsDataSource("database1", dataSource1));
        registerSpringBean("2", new UnitilsDataSource("database2", dataSource2));

        DataSource dataSource = unitilsDatabaseManager.getDataSource("database1", staticApplicationContext);
        assertSame(dataSource1, ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource());
    }

    @Test
    public void noDatabaseNameIsDefaultDataSource() throws Exception {
        registerSpringBean("default", new UnitilsDataSource(dataSource1));
        registerSpringBean("database2", new UnitilsDataSource("database2", dataSource2));

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, staticApplicationContext);
        assertSame(dataSource1, ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource());
    }

    @Test
    public void moreThanOneDefaultDataSourceConfigured() throws Exception {
        registerSpringBean("default", new UnitilsDataSource(dataSource1));
        registerSpringBean("other default", new UnitilsDataSource(dataSource2));

        try {
            unitilsDatabaseManager.getDataSource(null, staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get default unitils data source from test application context."));
        }
    }

    @Test
    public void moreThanOneNamedDataSourceConfigured() throws Exception {
        registerSpringBean("dataSource1", new UnitilsDataSource("dataSource1", dataSource1));
        registerSpringBean("other dataSource1", new UnitilsDataSource("dataSource1", dataSource2));

        try {
            unitilsDatabaseManager.getDataSource("dataSource1", staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get unitils data source for database name dataSource1 from test application context."));
        }
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            unitilsDatabaseManager.getDataSource("xxxx", staticApplicationContext);
            fail("DatabaseException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get unitils data source for database name xxxx from test application context."));
        }
    }

    @Test
    public void unknownDefaultDatabase() throws Exception {
        try {
            unitilsDatabaseManager.getDataSourceFromApplicationContext(null, staticApplicationContext);
            fail("DatabaseException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get default unitils data source from test application context."));
        }
    }


    private void registerSpringBean(String name, Object bean) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, bean);
    }
}
