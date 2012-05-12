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

package org.unitilsnew.database.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.database.annotations.Transactional;
import org.unitilsnew.database.core.TransactionManager;

import static org.unitilsnew.database.util.TransactionMode.*;

/**
 * @author Tim Ducheyne
 */
public class TransactionalTestAnnotationListenerBeforeTestSetupTest extends UnitilsJUnit4 {

    /* Tested object */
    private TransactionalTestAnnotationListener transactionalTestAnnotationListener;

    private Mock<TransactionManager> transactionManagerMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<Annotations<Transactional>> annotationsMock;


    private Transactional annotation1;
    private Transactional annotation2;
    private Transactional annotation3;
    private Transactional annotation4;


    @Before
    public void initialize() throws Exception {
        transactionalTestAnnotationListener = new TransactionalTestAnnotationListener(transactionManagerMock.getMock());

        annotation1 = MyClass1.class.getAnnotation(Transactional.class);
        annotation2 = MyClass2.class.getAnnotation(Transactional.class);
        annotation3 = MyClass3.class.getAnnotation(Transactional.class);
        annotation4 = MyClass4.class.getAnnotation(Transactional.class);
    }


    @Test
    public void noTransactionWhenDisabled() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), annotationsMock.getMock());

        transactionManagerMock.assertNotInvoked().startTransaction();
    }

    @Test
    public void startTransactionWhenCommit() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), annotationsMock.getMock());

        transactionManagerMock.assertInvoked().startTransaction();
    }

    @Test
    public void startTransactionWhenRollback() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), annotationsMock.getMock());

        transactionManagerMock.assertInvoked().startTransaction();
    }

    @Test
    public void noTransactionWhenDefault() {
        annotationsMock.returns(annotation4).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), annotationsMock.getMock());

        transactionManagerMock.assertNotInvoked().startTransaction();
    }


    @Transactional(DISABLED)
    private static class MyClass1 {
    }

    @Transactional(COMMIT)
    private static class MyClass2 {

    }

    @Transactional(ROLLBACK)
    private static class MyClass3 {

    }

    @Transactional
    private static class MyClass4 {
    }
}
