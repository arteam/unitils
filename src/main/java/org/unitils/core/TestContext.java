package org.unitils.core;

import java.lang.reflect.Method;

/**
 * todo javadoc
 */
public class TestContext {

    private Class testClass;

    private Object testObject;

    private Method testMethod;


    public Class getTestClass() {
        return testClass;
    }

    public void setTestClass(Class testClass) {
        this.testClass = testClass;
    }

    public Object getTestObject() {
        return testObject;
    }

    public void setTestObject(Object testObject) {
        this.testObject = testObject;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(Method testMethod) {
        this.testMethod = testMethod;
    }

}
