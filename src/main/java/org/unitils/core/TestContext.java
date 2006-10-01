package org.unitils.core;

/**
 * todo javadoc
 */
public class TestContext {

    private Class testClass;

    private Object testObject;

    private String testMethodName;


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

    public String getTestMethodName() {
        return testMethodName;
    }

    public void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

}
