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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.database.util.TransactionMode.DEFAULT;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotation;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAnnotationHelper {

    protected TransactionMode defaultTransactionMode;


    public DatabaseAnnotationHelper(TransactionMode defaultTransactionMode) {
        this.defaultTransactionMode = defaultTransactionMode;
    }


    /**
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     * @return The transactional annotation, null if none found
     */
    public Transactional getTransactionalAnnotation(Object testObject, Method testMethod) {
        return getMethodOrClassLevelAnnotation(Transactional.class, testMethod, testObject.getClass());
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
            return defaultTransactionMode;
        }
        return transactionMode;
    }
}
