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
package org.unitils.easymock.util;

import org.easymock.IAnswer;
import org.easymock.internal.AlwaysMatcher;
import org.easymock.internal.Range;
import org.easymock.internal.RecordState;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 */
public class LenientMocksControlInvocationInterceptorDelegationTest extends UnitilsJUnit4 {

    private LenientMocksControl.InvocationInterceptor invocationInterceptor;

    private Mock<RecordState> recordStateMock;


    @Before
    public void initialize() throws Exception {
        invocationInterceptor = new LenientMocksControl().new InvocationInterceptor();
        invocationInterceptor.setRecordState(recordStateMock.getMock());
    }


    @Test
    public void assertRecordStateDelegated() {
        invocationInterceptor.assertRecordState();
        recordStateMock.assertInvoked().assertRecordState();
    }

    @Test
    public void andReturnDelegated() {
        invocationInterceptor.andReturn("value");
        recordStateMock.assertInvoked().andReturn("value");
    }

    @Test
    public void andThrowDelegated() {
        NullPointerException e = new NullPointerException();
        invocationInterceptor.andThrow(e);
        recordStateMock.assertInvoked().andThrow(e);
    }

    @Test
    public void andAnswerDelegated() {
        IAnswer<String> answer = new IAnswer<String>() {
            public String answer() throws Throwable {
                return null;
            }
        };
        invocationInterceptor.andAnswer(answer);
        recordStateMock.assertInvoked().andAnswer(answer);
    }

    @Test
    public void andStubReturnDelegated() {
        invocationInterceptor.andStubReturn("value");
        recordStateMock.assertInvoked().andStubReturn("value");
    }

    @Test
    public void andStubThrowDelegated() {
        NullPointerException e = new NullPointerException();
        invocationInterceptor.andStubThrow(e);
        recordStateMock.assertInvoked().andStubThrow(e);
    }

    @Test
    public void andStubAnswerDelegated() {
        IAnswer<String> answer = new IAnswer<String>() {
            public String answer() throws Throwable {
                return null;
            }
        };
        invocationInterceptor.andStubAnswer(answer);
        recordStateMock.assertInvoked().andStubAnswer(answer);
    }

    @Test
    public void asStubDelegated() {
        invocationInterceptor.asStub();
        recordStateMock.assertInvoked().asStub();
    }

    @Test
    public void timesDelegated() {
        Range range = new Range(5);
        invocationInterceptor.times(range);
        recordStateMock.assertInvoked().times(range);
    }

    @Test
    public void checkOrderDelegated() {
        invocationInterceptor.checkOrder(true);
        recordStateMock.assertInvoked().checkOrder(true);
    }

    @Test
    public void replayDelegated() {
        invocationInterceptor.replay();
        recordStateMock.assertInvoked().replay();
    }

    @Test
    public void verifyDelegated() {
        invocationInterceptor.verify();
        recordStateMock.assertInvoked().verify();
    }

    @Test
    public void setDefaultReturnValueDelegated() {
        invocationInterceptor.setDefaultReturnValue("value");
        recordStateMock.assertInvoked().setDefaultReturnValue("value");
    }

    @Test
    public void setDefaultThrowableDelegated() {
        NullPointerException e = new NullPointerException();
        invocationInterceptor.setDefaultThrowable(e);
        recordStateMock.assertInvoked().setDefaultThrowable(e);
    }

    @Test
    public void setDefaultVoidCallableDelegated() {
        invocationInterceptor.setDefaultVoidCallable();
        recordStateMock.assertInvoked().setDefaultVoidCallable();
    }

    @Test
    public void setDefaultMatcherDelegated() {
        AlwaysMatcher matcher = new AlwaysMatcher();
        invocationInterceptor.setDefaultMatcher(matcher);
        recordStateMock.assertInvoked().setDefaultMatcher(matcher);
    }

    @Test
    public void setMatcherDelegated() throws Exception {
        AlwaysMatcher matcher = new AlwaysMatcher();
        Method method = getClass().getMethod("setMatcherDelegated");
        invocationInterceptor.setMatcher(method, matcher);
        recordStateMock.assertInvoked().setMatcher(method, matcher);
    }
}
