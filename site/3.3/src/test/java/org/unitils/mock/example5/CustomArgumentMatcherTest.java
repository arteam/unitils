package org.unitils.mock.example5;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import static org.unitils.mock.example5.CustomArgumentMatchers.lessThan;

public class CustomArgumentMatcherTest extends UnitilsJUnit4 {

    private Mock<TestObject> testMock;

    @Test
    public void testMethod() {
        testMock.getMock().method(5);

        testMock.assertInvoked().method(lessThan(6));
        testMock.assertNotInvoked().method(lessThan(4));
    }

    private static interface TestObject {

        void method(int arg);
    }
}

