package be.ordina.unitils.testing.mock;

import be.ordina.unitils.testing.util.ReflectionComparatorModes;
import org.easymock.ArgumentsMatcher;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;
import org.easymock.classextension.internal.MocksClassControl;
import org.easymock.internal.*;

import java.lang.reflect.Method;
import java.util.List;

public class LenientMocksControl extends MocksClassControl {


    private InvocationInterceptor invocationInterceptor;


    public LenientMocksControl(MockType type, ReflectionComparatorModes... modes) {
        super(type);
        this.invocationInterceptor = new InvocationInterceptor(modes);
    }


    public IMocksControlState getState() {
        IMocksControlState mocksControlState = super.getState();
        if (mocksControlState instanceof RecordState) {
            invocationInterceptor.setRecordState((RecordState) mocksControlState);
            return invocationInterceptor;
        }
        return mocksControlState;
    }


    private class InvocationInterceptor implements IMocksControlState {


        private RecordState recordState;

        private ReflectionComparatorModes[] modes;


        public InvocationInterceptor(ReflectionComparatorModes... modes) {
            this.modes = modes;
        }


        public void setRecordState(RecordState recordState) {
            this.recordState = recordState;
        }

        public Object invoke(Invocation invocation) {
            LastControl.reportLastControl(LenientMocksControl.this);
            createMatchers(invocation);
            return recordState.invoke(invocation);
        }

        private void createMatchers(Invocation invocation) {
            List<IArgumentMatcher> matchers = LastControl.pullMatchers();
            if (matchers != null && matchers.size() > 0) {
                throw new IllegalStateException("This mock control does not support per-argument matchers.");
            }

            Object[] arguments = invocation.getArguments();
            if (arguments == null) {
                return;
            }

            for (int i = 0; i < arguments.length; i++) {
                LastControl.reportMatcher(new ReflectionArgumentMatcher(arguments[i], invocation, i, modes));
            }
        }

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
