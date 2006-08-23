package be.ordina.unitils.testing.util;

import junit.framework.TestCase;

/**
 * @author Filip Neven
 */
public class PropertyAssertTest extends TestCase {

    public void testAssertPropertyEquals() {
        TestObject testObject = new TestObject("testValue");
        PropertyAssert.assertPropertyEquals("testValue", testObject, "testProperty");
    }

    public class TestObject {

        private String testProperty;

        public TestObject(String testProperty) {
            this.testProperty = testProperty;
        }

        public String getTestProperty() {
            return testProperty;
        }

        public void setTestProperty(String testProperty) {
            this.testProperty = testProperty;
        }

    }
}
