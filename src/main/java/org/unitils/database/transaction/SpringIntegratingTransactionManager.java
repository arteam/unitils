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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.spring.SpringModule;

/**
 * Transaction manager that relies on Spring transaction management. When starting a Transaction, this transaction
 * manager tries to locate a configured <code>org.springframework.transaction.PlatformTransactionManager</code> bean
 * instance in the spring ApplicationContext configured in the {@link SpringModule} for this testObject. If no such
 * bean was configured for this test, a <code>org.springframework.jdbc.datasource.DataSourceTransactionManager</code>
 * instance is created.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SpringIntegratingTransactionManager implements TransactionManager {

    /**
     * ThreadLocal for holding the TransactionStatus as used by spring's transaction management
     */
    private ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();

    
    /**
     * Constructs a new instance. Registers a SpringTransactionManagerInterceptingBeanPostProcessor with the 
     * spring module so that any PlatformTransactionManager configured in a spring application context is returned.
     */
    public SpringIntegratingTransactionManager() {
        getSpringModule().registerBeanPostProcessorType(SpringTransactionManagerInterceptingBeanPostProcessor.class);    
    }

    
    /**
     * Starts the transaction. Will start a transaction on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     * 
     * @param The test object, not null
     */
    public void startTransaction(Object testObject) {
        PlatformTransactionManager springTransactionManager = getSpringTransactionManager(testObject);
        TransactionStatus transactionStatus = springTransactionManager.getTransaction(
                getTransactionDefinition(testObject));
        transactionStatusHolder.set(transactionStatus);
    }

    
    /**
     * Commits the transaction. Will commit on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     * 
     * @param The test object, not null
     */
    public void commit(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to commit, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).commit(transactionStatus);
        transactionStatusHolder.remove();
    }

    
    /**
     * Rolls back the transaction. Will rollback on the PlatformTransactionManager that is configured
     * in the spring application context associated with the given testObject.
     * 
     * @param The test object, not null
     */
    public void rollback(Object testObject) {
        TransactionStatus transactionStatus = transactionStatusHolder.get();
        if (transactionStatus == null) {
            throw new UnitilsException("Trying to rollback, while no transaction is currently active");
        }
        getSpringTransactionManager(testObject).rollback(transactionStatus);
        transactionStatusHolder.remove();
    }

    
    /**
     * Returns true if a spring PlatformTransactionManager is configured
     * in the spring application context associated with the given testObject.
     * 
     * @param The test object, not null
     */
    public boolean isActive(Object testObject) {
        return getSpringConfiguredTransactionManager(testObject) != null;
    }

    
    /**
     * Returns a <code>TransactionDefinition</code> object containing the necessary transaction parameters. Simply
     * returns a default <code>DefaultTransactionDefinition</code> object without specifying any custom properties on
     * it.
     *
     * @param testObject The test object, not null
     * @return The default TransactionDefinition
     */
    protected TransactionDefinition getTransactionDefinition(Object testObject) {
        return new DefaultTransactionDefinition();
    }

    
    /**
     * @param testObject The test object, not null
     * @return The <code>PlatformTransactionManager</code> that is configured in the spring application 
     *         context associated with the given testObject.
     * @throws UnitilsException If no <code>PlatformTransactionManager</code> was configured for the
     *                          given test object
     */
    protected PlatformTransactionManager getSpringTransactionManager(Object testObject) {
        PlatformTransactionManager transactionManager = getSpringConfiguredTransactionManager(testObject);
        if (transactionManager == null) {
            throw new UnitilsException("No PlatformTransactionManager has been configured in a spring ApplicationContext " +
                    "associated with test class " + testObject.getClass());
        }
        return transactionManager;
    }

    
    /**
     * @param testObject The test object, not null
     * @return The <code>PlatformTransactionManager</code> that is configured in the spring application 
     * context associated with the given testObject.
     */
    protected PlatformTransactionManager getSpringConfiguredTransactionManager(Object testObject) {
        SpringTransactionManagerInterceptingBeanPostProcessor beanPostProcessor = getSpringModule().getBeanPostProcessor(
                testObject, SpringTransactionManagerInterceptingBeanPostProcessor.class);
        if (beanPostProcessor == null) {
            return null;
        }
        return beanPostProcessor.getSpringTransactionManager();
    }

    
    /**
     * @return The Spring module
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }

    
    /**
     * BeanPostProcessor that makes sure any Spring <code>PlatformTransactionManager</code> configured
     * in an application context is intercepted and made available for Unitils transaction management.  
     */
    public static class SpringTransactionManagerInterceptingBeanPostProcessor implements BeanPostProcessor {

        /* The intercepted PlatformTransactionManager, if any */
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
         * Intercepts every instance of <code>PlatformTransactionManager</code> and stores it.
         * 
         * @param bean     The new bean instance
         * @param beanName The name of the bean
         * @return The post processed bean. Is in all cases equal to the given bean parameter
         * @throws BeansException
         * @throws UnitilsException If more than one PlatformTransactionManager is configured in the application context.
         */
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof PlatformTransactionManager) {
                if (springTransactionManager != null) {
                    throw new UnitilsException("More than one PlatformTransactionManager is configured in the spring " +
                            "ApplicationContext. This is not supported in Unitils");
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
