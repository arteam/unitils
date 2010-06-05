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
package org.unitils.core.util;

import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.util.TestMethodFinder;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class TestMethodFinderTest {


    @Test
    public void findCurrentTestMethod() throws Exception {
        Method result = TestMethodFinder.findCurrentTestMethod(TestMethodFinderTest.class);
        assertEquals(TestMethodFinderTest.class.getDeclaredMethod("findCurrentTestMethod"), result);
    }

    @Test(expected = UnitilsException.class)
    public void wrongTestInstance() throws Exception {
        TestMethodFinder.findCurrentTestMethod(String.class);
    }


    @Test
    public void methodCallInTest() throws Exception {
        doMethodCallInTest();
    }

    private void doMethodCallInTest() throws Exception {
        Method result = TestMethodFinder.findCurrentTestMethod(TestMethodFinderTest.class);
        assertEquals(TestMethodFinderTest.class.getDeclaredMethod("methodCallInTest"), result);

    }
}
