package be.ordina.unitils.reflectionassert;

import static be.ordina.unitils.reflectionassert.ReflectionComparatorModes.LENIENT_ORDER;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

/**
 * todo javadoc
 */
public class ReflectionAssertCollectionsTest extends TestCase {

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

    /* Class under test */
    private ReflectionAssert reflectionAssert;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        reflectionAssert = new ReflectionAssert();
    }


    public void testAssertEquals_sameSequence() {
        reflectionAssert.assertEquals(list, sameList);
    }

    public void testAssertEquals_differentSequence() {
        new ReflectionAssert(LENIENT_ORDER).assertEquals(list, listDifferentSequence);
    }

    public void testAssertEquals_differentListSameSize() {
        try {
            reflectionAssert.assertEquals(list, differentListSameSize);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_duplicateElement() {
        try {
            reflectionAssert.assertEquals(list, listDuplicateElement);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_oneElementLess() {
        try {
            reflectionAssert.assertEquals(list, listOneElementLess);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_oneElementMore() {
        try {
            reflectionAssert.assertEquals(list, listOneElementMore);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_sameSequence() {
        reflectionAssert.assertEquals(list, sameList);
    }

    public void testAssertEquals_strictSequence_differentSequence() {
        try {
            reflectionAssert.assertEquals(list, listDifferentSequence);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_differentListSameSize() {
        try {
            reflectionAssert.assertEquals(list, differentListSameSize);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_duplicateElement() {
        try {
            reflectionAssert.assertEquals(list, listDuplicateElement);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_oneElementLess() {
        try {
            reflectionAssert.assertEquals(list, listOneElementLess);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertEquals_strictSequence_oneElementMore() {
        try {
            reflectionAssert.assertEquals(list, listOneElementMore);
        } catch (AssertionFailedError e) {
            // Expected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    //todo other test class

    public void testAssertPropertyEquals_sameSequence() {
        reflectionAssert.assertPropertyEquals("testProperty", list, listObjects);
    }

    public void testAssertPropertyEquals_differentSequence() {
        reflectionAssert.assertPropertyEquals("testProperty", list, listObjects);
    }

    public void testAssertPropertyEquals_differentList() {
        try {
            reflectionAssert.assertPropertyEquals("testProperty", list, differentListObjects);
        } catch (AssertionFailedError e) {
            // Excpected
            return;
        }
        Assert.fail("Expected AssertionFailedError");
    }

    public void testAssertPropertyEquals_strictSequence_sameSequence() {
        reflectionAssert.assertPropertyEquals("testProperty", list, listObjects);
    }

    public void testAssertPropertyEquals_strictSequence_differentSequence() {
        try {
            reflectionAssert.assertPropertyEquals("testProperty", list, listObjectsDifferentSequence);
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
