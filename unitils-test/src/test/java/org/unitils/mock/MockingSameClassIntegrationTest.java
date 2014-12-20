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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for UNI-233
 *
 * @author Florian Klaschka
 * @author Tim Ducheyne
 */
public class MockingSameClassIntegrationTest extends UnitilsJUnit4 {

    private Mock<A> b1;
    private Mock<A> b2;
    private Mock<A> b3;
    private Mock<A> b4;

    @Dummy
    private Object o;

    @Test
    public void assertInvokedShouldFailWhenNotInvoked() {
        b1.getMock().method(o);
        //b2.getMock().method(o);
        b3.getMock().method(o);
        b4.getMock().method(o);

        b1.assertInvoked().method(null);
        try {
            b2.assertInvoked().method(null);
            fail("AssertionError expected");
        } catch (AssertionError e) {
            String message = e.getMessage();
            assertTrue(message, message.startsWith("Expected invocation of A.method(), but it didn't occur."));
        }
        b3.assertInvoked().method(null);
        b4.assertInvoked().method(null);
    }

    @Test
    public void assertNoMoreInvocationsShouldFailWhenInvoked() {
        b1.getMock().method(o);
        b2.getMock().method(o);
        b3.getMock().method(o);
        b4.getMock().method(o);

        b1.assertInvoked().method(null);
        //b2.assertInvoked().method(null);
        b3.assertInvoked().method(null); // asserts invocation of b2.method(o)
        b4.assertInvoked().method(null); // asserts invocation of b3.method(o)

        try {
            MockUnitils.assertNoMoreInvocations();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            String message = e.getMessage();
            assertTrue(message, message.startsWith("No more invocations expected, yet observed following calls:\n" +
                    "1. b2.method(o)  .....  at org.unitils.mock.MockingSameClassIntegrationTest.assertNoMoreInvocationsShouldFailWhenInvoked"));
        }
    }

    public static class A {
        public void method(Object o) {
        }
    }
}
