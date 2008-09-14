/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock.mockbehavior.impl;

import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Mock behavior that returns a default value.
 * <p/>
 * Following defaults are used:<ul>
 * <li>Number values: 0</li>
 * <li>Object values: null</li>
 * <li>Collectionn, arrays etc: empty values</li></ul>
 * <p/>
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class DefaultValueReturningMockBehavior implements MockBehavior {


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The default value
     */
    public Object execute(ProxyInvocation proxyInvocation) {
        Class<?> returnType = proxyInvocation.getMethod().getReturnType();
        if (Number.class.isAssignableFrom(returnType)) {
            return 0;
        }
        if (List.class.equals(returnType)) {
            return new ArrayList();
        }
        if (Set.class.equals(returnType)) {
            return new TreeSet();
        }
        if (Map.class.equals(returnType)) {
            return new HashMap();
        }
        if (returnType.isArray()) {
            Array.newInstance(returnType.getComponentType(), 0);
        }
        return null;
    }

}