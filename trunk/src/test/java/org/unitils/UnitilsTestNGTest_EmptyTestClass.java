package org.unitils;

import org.unitils.core.TestListener;

/**
 * Empty TestNG test class
 * <p/>
 * Test class used in the {@link org.unitils.UnitilsTestNGTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_EmptyTestClass extends UnitilsTestNG {


    protected TestListener createTestListener() {
        return new UnitilsTestNGTest.TracingTestListener();
    }

}
