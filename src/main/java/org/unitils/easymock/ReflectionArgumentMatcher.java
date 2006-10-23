/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock;

import org.apache.commons.lang.StringUtils;
import static org.easymock.EasyMock.reportMatcher;
import org.easymock.IArgumentMatcher;
import org.easymock.internal.Invocation;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionComparator.Difference;
import org.unitils.reflectionassert.ReflectionComparatorModes;
import static org.unitils.reflectionassert.ReflectionComparatorModes.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorModes.LENIENT_ORDER;

import java.lang.reflect.Method;


/**
 * An easy mock argument matcher to check whether 2 objects are equal by comparing all fields of the objects using
 * reflection.
 * <p/>
 * The (combination of) comparator modes specify how strict the comparison must be:<ul>
 * <li>ignore defaults: compare only arguments (and inner values) that have a non default value (eg null) as exepected value</li>
 * <li>lenient dates: do not compare actual date values, just that they both have a value or not</li>
 * <li>lenient order: order is not important when comparing collections or arrays</li>
 * </ul>
 *
 * @see ReflectionComparator
 * @see ReflectionComparatorModes
 */
public class ReflectionArgumentMatcher<T> implements IArgumentMatcher {

    /* The expected argument value */
    private T expected;

    /* The recorded expected invocation */
    private Invocation invocation;

    /* The index of the argument in the method-signature (starts with 0) */
    private int argumentIndex;

    /* The comparator for lenient comparing expected and actual argument values */
    private ReflectionComparator reflectionComparator;

   
    /**
     * Creates a matcher for the expected argument value.
     * The modes specify how to compare the expected value with the actual value.
     *
     * @param expected the argument value, not null
     * @param modes    the comparator modes
     */
    public ReflectionArgumentMatcher(T expected, ReflectionComparatorModes... modes) {
        this(expected, null, -1, modes);
    }

    /**
     * Creates a matcher for the expected argument value and method invocation.
     * The modes specify how to compare the expected value with the actual value.
     * <p/>
     * The invocation and argument index are used to provide more detailed information
     * in case the actual value did not match. If invocation is left null, a more
     * general message will be returned.
     *
     * @param expected      the argument value, not null
     * @param invocation    the method invocation in which this argument value is expected
     * @param argumentIndex the index of the argument in the method-signature, starts with 0
     * @param modes         the comparator mode
     */
    public ReflectionArgumentMatcher(T expected, Invocation invocation, int argumentIndex, ReflectionComparatorModes... modes) {
        this.expected = expected;
        this.invocation = invocation;
        this.argumentIndex = argumentIndex;
        this.reflectionComparator = new ReflectionComparator(modes);
    }


    /**
     * Checks whether the given actual value is equal to the expected value.
     * A reflection comparator is used to compare both values. The modes determine how
     * strict this comparison will be.
     * <p/>
     * An assertion error is thrown if expected and actual did not match. This way, a more
     * detailed message can be provided.
     *
     * @param actual the actual argument value
     * @return true
     * @throws AssertionError in case expected and actual did not match
     */
    public boolean matches(Object actual) {

        Difference difference = reflectionComparator.getDifference(expected, actual);
        if (difference != null) {
            throw new AssertionError(formatMessage(difference));
        }
        return true;
    }


    /**
     * Appends the expected value to the given buffer.
     *
     * @param arg0 the buffer, not null
     */
    public void appendTo(StringBuffer arg0) {
        arg0.append(expected);
    }


    /**
     * Formats the exception message.
     *
     * @param difference the difference
     * @return the detailed message
     */
    private String formatMessage(Difference difference) {

        String result = "Assertion failed. Unexpected argument value for method invocation.";
        if (invocation != null) {
            result += "\nMethod: " + toString(invocation.getMethod());
            result += "\nArgument: " + (argumentIndex + 1);
            result += "\nInvoking call: at " + findCall(invocation.getMethod());
        }

        result += "\nReason: " + difference.getMessage();
        String fieldString = difference.getFieldStackAsString();
        if (StringUtils.isEmpty(fieldString)) {
            fieldString = "<top-level>";
        }

        result += "\nField: <" + fieldString;
        result += "> expected: <" + difference.getLeftValue();
        result += "> but was: <" + difference.getRightValue() + ">";
        return result;
    }


    /**
     * Creates a string representation of the given method.
     *
     * @param method the method, not null
     * @return the signature string
     */
    private String toString(Method method) {
        StringBuffer sb = new StringBuffer();
        sb.append(method.getName());
        sb.append("(");
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


    /**
     * Tries to find the line in the call stack that invoked the given method.
     * <p/>
     * This will try to find the call (stack trace element) that occured just before a call that
     * has the same method name and class as the given method.
     *
     * @param method the method, not null
     * @return the stack trace element string, &lt;unknown&gt; if not found
     */
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
