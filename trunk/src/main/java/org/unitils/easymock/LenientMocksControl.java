/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.easymock;

import org.easymock.ArgumentsMatcher;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.*;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.lang.reflect.Method;
import java.util.List;

/**
 * An EasyMock mock control that uses the reflection argument matcher for all arguments of a method invocation.
 * <p/>
 * No explicit argument matcher setting is needed (or allowed). This control will automatically report
 * lenient reflection argument matchers. These matchers can apply some leniency when comparing expected and actual
 * argument values.
 * <p/>
 * Setting the {@link ReflectionComparatorMode#IGNORE_DEFAULTS} mode will for example ignore all fields that
 * have default values as expected values. E.g. if a null value is recorded as argument it will not be checked when
 * the actual invocation occurs. The same applies for inner-fields of object arguments that contain default java values.
 * <p/>
 * Setting the {@link ReflectionComparatorMode#LENIENT_DATES} mode will ignore the actual date values of arguments and
 * inner fields of arguments. It will only check whether both dates are null or both dates are not null. The actual
 * date and hour do not matter.
 * <p/>
 * Setting the {@link ReflectionComparatorMode#LENIENT_ORDER} mode will ignore the actual order of collections and
 * arrays arguments and inner fields of arguments. It will only check whether they both contain the same elements.
 *
 * @see ReflectionComparatorMode
 * @see org.unitils.reflectionassert.ReflectionComparator
 */
public class LenientMocksControl extends MocksClassControl {


    /* The interceptor that wraps the record state */
    private InvocationInterceptor invocationInterceptor;

    /**
     * Creates a default (no default returns and no order checking) mock control.
     *
     * @param modes the modes for the reflection argument matcher
     */
    public LenientMocksControl(ReflectionComparatorMode... modes) {
        this(MockType.DEFAULT, modes);
    }

    /**
     * Creates a mock control.<ul>
     * <li>Default mock type: no default return values and no order checking</li>
     * <li>Nice mock type: returns default values if no return value set, no order checking</li>
     * <li>Strict mock type: no default return values and strict order checking</li>
     * </ul>
     *
     * @param type  the EasyMock mock type
     * @param modes the modes for the reflection argument matcher
     */
    public LenientMocksControl(MockType type, ReflectionComparatorMode... modes) {
        super(type);
        this.invocationInterceptor = new InvocationInterceptor(modes);
    }


    /**
     * Overriden to be able to replace the record behavior that going to record all method invocations.
     * The interceptor will make sure that reflection argument matchers will be reported for the
     * arguments of all recorded method invocations.
     *
     * @return the state, wrapped in case of a RecordState
     */
    public IMocksControlState getState() {
        IMocksControlState mocksControlState = super.getState();
        if (mocksControlState instanceof RecordState) {
            invocationInterceptor.setRecordState((RecordState) mocksControlState);
            return invocationInterceptor;
        }
        return mocksControlState;
    }


    /**
     * A wrapper for the record state in easy mock that will intercept the invoke method
     * so that it can install reflection argument matchers for all arguments of the recorded method invocation.
     * <p/>
     * The old easy mock way of having a single argument matcher for all arguments has been deprecated. Since
     * EasyMock 2 each argument should have its own matcher. We however want to avoid having to set all
     * matchers to the reflection argument matcher explicitly.
     * Because some of the methods are declared final and some classes explicitly cast to subtypes, creating a wrapper
     * seems to be the only way to be able to intercept the matcher behavior.
     */
    private class InvocationInterceptor implements IMocksControlState {

        /* The wrapped record state */
        private RecordState recordState;

        /* The modes for the reflection argument matchers */
        private ReflectionComparatorMode[] modes;


        /**
         * Creates an interceptor that will create reflection argument matchers for all arguments of all recorded
         * method invocations.
         *
         * @param modes the modes for the reflection argument matchers
         */
        public InvocationInterceptor(ReflectionComparatorMode... modes) {
            this.modes = modes;
        }


        /**
         * Sets the current wrapped record state.
         *
         * @param recordState the state, not null
         */
        public void setRecordState(RecordState recordState) {
            this.recordState = recordState;
        }


        /**
         * Overriden to report reflection argument matchers for all arguments of the given method invocation.
         *
         * @param invocation the method invocation, not null
         * @return the result of the invocation
         */
        public Object invoke(Invocation invocation) {
            LastControl.reportLastControl(LenientMocksControl.this);
            createMatchers(invocation);
            return recordState.invoke(invocation);
        }


        /**
         * Reports report reflection argument matchers for all arguments of the given method invocation.
         * An exception will be thrown if there were already matchers reported for the invocation.
         *
         * @param invocation the method invocation, not null
         */
        private void createMatchers(Invocation invocation) {
            List<IArgumentMatcher> matchers = LastControl.pullMatchers();
            if (matchers != null) {
                if (matchers.size() != invocation.getArguments().length) {
                    throw new IllegalStateException("This mock control does not support mixing of no-argument matchers and per-argument matchers. " +
                            "Either no matchers are defined and the reflection argument matcher is used by default or all matchers are defined explicitly (Eg by using refEq()).");
                }
                return;
            }
            Object[] arguments = invocation.getArguments();
            if (arguments == null) {
                return;
            }

            for (int i = 0; i < arguments.length; i++) {
                LastControl.reportMatcher(new ReflectionArgumentMatcher<Object>(arguments[i], modes));
            }
        }

        // Pass through delegation

        public void assertRecordState() {
            recordState.assertRecordState();
        }

        public void andReturn(Object value) {
            recordState.andReturn(value);
        }

        public void andThrow(Throwable throwable) {
            recordState.andThrow(throwable);
        }

        public void andAnswer(IAnswer answer) {
            recordState.andAnswer(answer);
        }

        public void andStubReturn(Object value) {
            recordState.andStubReturn(value);
        }

        public void andStubThrow(Throwable throwable) {
            recordState.andStubThrow(throwable);
        }

        public void andStubAnswer(IAnswer answer) {
            recordState.andStubAnswer(answer);
        }

        public void asStub() {
            recordState.asStub();
        }

        public void times(Range range) {
            recordState.times(range);
        }

        public void checkOrder(boolean value) {
            recordState.checkOrder(value);
        }

        public void replay() {
            recordState.replay();
        }

        public void verify() {
            recordState.verify();
        }

        public void setDefaultReturnValue(Object value) {
            recordState.setDefaultReturnValue(value);
        }

        public void setDefaultThrowable(Throwable throwable) {
            recordState.setDefaultThrowable(throwable);
        }

        public void setDefaultVoidCallable() {
            recordState.setDefaultVoidCallable();
        }

        public void setDefaultMatcher(ArgumentsMatcher matcher) {
            recordState.setDefaultMatcher(matcher);
        }

        public void setMatcher(Method method, ArgumentsMatcher matcher) {
            recordState.setMatcher(method, matcher);
        }
    }

}
