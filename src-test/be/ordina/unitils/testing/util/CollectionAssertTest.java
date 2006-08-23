package be.ordina.unitils.testing.util;

import junit.framework.AssertionFailedError;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.List;
import java.util.Arrays;

/**
 * @author Filip Neven
 */
public class CollectionAssertTest extends TestCase {

    private List<String> list = Arrays.asList("el1", "el2");

    private List<String> sameList = Arrays.asList("el1", "el2");

    private List<String> listDifferentSequence = Arrays.asList("el2", "el1");

    private List<String> differentListSameSize = Arrays.asList("el2", "el3");

    private List<String> listDuplicateElement = Arrays.asList("el2", "el2", "el1");

    private List<String> listOneElementLess = Arrays.asList("el1");

    private List<String> listOneElementMore = Arrays.asList("el1", "el2", "el3");

    private List<TestObject> listObjects = Arrays.asList(new TestObject("el1"), new TestObject("el2"));

    private List<TestObject> listObjectsDifferentSequence = Arrays.asList(new TestObject("el2"), new TestObject("el1"));

    private List<TestObject> differentListObjects = Arrays.asList(new TestObject("el2"), new TestObject("el3"));

    public void testAssertEquals_sameSequence() {
        CollectionAssert.assertEquals(list, sameList);
    }

    public void testAssertEquals_differentSequence() {
        CollectionAssert.assertEquals(list, listDifferentSequence);
    }

    public void testAssertEquals_differentListSameSize() {
        try {
            CollectionAssert.assertEquals(list, differentListSameSize);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_duplicateElement() {
        try {
            CollectionAssert.assertEquals(list, listDuplicateElement);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_oneElementLess() {
        try {
            CollectionAssert.assertEquals(list, listOneElementLess);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_oneElementMore() {
        try {
            CollectionAssert.assertEquals(list, listOneElementMore);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_sameSequence() {
        CollectionAssert.assertEquals(list, sameList, true);
    }

    public void testAssertEquals_strictSequence_differentSequence() {
        try {
            CollectionAssert.assertEquals(list, listDifferentSequence, true);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_differentListSameSize() {
        try {
            CollectionAssert.assertEquals(list, differentListSameSize);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_duplicateElement() {
        try {
            CollectionAssert.assertEquals(list, listDuplicateElement, true);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_oneElementLess() {
        try {
            CollectionAssert.assertEquals(list, listOneElementLess, true);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_oneElementMore() {
        try {
            CollectionAssert.assertEquals(list, listOneElementMore, true);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertPropertyEquals_sameSequence() {
        CollectionAssert.assertPropertyEquals(list, listObjects, "testProperty");
    }

    public void testAssertPropertyEquals_differentSequence() {
        CollectionAssert.assertPropertyEquals(list, listObjects, "testProperty");
    }

    public void testAssertPropertyEquals_differentList() {
        try {
            CollectionAssert.assertPropertyEquals(list, differentListObjects, "testProperty", true);
        } catch (AssertionFailedError e) {
            // Excpected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertPropertyEquals_strictSequence_sameSequence() {
        CollectionAssert.assertPropertyEquals(list, listObjects, "testProperty", true);
    }

    public void testAssertPropertyEquals_strictSequence_differentSequence() {
        try {
            CollectionAssert.assertPropertyEquals(list, listObjectsDifferentSequence, "testProperty", true);
        } catch (AssertionFailedError e) {
            // Excpected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
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
