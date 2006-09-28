package org.unitils.inject;

/**
 * @author Filip Neven
 */
public class InjectOnStatic {

    private static ToInject toInject;

    private static ToInject toInjectField;

    private static TestObject testObject;

    private static TestObject testObjectField;

    static {
        testObject = new TestObject();
        testObjectField = new TestObject();
    }

    public static void setToInject(ToInject toInject) {
        InjectOnStatic.toInject = toInject;
    }

    public static void setTestObject(TestObject testObject) {
        InjectOnStatic.testObject = testObject;
    }

    public static ToInject getToInject() {
        return toInject;
    }

    public static ToInject getToInjectField() {
        return toInjectField;
    }

    public static TestObject getTestObject() {
        return testObject;
    }

    public static TestObject getTestObjectField() {
        return testObjectField;
    }

    public static class TestObject {

        private ToInject toInject;

        public void setToInject(ToInject toInject) {
            this.toInject = toInject;
        }

        public ToInject getToInject() {
            return toInject;
        }

    }

    public static class ToInject {

    }
}
