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
package org.unitils.core.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.unitils.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class ArrayAndCollectionFormatterFormatCollectionTest extends UnitilsJUnit4 {

    private ArrayAndCollectionFormatter arrayAndCollectionFormatter;

    private Mock<ObjectFormatter> objectFormatterMock;
    @Dummy
    private MyObject myObject1;
    @Dummy
    private MyObject myObject2;
    private StringBuilder result;


    @Before
    public void initialize() {
        arrayAndCollectionFormatter = new ArrayAndCollectionFormatter(3, objectFormatterMock.getMock());

        result = new StringBuilder();
    }


    @Test
    public void list() {
        List<MyObject> list = asList(myObject1, myObject2);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("11");
                return null;
            }
        }).formatImpl(myObject1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("22");
                return null;
            }
        }).formatImpl(myObject2, 3, result);

        arrayAndCollectionFormatter.formatCollection(list, 2, result);
        assertEquals("[11, 22]", result.toString());
    }

    @Test
    public void set() {
        Set<MyObject> set = asSet(myObject1, myObject2);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("11");
                return null;
            }
        }).formatImpl(myObject1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("22");
                return null;
            }
        }).formatImpl(myObject2, 3, result);

        arrayAndCollectionFormatter.formatCollection(set, 2, result);
        assertEquals("[11, 22]", result.toString());
    }

    @Test
    public void emptyCollection() {
        List<MyObject> list = emptyList();

        arrayAndCollectionFormatter.formatCollection(list, 2, result);
        assertEquals("[]", result.toString());
    }

    @Test
    public void truncatedCollection() {
        List<MyObject> list = asList(myObject1, myObject2, myObject1, myObject2);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("11");
                return null;
            }
        }).formatImpl(myObject1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("22");
                return null;
            }
        }).formatImpl(myObject2, 3, result);

        arrayAndCollectionFormatter.formatCollection(list, 2, result);
        assertEquals("[11, 22, 11, ...]", result.toString());
    }


    public static class MyObject {
    }
}
