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
package org.unitils.mock.core.proxy.impl;

import org.unitils.core.UnitilsException;
import org.unitils.mock.core.matching.MatchingInvocationBuilder;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.proxy.ProxyInvocationHandler;


public class MatchingProxyInvocationHandler implements ProxyInvocationHandler {

    protected MatchingInvocationBuilder matchingInvocationBuilder;
    protected ProxyInvocationHandler proxyInvocationHandler;


    public MatchingProxyInvocationHandler(MatchingInvocationBuilder matchingInvocationBuilder) {
        this.matchingInvocationBuilder = matchingInvocationBuilder;
    }


    public void startMatchingInvocation(String mockName, boolean chained, MatchingInvocationHandler matchingInvocationHandler) {
        this.proxyInvocationHandler = matchingInvocationBuilder.startMatchingInvocation(mockName, !chained, matchingInvocationHandler);
    }


    public Object handleInvocation(ProxyInvocation proxyInvocation) throws Throwable {
        if (proxyInvocationHandler == null) {
            // todo td implement => merge builder with this handler
            throw new UnitilsException("todo");
        }
        Object result = proxyInvocationHandler.handleInvocation(proxyInvocation);
        proxyInvocationHandler = null;
        return result;
    }
}