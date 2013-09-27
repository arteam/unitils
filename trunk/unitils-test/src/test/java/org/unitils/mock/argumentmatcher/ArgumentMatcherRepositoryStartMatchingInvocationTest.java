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
package org.unitils.mock.argumentmatcher;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.core.util.CloneService;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherRepositoryStartMatchingInvocationTest extends UnitilsJUnit4 {

    private ArgumentMatcherRepository argumentMatcherRepository;

    private Mock<ArgumentMatcherPositionFinder> argumentMatcherPositionFinderMock;
    private Mock<CloneService> cloneServiceMock;
    private ProxyInvocation proxyInvocation;


    @Before
    public void initialize() throws Exception {
        argumentMatcherRepository = new ArgumentMatcherRepository(argumentMatcherPositionFinderMock.getMock(), cloneServiceMock.getMock());

        Method method = MyInterface.class.getMethod("method");
        StackTraceElement element = new StackTraceElement(MyInterface.class.getName(), "method1", "file", 222);
        proxyInvocation = new ProxyInvocation("mockName", null, method, emptyList(), emptyList(), new StackTraceElement[]{element});
    }


    @Test
    public void startMatchingInvocation() {
        argumentMatcherRepository.startMatchingInvocation(111);
        List<ArgumentMatcher> result = argumentMatcherRepository.finishMatchingInvocation(proxyInvocation);
        assertTrue(result.isEmpty());
        argumentMatcherPositionFinderMock.assertInvoked().getArgumentMatcherIndexes(proxyInvocation, 111, 222, 1);
    }


    private static interface MyInterface {

        void method();
    }
}
