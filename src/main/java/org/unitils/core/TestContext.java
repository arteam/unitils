package org.unitils.core;

/**
 * todo javadoc
 */
public class TestContext {

    private static ThreadLocal<Class> testClassHolder = new ThreadLocal<Class>();

    private static ThreadLocal<Object> testObjectHolder = new ThreadLocal<Object>();

    private static ThreadLocal<String> testMethodNameHolder = new ThreadLocal<String>();

    private TestContext() {
    }

    public static Class getTestClass() {
        return testClassHolder.get();
    }

    public static void setTestClass(Class testClass) {
        testClassHolder.set(testClass);
    }

    public static Object getTestObject() {
        return testObjectHolder.get();
    }

    public static void setTestObject(Object testObject) {
        testObjectHolder.set(testObject);
    }

    public static String getTestMethodName() {
        return testMethodNameHolder.get();
    }

    public static void setTestMethodName(String testMethodName) {
        testMethodNameHolder.set(testMethodName);
    }
}
