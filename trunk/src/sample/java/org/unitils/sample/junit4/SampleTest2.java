package org.unitils.sample.junit4;

import org.unitils.UnitilsJUnit4;
import junit.framework.JUnit4TestAdapter;
import org.junit.*;

public class SampleTest2 extends UnitilsJUnit4 {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(org.unitils.sample.junit3.SampleTest2.class);
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("SampleTest2.beforeClass");
    }


    @AfterClass
    public static void afterClass() {
        System.out.println("SampleTest2.afterClass");
    }

    @Before
    public void before() {
        System.out.println("SampleTest2.before");
    }

    @After
    public void after() {
        System.out.println("SampleTest2.after");
    }


    @Test
    public void test1() {
        System.out.println("SampleTest2.test1");
    }

    @Test
    public void test2() {
        System.out.println("SampleTest2.test2");
    }

}

