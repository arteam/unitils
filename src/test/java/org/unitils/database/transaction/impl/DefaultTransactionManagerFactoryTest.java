/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.database.transaction.impl;

import org.unitils.core.ConfigurationLoader;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.database.DatabaseModuleTransactionalTest;
import org.unitils.database.transaction.TransactionManager;
import static org.unitils.database.transaction.impl.DefaultTransactionManagerFactory.PROPKEY_TRANSACTION_MANAGER_TYPE;

import java.util.Properties;

/**
 * Tests for the {@link DefaultTransactionManagerFactory}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultTransactionManagerFactoryTest extends DatabaseModuleTransactionalTest {

    /* Object under test */
    private DefaultTransactionManagerFactory defaultTransactionManagerFactory;

    /* The unitils configuration */
    protected Properties configuration;


    /**
     * Initializes the test fixture.
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        defaultTransactionManagerFactory = new DefaultTransactionManagerFactory();
        configuration = new ConfigurationLoader().loadConfiguration();
    }


    /**
     * Cleans up test by resetting the unitils instance.
     */
    @Override
    public void tearDown() throws Exception {
        Unitils.getInstance().init();
    }


    /**
     * Tests for creating a simple transaction manager.
     */
    public void testCreateTransactionManager_simple() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "simple");
        defaultTransactionManagerFactory.init(configuration);

        TransactionManager result = defaultTransactionManagerFactory.createTransactionManager();

        assertTrue(result instanceof SimpleTransactionManager);
    }


    /**
     * Tests for creating a spring transaction manager.
     */
    public void testCreateTransactionManager_spring() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "spring");
        defaultTransactionManagerFactory.init(configuration);

        TransactionManager result = defaultTransactionManagerFactory.createTransactionManager();

        assertTrue(result instanceof SpringTransactionManager);
    }


    /**
     * Tests for creating a spring transaction manager using the auto-detect. The spring module is enabled so
     * the spring transaction manager should be created.
     */
    public void testCreateTransactionManager_autoSpringEnabled() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "auto");
        configuration.setProperty("unitils.module.spring.enabled", "true");
        Unitils.getInstance().init(configuration);
        defaultTransactionManagerFactory.init(configuration);

        TransactionManager result = defaultTransactionManagerFactory.createTransactionManager();

        assertTrue(result instanceof SpringTransactionManager);
    }


    /**
     * Tests for creating a simple transaction manager using the auto-detect. The spring module is not enabled so
     * the simple transaction manager should be created.
     */
    public void testCreateTransactionManager_autoSpringDisabled() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "auto");
        configuration.setProperty("unitils.module.spring.enabled", "false");
        Unitils.getInstance().init(configuration);
        defaultTransactionManagerFactory.init(configuration);

        TransactionManager result = defaultTransactionManagerFactory.createTransactionManager();

        assertTrue(result instanceof SimpleTransactionManager);
    }


    /**
     * Tests for creating a transaction manager for an unknown type.
     */
    public void testCreateTransactionManager_unknowType() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "xxxx");
        try {
            defaultTransactionManagerFactory.init(configuration);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Tests for creating a transaction manager for an unknown type of transaction manager.
     */
    public void testCreateTransactionManager_unknowTransactionManagerClassName() throws Exception {
        configuration.setProperty(PROPKEY_TRANSACTION_MANAGER_TYPE, "simple");
        configuration.setProperty(TransactionManager.class.getName() + ".implClassName.simple", "xxxx");
        defaultTransactionManagerFactory.init(configuration);
        try {
            defaultTransactionManagerFactory.createTransactionManager();
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }
}
