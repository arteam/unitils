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
package org.unitils.mock;

import org.unitils.mock.annotation.MatchStatement;
import org.unitils.mock.core.MockObject;

/**
 * Declares the contract for a controller object that enables defining the behavior of methods of a partial mock object,
 * or for performing assert statements that verify that certain calls were effectively made. A method is also defined
 * that provides access to the actual partial mock object.
 * <p/>
 * If Unitils encounters a field declared as {@link PartialMock}, a {@link MockObject} is automatically instantiated and
 * assigned to the declared field. This mock object will use the original implementation of each method as default
 * behavior.
 */
public interface PartialMock<T> extends Mock<T> {

    /**
     * Stubs out (removes) the behavior of the method when the invocation following
     * this call matches the observed behavior. E.g.
     * <p/>
     * mock.stub().method1();
     * <p/>
     * will not invoke the actual behavior of method1.
     * <p/>
     * If the method has a return type, a default value will be returned.
     * <p/>
     * Note: stubbed methods can still be asserted afterwards: e.g.
     * <p/>
     * mock.assertInvoked().method1();
     *
     * @return The proxy instance that will record the method call, not null
     */
    @MatchStatement
    T stub();
}