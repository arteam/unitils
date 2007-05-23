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

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.spring.SpringModule;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringIntegratingTransactionManager implements TransactionManager {

    private DataSource dataSource;
    private ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();
    private PlatformTransactionManager defaultSpringTransactionManager;

    public SpringIntegratingTransactionManager() {
        getSpringModule().registerBeanPostProcessorType(SpringTransactionManagerInterceptingBeanPostProcessor.class);    
    }

    public void startTransaction(Object testObject) {
        PlatformTransactionManager springConfiguredTransactionManager = getSpringTransactionManager(testObject);
        if (springConfiguredTransactionManager == null) {
            throw new UnitilsException("No PlatformTransactionManager has been configured in a spring ApplicationContext " +
                    "associated with test class " + testObject.getClass());
        }
        TransactionStatus transactionStatus = springConfiguredTransactionManager.getTransaction(
                getTransactionDefinition(testObject));
        transactionStatusHolder.set(transactionStatus);
    }

    public void commit(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to commit, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).commit(transactionStatus);
        transactionStatusHolder.remove();
    }

    public void rollback(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to rollback, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).rollback(transactionStatus);
        transactionStatusHolder.remove();
    }

    public DataSource registerDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return dataSource;
    }

    /**
     * Returns a <code>TransactionDefinition</code> object containing the necessary transaction parameters. Simply
     * returns a default <code>DefaultTransactionDefinition</code> object without specifying any custom properties on
     * it.
     *
     * @param testObject The test instance
     * @return the TransactionDefinition
     */
    protected TransactionDefinition getTransactionDefinition(Object testObject) {
        return new DefaultTransactionDefinition();
    }

    protected PlatformTransactionManager getSpringTransactionManager(Object testObject) {
        PlatformTransactionManager transactionManager = getSpringConfiguredTransactionManager(testObject);
        if (transactionManager == null) {
            transactionManager = getDefaultSpringTransactionManager(testObject);
        }
        return transactionManager;
    }

    protected PlatformTransactionManager getSpringConfiguredTransactionManager(Object testObject) {
        SpringTransactionManagerInterceptingBeanPostProcessor beanPostProcessor = getSpringModule().getBeanPostProcessor(
                testObject, SpringTransactionManagerInterceptingBeanPostProcessor.class);
        if (beanPostProcessor != null && beanPostProcessor.getSpringTransactionManager() != null) {
            return beanPostProcessor.getSpringTransactionManager();
        }
        return getDefaultSpringTransactionManager(testObject);
    }

    protected PlatformTransactionManager getDefaultSpringTransactionManager(Object testObject) {
        if (defaultSpringTransactionManager == null) {
            defaultSpringTransactionManager = new DataSourceTransactionManager(dataSource);
        }
        return defaultSpringTransactionManager;
    }

    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

    public static class SpringTransactionManagerInterceptingBeanPostProcessor implements BeanPostProcessor {

        private PlatformTransactionManager springTransactionManager;

        /**
         * Simply passes through all beans before they are initialized.
         *
         * @param bean     The new bean instance
         * @param beanName The name of the bean
         * @return The given bean
         */
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        /**
         * Intercepts every instance of <code>PlatformTransactionManager</code>.
         * 
         * @param bean     The new bean instance
         * @param beanName The name of the bean
         * @return The post processed bean. Is in all cases equal to the given bean parameter
         * @throws BeansException
         */
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof PlatformTransactionManager) {
                if (springTransactionManager != null) {
                    throw new UnitilsException("More than one PlatformTransactionManager is configured in the spring " +
                            "configuration. This is not supported in Unitils");
                }
                springTransactionManager = (PlatformTransactionManager) bean;
            }
            return bean;
        }


        protected PlatformTransactionManager getSpringTransactionManager() {
            return springTransactionManager;
        }
    }

}
