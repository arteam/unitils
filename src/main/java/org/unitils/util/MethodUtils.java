package org.unitils.util;

import java.lang.reflect.Method;

/**
 * @author Filip Neven
 */
public class MethodUtils {

    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#equals} method
     */
    public static boolean isEqualsMethod(Method method) {
        return "equals".equals(method.getName())
                && 1 == method.getParameterTypes().length
                && Object.class.equals(method.getParameterTypes()[0]);
    }


    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#hashCode} method
     */
    public static boolean isHashCodeMethod(Method method) {
        return "hashCode".equals(method.getName())
                && 0 == method.getParameterTypes().length;
    }


    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#toString} method
     */
    public static boolean isToStringMethod(Method method) {
        return "toString".equals(method.getName())
                && 0 == method.getParameterTypes().length;
    }


    /**
     * @param method The method to check, not null
     * @return True if the given method is the {@link Object#clone} method
     */
    public static boolean isCloneMethod(Method method) {
        return "clone".equals(method.getName())
                && 0 == method.getParameterTypes().length;
    }
}
