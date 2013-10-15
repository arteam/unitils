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
package org.unitils.mock.core;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockReturnsIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;
    private Mock<Set> otherMock;


    @Test
    public void returns() {
        mockObject.returns("aValue").testMethodString();

        String result1 = mockObject.getMock().testMethodString();
        String result2 = mockObject.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertLenientEquals("aValue", result2);
    }

    @Test
    public void onceReturns() {
        mockObject.onceReturns("aValue").testMethodString();

        String result1 = mockObject.getMock().testMethodString();
        String result2 = mockObject.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertNull(result2);
    }

    @Test
    public void returnsNullWhenNoBehaviorDefinedForObjectMethod() {
        String result = mockObject.getMock().testMethodString();
        assertLenientEquals(null, result);
    }

    @Test
    public void returnsZeroWhenNoBehaviorDefinedForNumberMethod() {
        int result = mockObject.getMock().testMethodNumber();
        assertLenientEquals(0, result);
    }

    @Test
    public void returnsEmptyListWhenNoBehaviorDefinedForListMethod() {
        List<String> result = mockObject.getMock().testMethodList();
        assertLenientEquals(0, result.size());
    }

    @Test
    public void returnsEmptySetWhenNoBehaviorDefinedForSetMethod() {
        Set<String> result = mockObject.getMock().testMethodSet();
        assertLenientEquals(0, result.size());
    }

    @Test
    public void returnsEmptyMapWhenNoBehaviorDefinedForMapMethod() {
        Map<String, String> result = mockObject.getMock().testMethodMap();
        assertLenientEquals(0, result.size());
    }

    @Test
    public void returnsProxyWhenReturningOtherMock() {
        mockObject.returns(otherMock).testMethodSet();

        Set<String> result = mockObject.getMock().testMethodSet();
        assertSame(otherMock.getMock(), result);
    }

    @Test
    public void returnsWithVarArgs() {
        mockObject.returns("result").testMethodWithVarArgs(3);
        Object result = mockObject.getMock().testMethodWithVarArgs(3);
        assertEquals("result", result);
    }


    private static interface TestInterface {

        String testMethodString();

        int testMethodNumber();

        List<String> testMethodList();

        Set<String> testMethodSet();

        Map<String, String> testMethodMap();

        Object testMethodWithVarArgs(int arg, String... otherArgs);
    }
}