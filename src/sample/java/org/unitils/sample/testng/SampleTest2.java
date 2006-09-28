/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.sample.testng;

import org.unitils.UnitilsTestNG;
import org.testng.annotations.*;


public class SampleTest2 extends UnitilsTestNG {


    @BeforeClass
    public static void beforeClass() {
        System.out.println("SampleTest2.beforeClass");
    }


    @AfterClass
    public static void afterClass() {
        System.out.println("SampleTest2.afterClass");
    }

    @BeforeTest
    public void before() {
        System.out.println("SampleTest2.before");
    }

    @AfterTest
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
