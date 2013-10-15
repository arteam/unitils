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
package org.unitils.mock.core.matching;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;
import org.unitils.mock.core.util.StackTraceService;

import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class MatchingProxyInvocationHandlerResetTest extends UnitilsJUnit4 {

    private MatchingProxyInvocationHandler matchingProxyInvocationHandler;

    private Mock<ArgumentMatcherRepository> argumentMatcherRepositoryMock;
    private Mock<ProxyService> proxyServiceMock;
    private Mock<StackTraceService> stackTraceServiceMock;
    @Dummy
    private MatchingInvocationHandler matchingInvocationHandler;
    @Dummy
    private Map proxy;


    @Before
    public void initialize() {
        matchingProxyInvocationHandler = new MatchingProxyInvocationHandler(argumentMatcherRepositoryMock.getMock(), proxyServiceMock.getMock(), stackTraceServiceMock.getMock());

        StackTraceElement stackTraceElement1 = new StackTraceElement("class1", "method1", "file1", 111);
        StackTraceElement stackTraceElement2 = new StackTraceElement("class2", "method2", "file2", 222);
        StackTraceElement[] stackTrace = new StackTraceElement[]{stackTraceElement1, stackTraceElement2};
        stackTraceServiceMock.returns(stackTrace).getInvocationStackTrace(Mock.class);

        proxyServiceMock.returns(proxy).createProxy("mockName", false, null, Map.class);
    }


    @Test
    public void reset() {
        matchingProxyInvocationHandler.startMatchingInvocation("mockName", true, matchingInvocationHandler);

        matchingProxyInvocationHandler.reset();
        matchingProxyInvocationHandler.assertPreviousMatchingInvocationCompleted();
        argumentMatcherRepositoryMock.assertInvoked().reset();
    }
}
