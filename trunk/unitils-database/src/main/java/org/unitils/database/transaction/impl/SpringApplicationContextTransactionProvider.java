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
package org.unitils.database.transaction.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.UnitilsException;
import org.unitils.database.transaction.TransactionProvider;
import org.unitilsnew.core.spring.SpringTestManager;

import javax.sql.DataSource;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class SpringApplicationContextTransactionProvider implements TransactionProvider {

    protected SpringTestManager springTestManager;
    protected DefaultTransactionProvider defaultTransactionProvider;


    public SpringApplicationContextTransactionProvider(SpringTestManager springTestManager, DefaultTransactionProvider defaultTransactionProvider) {
        this.springTestManager = springTestManager;
        this.defaultTransactionProvider = defaultTransactionProvider;
    }


    public synchronized PlatformTransactionManager getPlatformTransactionManager(String transactionManagerName, DataSource dataSource) {
        ApplicationContext applicationContext = springTestManager.getApplicationContext();
        if (applicationContext == null) {
            throw new UnitilsException("Unable to get platform transaction manager from application context. No test application context found.");
        }

        PlatformTransactionManager platformTransactionManager = getPlatformTransactionManagerFromApplicationContext(transactionManagerName, applicationContext);
        if (platformTransactionManager != null) {
            return platformTransactionManager;
        }
        return defaultTransactionProvider.getPlatformTransactionManager(transactionManagerName, dataSource);
    }


    @SuppressWarnings("unchecked")
    protected PlatformTransactionManager getPlatformTransactionManagerFromApplicationContext(String transactionManagerName, ApplicationContext applicationContext) {
        Map<String, PlatformTransactionManager> platformTransactionManagers = applicationContext.getBeansOfType(PlatformTransactionManager.class);
        if (platformTransactionManagers.isEmpty()) {
            return null;
        }
        if (isBlank(transactionManagerName)) {
            if (platformTransactionManagers.size() > 1) {
                throw new UnitilsException("Unable to get default platform transaction manager from application context. More than one bean of type PlatformTransactionManager found in application context. Please specify the id of the transaction manager explicitly.");
            }
            return platformTransactionManagers.values().iterator().next();
        }
        PlatformTransactionManager platformTransactionManager = platformTransactionManagers.get(transactionManagerName);
        if (platformTransactionManager == null) {
            throw new UnitilsException("Unable to get platform transaction manager from application context. No bean of type PlatformTransactionManager with id '" + transactionManagerName + "' found in application context.");
        }
        return platformTransactionManager;
    }
}
