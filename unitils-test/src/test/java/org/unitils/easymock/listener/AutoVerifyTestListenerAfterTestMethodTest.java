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
package org.unitils.easymock.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.core.MockService;
import org.unitils.mock.Mock;

/**
 * @author Tim Ducheyne
 */
public class AutoVerifyTestListenerAfterTestMethodTest extends UnitilsJUnit4 {

    private AutoVerifyTestListener autoVerifyTestListener;

    private Mock<MockService> mockServiceMock;


    @Before
    public void initialize() {
        autoVerifyTestListener = new AutoVerifyTestListener(mockServiceMock.getMock(), true);
    }


    @Test
    public void afterTestMethod() {
        autoVerifyTestListener.afterTestMethod(null, null);
        mockServiceMock.assertInvoked().verify();
    }

    @Test
    public void doNotVerifyWhenVerifyDisabled() {
        autoVerifyTestListener = new AutoVerifyTestListener(mockServiceMock.getMock(), false);

        autoVerifyTestListener.afterTestMethod(null, null);
        mockServiceMock.assertNotInvoked().verify();
    }

    @Test
    public void doNotVerifyWhenTestHasException() {
        autoVerifyTestListener.afterTestMethod(null, new NullPointerException());
        mockServiceMock.assertNotInvoked().verify();
    }
}