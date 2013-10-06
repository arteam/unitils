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

import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;

public class MockProxy<T> {

    protected String name;
    protected Class<T> type;
    protected T proxy;
    protected T matchingProxy;
    protected MatchingProxyInvocationHandler matchingProxyInvocationHandler;


    public MockProxy(String name, Class<T> type, T proxy, T matchingProxy, MatchingProxyInvocationHandler matchingProxyInvocationHandler) {
        this.name = name;
        this.type = type;
        this.proxy = proxy;
        this.matchingProxy = matchingProxy;
        this.matchingProxyInvocationHandler = matchingProxyInvocationHandler;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public T getProxy() {
        return proxy;
    }

    public T startMatchingInvocation(boolean chained, MatchingInvocationHandler matchingInvocationHandler) {
        matchingProxyInvocationHandler.startMatchingInvocation(name, chained, matchingInvocationHandler);
        return matchingProxy;
    }
}