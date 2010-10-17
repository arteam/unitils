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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.database.SQLUnitils.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTransactionManagerSpringTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTransactionManager unitilsTransactionManager = new UnitilsTransactionManager();

    @TestDataSource
    protected DataSource dataSource;

    private StaticApplicationContext staticApplicationContext;
    private PlatformTransactionManager platformTransactionManager;


    @Before
    public void initialize() {
        this.staticApplicationContext = new StaticApplicationContext();
        this.platformTransactionManager = new DataSourceTransactionManager(dataSource);
    }

    @Before
    public void createTestTable() {
        dropTestTable();
        executeUpdate("create table test (val integer)", dataSource);
    }

    @After
    public void dropTestTable() {
        executeUpdateQuietly("drop table test", dataSource);
    }


    @Test
    public void commitTransaction() {
        registerSpringBean("transactionManager", platformTransactionManager);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager"), staticApplicationContext);
        performDatabaseUpdate();
        unitilsTransactionManager.commit(this);

        assertDatabaseUpdateCommitted();
    }

    @Test
    public void rollbackTransaction() {
        registerSpringBean("transactionManager", platformTransactionManager);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager"), staticApplicationContext);
        performDatabaseUpdate();
        unitilsTransactionManager.rollback(this);

        assertDatabaseUpdateRolledBack();
    }

    @Test
    public void moreThanOnePlatFormTransactionManagerDefined() {
        registerSpringBean("transactionManager1", platformTransactionManager);
        registerSpringBean("transactionManager2", platformTransactionManager);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager1", "transactionManager2"), staticApplicationContext);
        performDatabaseUpdate();
        unitilsTransactionManager.commit(this);

        assertDatabaseUpdateCommitted();
    }

    @Test
    public void unknownBeanName() {
        try {
            unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("xxxx"), staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get transaction manager for name xxxx from test application context"));
        }
    }

    @Test
    public void defaultTransactionManager() {
        registerSpringBean("transactionManager1", platformTransactionManager);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
        performDatabaseUpdate();
        unitilsTransactionManager.commit(this);

        assertDatabaseUpdateCommitted();
    }

    @Test
    public void doNothingIfNoDefaultTransactionManagerFound() {
        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
        try {
            unitilsTransactionManager.commit(this);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Trying to commit while no transaction is currently active."));
        }
    }

    @Test
    public void defaultTransactionManagerButMoreThanOneTransactionManagerFound() {
        registerSpringBean("transactionManager1", platformTransactionManager);
        registerSpringBean("transactionManager2", platformTransactionManager);

        try {
            unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get default transaction manager from test application context."));
        }
    }


    private void performDatabaseUpdate() {
        executeUpdate("insert into test(val) values(1)", dataSource);
    }

    private void assertDatabaseUpdateCommitted() {
        assertEquals(1, getItemAsLong("select count(*) from test", dataSource));
    }

    private void assertDatabaseUpdateRolledBack() {
        assertEquals(0, getItemAsLong("select count(*) from test", dataSource));
    }


    private void registerSpringBean(String name, Object bean) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, bean);
    }

}
