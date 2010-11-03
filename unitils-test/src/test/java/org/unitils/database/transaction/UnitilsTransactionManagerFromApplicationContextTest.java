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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTransactionManagerFromApplicationContextTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTransactionManager unitilsTransactionManager = new UnitilsTransactionManager();

    private StaticApplicationContext staticApplicationContext;

    protected Mock<PlatformTransactionManager> platformTransactionManager1;
    protected Mock<PlatformTransactionManager> platformTransactionManager2;
    @Dummy
    protected TransactionStatus transactionStatus;


    @Before
    public void initialize() {
        this.staticApplicationContext = new StaticApplicationContext();
    }


    @Test
    public void startTransaction() {
        registerTransactionManager("transactionManager", platformTransactionManager1);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager"), staticApplicationContext);
        platformTransactionManager1.assertInvoked().getTransaction(new DefaultTransactionDefinition(PROPAGATION_REQUIRED));
    }


    @Test
    public void commitTransaction() {
        registerTransactionManager("transactionManager", platformTransactionManager1);
        platformTransactionManager1.returns(transactionStatus).getTransaction(null);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager"), staticApplicationContext);
        unitilsTransactionManager.commit(this);

        platformTransactionManager1.assertInvoked().commit(transactionStatus);
    }

    @Test
    public void rollbackTransaction() {
        registerTransactionManager("transactionManager", platformTransactionManager1);
        platformTransactionManager1.returns(transactionStatus).getTransaction(null);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager"), staticApplicationContext);
        unitilsTransactionManager.rollback(this);

        platformTransactionManager1.assertInvoked().rollback(transactionStatus);
    }

    @Test
    public void commitWhenMoreThanOnePlatFormTransactionManagerDefined() {
        registerTransactionManager("transactionManager1", platformTransactionManager1);
        registerTransactionManager("transactionManager2", platformTransactionManager2);
        platformTransactionManager1.returns(transactionStatus).getTransaction(null);
        platformTransactionManager2.returns(transactionStatus).getTransaction(null);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager1", "transactionManager2"), staticApplicationContext);
        unitilsTransactionManager.commit(this);

        platformTransactionManager1.assertInvoked().commit(transactionStatus);
        platformTransactionManager2.assertInvoked().commit(transactionStatus);
    }

    @Test
    public void rollbackWhenMoreThanOnePlatFormTransactionManagerDefined() {
        registerTransactionManager("transactionManager1", platformTransactionManager1);
        registerTransactionManager("transactionManager2", platformTransactionManager2);
        platformTransactionManager1.returns(transactionStatus).getTransaction(null);
        platformTransactionManager2.returns(transactionStatus).getTransaction(null);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, asList("transactionManager1", "transactionManager2"), staticApplicationContext);
        unitilsTransactionManager.rollback(this);

        platformTransactionManager1.assertInvoked().rollback(transactionStatus);
        platformTransactionManager2.assertInvoked().rollback(transactionStatus);
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
        registerTransactionManager("transactionManager1", platformTransactionManager1);

        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
        platformTransactionManager1.assertInvoked().getTransaction(null);
    }

    @Test
    public void doNothingIfNoDefaultTransactionManagerFound() {
        unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
        platformTransactionManager1.assertNotInvoked().getTransaction(null);
    }

    @Test
    public void defaultTransactionManagerButMoreThanOneTransactionManagerFound() {
        registerTransactionManager("transactionManager1", platformTransactionManager1);
        registerTransactionManager("transactionManager2", platformTransactionManager2);

        try {
            unitilsTransactionManager.startTransactionOnTransactionManagersInApplicationContext(this, new ArrayList<String>(), staticApplicationContext);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertTrue(e.getMessage().contains("Unable to get default transaction manager from test application context."));
        }
    }


    private void registerTransactionManager(String name, Mock<PlatformTransactionManager> transactionManager) {
        staticApplicationContext.getBeanFactory().registerSingleton(name, transactionManager.getMock());
    }

}
