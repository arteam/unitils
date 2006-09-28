package org.unitils.sample.junit3;

import org.unitils.UnitilsJUnit3;

public class SampleTest2 extends UnitilsJUnit3 {

    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("SampleTest2.setUp");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("SampleTest2.tearDown");
    }


    public void test1() {
        System.out.println("SampleTest2.test1");
    }

    public void test2() {
        System.out.println("SampleTest2.test2");
    }

}
