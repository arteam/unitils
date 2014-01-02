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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class ArrayAndCollectionFormatterFormatMapTest extends UnitilsJUnit4 {

    private ArrayAndCollectionFormatter arrayAndCollectionFormatter;

    private Mock<ObjectFormatter> objectFormatterMock;
    @Dummy
    private MyObject myObject1;
    @Dummy
    private MyObject myObject2;
    @Dummy
    private MyObject myObject3;
    @Dummy
    private MyObject myKey1;
    @Dummy
    private MyObject myKey2;
    @Dummy
    private MyObject myKey3;
    private StringBuilder result;


    @Before
    public void initialize() {
        arrayAndCollectionFormatter = new ArrayAndCollectionFormatter(2, objectFormatterMock.getMock());

        result = new StringBuilder();
    }


    @Test
    public void map() {
        Map<MyObject, MyObject> map = new LinkedHashMap<MyObject, MyObject>();
        map.put(myKey1, myObject1);
        map.put(myKey2, myObject2);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("key1");
                return null;
            }
        }).formatImpl(myKey1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("key2");
                return null;
            }
        }).formatImpl(myKey2, 3, result);
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

        arrayAndCollectionFormatter.formatMap(map, 2, result);
        assertEquals("{key1=11, key2=22}", result.toString());
    }

    @Test
    public void truncatedCollection() {
        Map<MyObject, MyObject> map = new LinkedHashMap<MyObject, MyObject>();
        map.put(myKey1, myObject1);
        map.put(myKey2, myObject2);
        map.put(myKey3, myObject3);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("key1");
                return null;
            }
        }).formatImpl(myKey1, 3, result);
        objectFormatterMock.performs(new MockBehavior() {
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
                result.append("key2");
                return null;
            }
        }).formatImpl(myKey2, 3, result);
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

        arrayAndCollectionFormatter.formatMap(map, 2, result);
        assertEquals("{key1=11, key2=22, ...}", result.toString());
    }

    @Test
    public void emptyMap() {
        Map<MyObject, MyObject> map = new HashMap<MyObject, MyObject>();

        arrayAndCollectionFormatter.formatMap(map, 2, result);
        assertEquals("{}", result.toString());
    }


    public static class MyKey {
    }

    public static class MyObject {
    }
}
