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
package org.unitils.database.transaction.impl;

import org.unitils.core.Unitils;
import org.unitils.database.transaction.TransactionManager;
import org.unitils.database.transaction.TransactionManagerFactory;
import static org.unitils.util.ConfigUtils.getConfiguredClassName;
import org.unitils.util.PropertyUtils;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.Properties;

/**
 * Transaction manager factory implementation that will load a transaction manager determined by the {#PROPKEY_TRANSACTION_MANAGER_TYPE}
 * property value. If this property is set to 'spring', the spring transaction manager is created, if it is set to 'simple', the
 * simple transaction manager is created.
 * <p/>
 * The type can also be set to 'auto', the factory will first see whether the SpringModule is enabled. If so, the spring
 * transaction mananager is created, otherwise the simple transaction manager will be created.
 * <p/>
 * The actual implentation of the different transaction managers is determined by the 'TransactionManager class name' + type value.
 * E.g. org.unitils.database.transaction.TransactionManager.implClassName.simple for the simple transaction manager.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DefaultTransactionManagerFactory implements TransactionManagerFactory {

    /* Propery key of the database driver class name */
    public static final String PROPKEY_TRANSACTION_MANAGER_TYPE = "transactionManager.type";

    /* The class name of the configured concrete transaction manager implementation */
    private String transactionManagerClassName;


    /**
     * Initializes the factory by looking up the concrete transaction manager implementation as described in the
     * class javadoc.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        String type = PropertyUtils.getString(PROPKEY_TRANSACTION_MANAGER_TYPE, configuration);
        if ("auto".equals(type)) {
            if (isSpringModuleEnabled()) {
                type = "spring";
            } else {
                type = "simple";
            }
        }
        transactionManagerClassName = getConfiguredClassName(TransactionManager.class, configuration, type);
    }


    /**
     * Creates a new {@link TransactionManager}
     *
     * @return The TransactionManager, not null
     */
    public TransactionManager createTransactionManager() {
        return createInstanceOfType(transactionManagerClassName);
    }


    /**
     * @return True if the spring module is loaded and enabled
     */
    protected boolean isSpringModuleEnabled() {
        return Unitils.getInstance().getModulesRepository().isModuleEnabled("org.unitils.spring.SpringModule");
    }


}
