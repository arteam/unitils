package org.unitils.io.annotation.handler;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestListener;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class CompositeAnnotationHandlerTest extends UnitilsJUnit4 {

    @TestedObject
    private CompositeAnnotationHandler annotationHandler;

    private Mock<TestListener> listenerOne;

    private Mock<TestListener> listenerTwo;

    @Before
    public void setUp() {
        annotationHandler = new CompositeAnnotationHandler(listenerOne.getMock(), listenerTwo.getMock());
    }

    @Test
    public void beforeTestClassTest() {
        annotationHandler.beforeTestClass(null);

        listenerOne.assertInvoked().beforeTestClass(null);
        listenerTwo.assertInvoked().beforeTestClass(null);
    }

    @Test
    public void afterCreateTestObjectTest() {
        annotationHandler.afterCreateTestObject(null);

        listenerOne.assertInvoked().afterCreateTestObject(null);
        listenerTwo.assertInvoked().afterCreateTestObject(null);
    }

    @Test
    public void beforeTestSetUpTest() {
        annotationHandler.beforeTestSetUp(null, null);

        listenerOne.assertInvoked().beforeTestSetUp(null, null);
        listenerTwo.assertInvoked().beforeTestSetUp(null, null);
    }

    @Test
    public void beforeTestMethodTest() {
        annotationHandler.beforeTestMethod(null, null);

        listenerOne.assertInvoked().beforeTestMethod(null, null);
        listenerTwo.assertInvoked().beforeTestMethod(null, null);
    }

    @Test
    public void afterTestMethodTest() {
        annotationHandler.afterTestMethod(null, null, null);

        listenerOne.assertInvoked().afterTestMethod(null, null, null);
        listenerTwo.assertInvoked().afterTestMethod(null, null, null);
    }

    @Test
    public void afterTestTearDownTest() {

        annotationHandler.afterTestTearDown(null, null);

        listenerOne.assertInvoked().afterTestTearDown(null, null);
        listenerTwo.assertInvoked().afterTestTearDown(null, null);

    }

}
