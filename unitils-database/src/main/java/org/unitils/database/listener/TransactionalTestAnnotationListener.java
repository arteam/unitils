/*
 * Copyright 2013,  Unitils.org
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

package org.unitils.database.listener;

import org.unitils.core.TestAnnotationListener;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.reflect.Annotations;
import org.unitils.database.annotation.Transactional;
import org.unitils.database.core.TransactionManager;
import org.unitils.database.util.TransactionMode;

import static org.unitils.core.TestPhase.INJECTION;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.database.util.TransactionMode.ROLLBACK;

/**
 * @author Tim Ducheyne
 */
public class TransactionalTestAnnotationListener extends TestAnnotationListener<Transactional> {

    protected TransactionManager transactionManager;


    public TransactionalTestAnnotationListener(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    @Override
    public TestPhase getTestPhase() {
        return INJECTION;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance, Annotations<Transactional> annotations) {
        Transactional annotation = annotations.getAnnotationWithDefaults();
        TransactionMode transactionMode = annotation.value();
        String transactionManagerName = annotation.transactionManagerName();

        if (transactionMode == COMMIT || transactionMode == ROLLBACK) {
            transactionManager.startTransaction(transactionManagerName);
        }
    }

    @Override
    public void afterTestTearDown(TestInstance testInstance, Annotations<Transactional> annotations, Throwable testThrowable) {
        Transactional annotation = annotations.getAnnotationWithDefaults();
        TransactionMode transactionMode = annotation.value();

        if (transactionMode == COMMIT) {
            transactionManager.commit(true);

        } else if (transactionMode == ROLLBACK) {
            transactionManager.rollback(true);
        }
    }
}
