/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import static org.unitils.easymock.ReflectionArgumentMatcher.lenEq;
import static org.unitils.easymock.ReflectionArgumentMatcher.refEq;
import static org.unitils.reflectionassert.ReflectionComparatorModes.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorModes.LENIENT_ORDER;

import java.util.Arrays;
import java.util.List;

/**
 * Test for {@link ReflectionArgumentMatcher}.
 */
public class ReflectionArgumentMatcherTest extends TestCase {


    /* A test mock instance */
    private TestMock testMock;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        testMock = createMock(TestMock.class);
    }


    /**
     * Tests the refEq argument matcher with strict mode and equal values.
     */
    public void testRefEq() {

        testMock.method(refEq("stringValue"), EasyMock.eq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);

        testMock.method("stringValue", 3, "objectValue1", "objectValue2");
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher with strict mode and different values.
     */
    public void testRefEq_notEquals() {

        testMock.method(refEq("stringValue"), EasyMock.eq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);


        try {
            testMock.method("xxxx", 3, "objectValue1", "objectValue2");
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with strict mode and different vararg values.
     */
    public void testRefEq_notEqualsVarArgs() {

        testMock.method(refEq("stringValue"), EasyMock.eq(3), refEq("objectValue1"), refEq("objectValue2"));
        replay(testMock);

        try {
            testMock.method("stringValue", 3, "objectValue1");
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with lenient order mode and lists having a different order.
     */
    public void testRefEq_equalsLenientOrder() {

        testMock.method(refEq(Arrays.asList("element1", "element2", "element3"), LENIENT_ORDER));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the lenEq argument matcher with lists having a different order.
     * This should be the same as refEq with lenient order and ignore defaults.
     */
    public void testLenEq() {

        testMock.method(lenEq(Arrays.asList("element1", "element2", "element3")));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher with strict order mode and lists having a different order.
     */
    public void testRefEq_notEqualsStrictOrder() {

        testMock.method(refEq(Arrays.asList("element1", "element2", "element3")));
        replay(testMock);

        try {
            testMock.method(Arrays.asList("element3", "element1", "element2"));
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Tests the refEq argument matcher with ignore defaults mode and null default value.
     */
    public void testRefEq_equalsIgnoreDefaults() {

        testMock.method(refEq((List<String>) null, IGNORE_DEFAULTS));
        replay(testMock);

        testMock.method(Arrays.asList("element3", "element1", "element2"));
        verify(testMock);
    }


    /**
     * Tests the refEq argument matcher without ignore defaults mode and null default value.
     */
    public void testRefEq_notEqualsNoIgnoreDefaults() {

        testMock.method(refEq((List<String>) null));
        replay(testMock);

        try {
            testMock.method(Arrays.asList("element3", "element1", "element2"));
            fail("Expected AssertionError");

        } catch (AssertionError e) {
            //expected
        }
    }


    /**
     * Test interface that is going to be mocked.
     */
    private interface TestMock {

        public void method(String arg1, int arg2, Object... arg3);

        public void method(List<String> arg1);

    }

}
