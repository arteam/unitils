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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.core.MockObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


/**
 * Tests the formatting of proxies and mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectFormatterMockAndProxyTest extends UnitilsJUnit4 {

    private ObjectFormatter objectFormatter = new ObjectFormatter();


    @Test
    public void formatCgLibProxy() {
        Object proxy = createCgLibProxy();
        String result = objectFormatter.format(proxy);
        assertEquals("Proxy<Collection>", result);
    }

    @Test
    public void formatJdkProxy() {
        Object proxy = createJdkProxy();
        String result = objectFormatter.format(proxy);
        assertEquals("Proxy<?>", result);
    }

    @Test
    public void formatMock() {
        Mock<Collection> mock = new MockObject<Collection>("mockName", Collection.class, this);
        String result = objectFormatter.format(mock);
        assertEquals("Mock<mockName>", result);
    }

    @Test
    public void formatMockProxy() {
        Object mockProxy = new MockObject<Collection>("mockName", Collection.class, this).getMock();
        String result = objectFormatter.format(mockProxy);
        assertEquals("Mock<mockName>", result);
    }

    @Test
    public void formatDummy() {
        Object dummy = MockUnitils.createDummy(Collection.class);
        String result = objectFormatter.format(dummy);
        assertEquals("Dummy<Collection>", result);
    }


    private Object createCgLibProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Collection.class);
        enhancer.setCallback(new NoOp() {
        });
        return enhancer.create();
    }

    private Object createJdkProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Collection.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }
}