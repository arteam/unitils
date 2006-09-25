package be.ordina.unitils.sample.junit4;

import be.ordina.unitils.UnitilsJUnit4;
import junit.framework.JUnit4TestAdapter;
import org.junit.*;


public class SampleTest1 extends UnitilsJUnit4 {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SampleTest1.class);
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("SampleTest1.beforeClass");
    }


    @AfterClass
    public static void afterClass() {
        System.out.println("SampleTest1.afterClass");
    }

    @Before
    public void before() {
        System.out.println("SampleTest1.before");
    }

    @After
    public void after() {
        System.out.println("SampleTest1.after");
    }

    @Test
    public void test1() {
        System.out.println("SampleTest1.test1");
    }

    @Test
    public void test2() {
        System.out.println("SampleTest1.test2");
    }

    @Test
    public void test3() {
        System.out.println("SampleTest1.test3");
    }

}
