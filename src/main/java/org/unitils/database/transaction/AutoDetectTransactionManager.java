/*
 * Copyright 2006 the original author or authors.
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

import org.unitils.core.Unitils;
import org.unitils.util.ReflectionUtils;

/**
 * Transaction manager that automatically detects which transaction management implementation 
 * should be used. If a spring ApplicationContext is configured for a testObject and a 
 * <code>PlatformTransactionManager</code> is configured in this application context, the 
 * {@link org.unitils.database.transaction.SpringIntegratingTransactionManager}
 * 
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AutoDetectTransactionManager implements TransactionManager {

    TransactionManager springIntegratingTransactionManager;

    TransactionManager simpleTransactionManager;

    public AutoDetectTransactionManager() {
        if (isSpringModuleEnabled()) {
            springIntegratingTransactionManager = ReflectionUtils.createInstanceOfType(
                    "org.unitils.database.transaction.SpringIntegratingTransactionManager");
        }
        simpleTransactionManager = new SimpleTransactionManager();
        
    }

    public void startTransaction(Object testObject) {
        if (isSpringIntegratingTransactionManagerActive(testObject)) {
            springIntegratingTransactionManager.startTransaction(testObject);
        } else {
            simpleTransactionManager.startTransaction(testObject);
        }
    }

    public void commit(Object testObject) {
        if (isSpringIntegratingTransactionManagerActive(testObject)) {
            springIntegratingTransactionManager.commit(testObject);
        } else {
            simpleTransactionManager.commit(testObject);
        }
    }

    public void rollback(Object testObject) {
        if (isSpringIntegratingTransactionManagerActive(testObject)) {
            springIntegratingTransactionManager.rollback(testObject);
        } else {
            simpleTransactionManager.rollback(testObject);
        }
    }

    public boolean isActive(Object testObject) {
        return springIntegratingTransactionManager.isActive(testObject) || simpleTransactionManager.isActive(testObject);
    }

    /**
     * @return Whether the spring module is enabled
     */
    protected boolean isSpringModuleEnabled() {
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }

    /**
     * @param testObject The test object, not null
     * @return Whether the springIntegratingTransactionManager is configured and active, i.e. able to manage transactions
     */
    protected boolean isSpringIntegratingTransactionManagerActive(Object testObject) {
        return springIntegratingTransactionManager != null && springIntegratingTransactionManager.isActive(testObject);
    }
}
