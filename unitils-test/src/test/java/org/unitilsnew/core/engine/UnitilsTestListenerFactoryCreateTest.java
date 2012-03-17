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

package org.unitilsnew.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.core.TestListener;
import org.unitilsnew.core.context.UnitilsContext;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class UnitilsTestListenerFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsTestListenerFactory unitilsTestListenerFactory;

    private Mock<UnitilsContext> unitilsContextMock;
    @Dummy
    private WrapperForFieldAnnotationListenerFactory wrapperForFieldAnnotationListenerFactory;
    @Dummy
    private WrapperForTestAnnotationListenerFactory wrapperForTestAnnotationListenerFactory;


    @Before
    public void initialize() {
        unitilsTestListenerFactory = new UnitilsTestListenerFactory(unitilsContextMock.getMock(), wrapperForFieldAnnotationListenerFactory, wrapperForTestAnnotationListenerFactory);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void testListenersAreCreated() throws Exception {
        MyTestListener1 myTestListener1 = new MyTestListener1();
        MyTestListener2 myTestListener2 = new MyTestListener2();
        unitilsContextMock.returns(asList(MyTestListener1.class, MyTestListener2.class)).getTestListenerTypes();
        unitilsContextMock.returns(myTestListener1).getInstanceOfType(MyTestListener1.class);
        unitilsContextMock.returns(myTestListener2).getInstanceOfType(MyTestListener2.class);

        UnitilsTestListener result = unitilsTestListenerFactory.create();

        assertEquals(2, result.testListeners.size());
        assertSame(myTestListener1, result.testListeners.get(0));
        assertSame(myTestListener2, result.testListeners.get(1));
        assertSame(wrapperForFieldAnnotationListenerFactory, result.wrapperForFieldAnnotationListenerFactory);
        assertSame(wrapperForTestAnnotationListenerFactory, result.wrapperForTestAnnotationListenerFactory);
    }

    @Test
    public void emptyTestListenerTypes() throws Exception {
        UnitilsTestListener result = unitilsTestListenerFactory.create();
        assertTrue(result.testListeners.isEmpty());
    }


    private static class MyTestListener1 extends TestListener {
    }

    private static class MyTestListener2 extends TestListener {
    }
}
