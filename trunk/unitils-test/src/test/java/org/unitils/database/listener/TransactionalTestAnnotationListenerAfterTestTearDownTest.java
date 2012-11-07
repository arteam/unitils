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

package org.unitils.database.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotation.Transactional;
import org.unitils.database.core.TransactionManager;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import static org.unitils.database.util.TransactionMode.*;

/**
 * @author Tim Ducheyne
 */
public class TransactionalTestAnnotationListenerAfterTestTearDownTest extends UnitilsJUnit4 {

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
    public void ignoreWhenDisabled() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.afterTestTearDown(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        transactionManagerMock.assertNotInvoked().commit(true);
        transactionManagerMock.assertNotInvoked().rollback(true);
    }

    @Test
    public void commit() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.afterTestTearDown(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        transactionManagerMock.assertInvoked().commit(true);
    }

    @Test
    public void rollback() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.afterTestTearDown(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        transactionManagerMock.assertInvoked().rollback(true);
    }

    @Test
    public void ignoreWhenDefault() {
        annotationsMock.returns(annotation4).getAnnotationWithDefaults();

        transactionalTestAnnotationListener.afterTestTearDown(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        transactionManagerMock.assertNotInvoked().commit(true);
        transactionManagerMock.assertNotInvoked().rollback(true);
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
