/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.sample.junit3;

import be.ordina.unitils.UnitilsJUnit3;

public class SampleTest1 extends UnitilsJUnit3 {


    protected void setUp() throws Exception {
        super.setUp();
        System.out.println("SampleTest1.setUp");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        System.out.println("SampleTest1.tearDown");
    }


    public void test1() {
        System.out.println("SampleTest1.test1");
    }


    public void test2() {
        System.out.println("SampleTest1.test2");
    }


    public void test3() {
        System.out.println("SampleTest1.test3");
    }

}
