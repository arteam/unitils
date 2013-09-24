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

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.unitils.mock.ArgumentMatchers.any;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestClass> mockObject;


    @Test
    public void exactMatches() {
        mockObject.returns("111").method("a", "b", "c");
        mockObject.returns("222").method("d", "e", "f");

        String result = mockObject.getMock().method("d", "e", "f");
        assertEquals("222", result);
        mockObject.assertNotInvoked().method("a", "b", "c");
        mockObject.assertInvoked().method("d", "e", "f");
    }

    @Test
    public void sameValuesMatchBetterThanMatchingValues() {
        mockObject.returns("111").method(any(String.class), any(String.class), any(String.class));
        mockObject.returns("222").method("d", "e", "f");
        mockObject.returns("333").method("d", any(String.class), any(String.class));

        String result = mockObject.getMock().method("d", "e", "f");
        assertEquals("222", result);
    }

    @Test
    public void useLastWhenEqualMatch() {
        mockObject.returns("111").method(any(String.class), any(String.class), any(String.class));
        mockObject.returns("222").method(any(String.class), "e", any(String.class));
        mockObject.returns("333").method("d", any(String.class), any(String.class));
        mockObject.returns("444").method(any(String.class), any(String.class), "f");
        mockObject.returns("555").method(any(String.class), any(String.class), "f");
        mockObject.returns("666").method(any(String.class), any(String.class), any(String.class));

        String result = mockObject.getMock().method("d", "e", "f");
        assertEquals("555", result);
    }

    @Test
    public void lessNullValuesMatchesBetter() {
        mockObject.returns("111").method(null, "e", "f");
        mockObject.returns("222").method(null, null, "f");

        String result = mockObject.getMock().method("d", "e", "f");
        assertEquals("111", result);
    }

    @Test
    public void sameValueMatchesBetter() {
        List<String> list = asList("a", "b");
        mockObject.returns("111").listMethod(asList("a", "b"));
        mockObject.returns("222").listMethod(list);
        mockObject.returns("333").listMethod(asList("a", "b"));

        String result = mockObject.getMock().listMethod(list);
        assertEquals("222", result);
    }


    private static interface TestClass {

        String method(String value1, String value2, String value3);

        String listMethod(List value);
    }
}