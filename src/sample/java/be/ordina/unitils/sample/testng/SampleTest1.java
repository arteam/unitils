/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.sample.testng;

import be.ordina.unitils.UnitilsTestNG;
import org.testng.annotations.*;

public class SampleTest1 extends UnitilsTestNG {


    @BeforeClass
    public void beforeClass() {
        System.out.println("SampleTest1.beforeClass");
    }


    @AfterClass
    public static void afterClass() {
        System.out.println("SampleTest1.afterClass");
    }

    @BeforeTest
    public void before() {
        System.out.println("SampleTest1.before");
    }

    @AfterTest
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
