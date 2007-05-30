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
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.Properties;

/**
 * todo javadoc
 * <p/>
 * Transaction manager that automatically detects which transaction management implementation
 * should be used. If a spring ApplicationContext is configured for a testObject and a
 * <code>PlatformTransactionManager</code> is configured in this application context, the
 * {@link SpringTransactionManager}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultTransactionManagerFactory implements TransactionManagerFactory {

    /* Propery key of the database driver class name */
    private static final String PROPKEY_TRANSACTION_MANAGER_TYPE = "transactionManager.type";

    // todo javadoc
    private String transactionManagerClassName;


    private Properties configuration;


    //todo javadoc
    public void init(Properties configuration) {

        //todo implement get class name using config utils               
    }


    //todo make configurable
    public TransactionManager createTransactionManager() {
        if (isSpringModuleEnabled()) {
            return createInstanceOfType("org.unitils.database.transaction.SpringTransactionManager");
        }
        return new SimpleTransactionManager();
    }


    /**
     * @return Whether the spring module is enabled
     */
    protected boolean isSpringModuleEnabled() {
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }


}
