package be.ordina.unitils.testing.mock;

import be.ordina.unitils.testing.util.ReflectionAssert;
import be.ordina.unitils.testing.util.ReflectionComparatorModes;
import org.easymock.IArgumentMatcher;
import org.easymock.internal.Invocation;

import java.lang.reflect.Method;

public class ReflectionArgumentMatcher <T> implements IArgumentMatcher {

    private T object;

    private Invocation invocation;

    private int argumentIndex;

    private ReflectionAssert reflectionAssert;

    public ReflectionArgumentMatcher(T object, ReflectionComparatorModes... modes) {
        this(object, null, 0, modes);
    }

    public ReflectionArgumentMatcher(T object, Invocation invocation, int argumentIndex, ReflectionComparatorModes... modes) {
        this.object = object;
        this.invocation = invocation;
        this.argumentIndex = argumentIndex;
        this.reflectionAssert = new ReflectionAssert(modes);
    }


    public boolean matches(Object arg0) {

        String message = null;
        if (invocation != null) {
            message = "\nMethod: " + toString(invocation.getMethod());
            message += "\nArgument index: " + argumentIndex;
            message += "\nInvoking call: at " + findCall(invocation.getMethod());
        }

        reflectionAssert.assertEquals(message, object, arg0);
        return true;
    }


    public void appendTo(StringBuffer arg0) {
        arg0.append(object);
    }


    private String toString(Method method) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName() + "(");
        Class[] params = method.getParameterTypes();
        for (int j = 0; j < params.length; j++) {
            sb.append(params[j].getSimpleName());
            if (j < (params.length - 1)) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private String findCall(Method method) {

        StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
        if (stackTraceElements != null) {

            boolean found = false;
            String methodName = method.getName();
            String className = method.getDeclaringClass().getName();

            for (StackTraceElement stackTraceElement : stackTraceElements) {
                if (found) {
                    return stackTraceElement.toString();
                }
                if (stackTraceElement.getClassName().contains(className) && stackTraceElement.getMethodName().contains(methodName)) {
                    found = true;
                }
            }
        }
        return "<unknown>";
    }

}
