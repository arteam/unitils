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
package org.unitils.database.util;

import org.unitils.database.annotations.Transactional;
import org.unitils.util.PropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.unitils.database.util.TransactionMode.DEFAULT;
import static org.unitils.util.AnnotationUtils.getClassLevelAnnotation;
import static org.unitils.util.ReflectionUtils.getEnumValue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAnnotationHelper {

    public static final String DEFAULT_TRANSACTION_MODE_PROPERTY = "database.default.transaction.mode";

    protected Properties configuration;


    public DatabaseAnnotationHelper(Properties configuration) {
        this.configuration = configuration;
    }


    /**
     * @param testObject The test object, not null
     * @return The transactional annotation, null if none found
     */
    public Transactional getTransactionalAnnotation(Object testObject) {
        return getClassLevelAnnotation(Transactional.class, testObject.getClass());
    }

    public List<String> getTransactionManagerBeanNames(Transactional transactional) {
        if (transactional == null) {
            return new ArrayList<String>();
        }
        String[] transactionManagerBeanNames = transactional.transactionManagerBeanNames();
        return asList(transactionManagerBeanNames);
    }

    /**
     * Gets the transaction mode, replacing the value by the configured default value if the transaction mode was DEFAULT.
     *
     * @param transactional The annotation, can be null
     * @return The {@link TransactionMode}, null if the annotation was null
     */
    public TransactionMode getTransactionMode(Transactional transactional) {
        if (transactional == null) {
            return null;
        }
        TransactionMode transactionMode = transactional.value();
        if (transactionMode == DEFAULT) {
            String defaultValue = PropertyUtils.getString(DEFAULT_TRANSACTION_MODE_PROPERTY, configuration);
            return getEnumValue(TransactionMode.class, defaultValue);
        }
        return transactionMode;
    }
}
