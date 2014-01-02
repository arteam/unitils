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

package org.unitils.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestListener;
import org.unitils.core.context.UnitilsContext;
import org.unitils.core.spring.SpringTestListener;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListenerFactory unitilsTestListenerFactory;

    private Mock<UnitilsContext> unitilsContextMock;
    @Dummy
    private FieldAnnotationTestListenerFactory fieldAnnotationTestListenerFactory;
    @Dummy
    private TestAnnotationTestListenerFactory testAnnotationTestListenerFactory;
    @Dummy
    private SpringTestListener springTestListener;


    @Before
    public void initialize() {
        unitilsTestListenerFactory = new UnitilsTestListenerFactory(unitilsContextMock.getMock(), fieldAnnotationTestListenerFactory, testAnnotationTestListenerFactory, springTestListener);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testListenersAreCreated() throws Exception {
        MyTestListener1 myTestListener1 = new MyTestListener1();
        MyTestListener2 myTestListener2 = new MyTestListener2();
        unitilsContextMock.returnsAll(MyTestListener1.class, MyTestListener2.class).getTestListenerTypes();
        unitilsContextMock.returns(myTestListener1).getInstanceOfType(MyTestListener1.class);
        unitilsContextMock.returns(myTestListener2).getInstanceOfType(MyTestListener2.class);

        UnitilsTestListener result = unitilsTestListenerFactory.create();

        assertEquals(3, result.testListeners.size());
        assertSame(springTestListener, result.testListeners.get(0));
        assertSame(myTestListener1, result.testListeners.get(1));
        assertSame(myTestListener2, result.testListeners.get(2));
        assertSame(fieldAnnotationTestListenerFactory, result.fieldAnnotationTestListenerFactory);
        assertSame(testAnnotationTestListenerFactory, result.testAnnotationTestListenerFactory);
    }

    @Test
    public void emptyTestListenerTypes() throws Exception {
        UnitilsTestListener result = unitilsTestListenerFactory.create();

        assertEquals(1, result.testListeners.size());
        assertSame(springTestListener, result.testListeners.get(0));
    }


    private static class MyTestListener1 extends TestListener {
    }

    private static class MyTestListener2 extends TestListener {
    }
}
