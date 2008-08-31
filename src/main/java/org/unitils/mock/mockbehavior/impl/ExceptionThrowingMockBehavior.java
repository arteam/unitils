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

/**
 * Mock behavior that throws a given exception.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ExceptionThrowingMockBehavior implements MockBehavior {

    /* The exception to throw */
    private Throwable exceptionToThrow;


    /**
     * Creates the throwing behavior for the given exception.
     *
     * @param exceptionToThrow The exception, not null
     */
    public ExceptionThrowingMockBehavior(Throwable exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }


    /**
     * Executes the mock behavior.
     *
     * @param proxyInvocation The proxy method invocation, not null
     * @return Nothing
     */
    public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
        throw exceptionToThrow;
    }

}
