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
package org.unitils.mock.mockbehavior;

import org.unitils.mock.proxy.ProxyInvocation;


/**
 * Behavior for a mock instance. Mock behavior is defined before the test is performed and then executed during
 * the test when needed. If a certain proxy method invocation requires the mock behavior, the {@link #execute} method
 * is called with the invocation as argument. The result value will then be used as return value of the proxy method.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public interface MockBehavior {


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return The return value, null if there is no return value
     */
    Object execute(ProxyInvocation proxyInvocation) throws Throwable;
}
