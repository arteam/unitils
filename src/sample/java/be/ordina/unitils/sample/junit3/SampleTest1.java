/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.sample.junit3;

import org.unitils.easymock.annotation.Mock;
import org.unitils.UnitilsJUnit3;
import org.unitils.easymock.EasyMockModule;
import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

public class SampleTest1 extends UnitilsJUnit3 {


    @Mock (order = Mock.Order.Default)
    private MockedClass mock;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void test1() throws IllegalAccessException {

        expect(mock.someBehavior(false, 0, null, null)).andReturn("Result");
        EasyMockModule.replay();

        String result = mock.someBehavior(true, 999, "Test", new ArrayList());

        assertEquals("Result", result);
        EasyMockModule.verify();
    }


    public void test2() {
        System.out.println("SampleTest1.test2");
    }


    public void test3() {
        System.out.println("SampleTest1.test3");
    }


    //todo javadoc
    private static class MockedClass {

        public void someBehavior() {
        }

        public String someBehavior(boolean b, int i, Object object, List list) {
            return null;
        }
    }
}
