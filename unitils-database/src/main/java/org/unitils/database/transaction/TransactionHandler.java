package org.unitils.database.transaction;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.database.util.TransactionMode.DEFAULT;
import static org.unitils.database.util.TransactionMode.DISABLED;
import static org.unitils.database.util.TransactionMode.ROLLBACK;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.transaction.impl.UnitilsTransactionManagementConfiguration;
import org.unitils.database.util.Flushable;
import org.unitils.database.util.TransactionMode;


/**
 * TransactionHandler.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class TransactionHandler {
    private UnitilsTransactionManager transactionManager;

    /**
     * Set of possible providers of a spring <code>PlatformTransactionManager</code>
     */
    protected Set<UnitilsTransactionManagementConfiguration> transactionManagementConfigurations = new HashSet<UnitilsTransactionManagementConfiguration>();

    private Properties configuration;
    private Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    @SuppressWarnings("unchecked")
    public TransactionHandler() {
        this.configuration = Unitils.getInstance().getConfiguration();
        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DatabaseModule.class, configuration, Transactional.class);
    }

    /**
     * Returns the transaction manager or creates one if it does not exist yet.
     *
     * @return The transaction manager, not null
     */
    public UnitilsTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = getInstanceOf(UnitilsTransactionManager.class, configuration);
            transactionManager.init(transactionManagementConfigurations);
        }
        return transactionManager;
    }

    /**
     * Starts a new transaction on the transaction manager configured in unitils
     *
     * @param testObject The test object, not null
     */
    public void startTransaction(Object testObject) {
        getTransactionManager().startTransaction(testObject);
    }


    /**
     * Commits the current transaction.
     *
     * @param testObject The test object, not null
     */
    public void commitTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        getTransactionManager().commit(testObject);
    }


    /**
     * Performs a rollback of the current transaction
     *
     * @param testObject The test object, not null
     */
    public void rollbackTransaction(Object testObject) {
        flushDatabaseUpdates(testObject);
        getTransactionManager().rollback(testObject);
    }

    /**
     * Starts a transaction. If the Unitils DataSource was not loaded yet, we simply remember that a
     * transaction was started but don't actually start it. If the DataSource is loaded within this
     * test, the transaction will be started immediately after loading the DataSource.
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     */
    public void startTransactionForTestMethod(Object testObject, Method testMethod) {
        if (isTransactionsEnabled(testObject, testMethod)) {
            startTransaction(testObject);
        }
    }


    /**
     * Commits or rollbacks the current transaction, if transactions are enabled and a transactionManager is
     * active for the given testObject
     *
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     */
    public void endTransactionForTestMethod(Object testObject, Method testMethod) {
        if (isTransactionsEnabled(testObject, testMethod)) {
            if (getTransactionMode(testObject, testMethod) == COMMIT) {
                commitTransaction(testObject);
            } else if (getTransactionMode(testObject, testMethod) == ROLLBACK) {
                rollbackTransaction(testObject);
            }
        }
    }

    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return The {@link TransactionMode} for the given object
     */
    protected TransactionMode getTransactionMode(Object testObject, Method testMethod) {
        TransactionMode transactionMode = getMethodOrClassLevelAnnotationProperty(Transactional.class, "value", DEFAULT, testMethod, testObject.getClass());
        transactionMode = getEnumValueReplaceDefault(Transactional.class, "value", transactionMode, defaultAnnotationPropertyValues);
        return transactionMode;
    }

    /**
     * @param testObject The test object, not null
     * @param testMethod The test method, not null
     * @return Whether transactions are enabled for the given test method and test object
     */
    public boolean isTransactionsEnabled(Object testObject, Method testMethod) {
        
        TransactionMode transactionMode = getTransactionMode(testObject, testMethod);
        return transactionMode != DISABLED;
    }

    public void registerTransactionManagementConfiguration(UnitilsTransactionManagementConfiguration transactionManagementConfiguration) {

        transactionManagementConfigurations.add(transactionManagementConfiguration);
    }

    /**
     * @param testClass
     */
    public void registerTransactionManagementConfiguration(final DataSource dataSource) {

            // Make sure that a spring DataSourceTransactionManager is used for transaction management, if
            // no other transaction management configuration takes preference
            registerTransactionManagementConfiguration(new UnitilsTransactionManagementConfiguration() {

                public boolean isApplicableFor(Object testObject) {
                    return true;
                }

                public PlatformTransactionManager getSpringPlatformTransactionManager(Object testObject) {
                    //return new DataSourceTransactionManager(getDataSourceAndActivateTransactionIfNeeded(databaseName));
                    return new DataSourceTransactionManager(dataSource);
                }

                public boolean isTransactionalResourceAvailable(Object testObject) {
                    return dataSource != null;
                }

                public Integer getPreference() {
                    return 1;
                }

            });
    }

    public void flushDatabaseUpdates(Object testObject) {
        List<Flushable> flushables = Unitils.getInstance().getModulesRepository().getModulesOfType(Flushable.class);
        for (Flushable flushable : flushables) {
            flushable.flushDatabaseUpdates(testObject);
        }
    } 

}
