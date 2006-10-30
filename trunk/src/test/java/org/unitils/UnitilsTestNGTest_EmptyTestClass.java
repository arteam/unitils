package org.unitils;

import org.unitils.core.TestListener;

import java.util.List;

/**
 * Empty TestNG test class
 * <p/>
 * Test class used in the {@link org.unitils.UnitilsTestNGTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_EmptyTestClass extends UnitilsTestNG {


    private static List<String> callList;

    public static void setCallList(List<String> list) {
        callList = list;
    }


    @Override
    protected TestListener createTestListener() {
        return new TracingTestListener(callList);
    }

}
