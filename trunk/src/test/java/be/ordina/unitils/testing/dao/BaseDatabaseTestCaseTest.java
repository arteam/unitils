/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.dao;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

/**
 * todo
 */
public class BaseDatabaseTestCaseTest extends TestCase {

    /* Class under test */
    private BaseDatabaseTestCase baseDatabaseTestCase;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        baseDatabaseTestCase = new ConcreteBaseDatabaseTest();
    }

    /**
     * Test for {@link BaseDatabaseTestCase#setUp()}.
     */
    public void testSetUp() throws Exception {

        // todo implement
        //baseDatabaseTestCase.setUp();

        //assertNotNull(baseDatabaseTestCase.getProperties());
        //assertNotNull(baseDatabaseTestCase.getDataSource());
    }

    public void testGetConnection() throws Exception {
//        todo implement
//        baseDatabaseTestCase.getConnection();
//
//        assertNotNull(baseDatabaseTestCase.getProperties());
//        assertNotNull(baseDatabaseTestCase.getDataSource());
    }


    public void testGetDataSet() throws Exception {
        // todo implement
        //   baseDatabaseTestCase.getDataSet();

        //   assertNotNull(baseDatabaseTestCase.getProperties());
        //   assertNotNull(baseDatabaseTestCase.getDataSource());
    }


    public void testGetExpectedDataSet() throws Exception {
        // todo implement
        //baseDatabaseTestCase.getExpectedDataSet();

        //assertNotNull(baseDatabaseTestCase.getProperties());
        //assertNotNull(baseDatabaseTestCase.getDataSource());
    }


    public void testGetTestDataSetFileName() throws Exception {

        String fileName = baseDatabaseTestCase.getTestDataSetFileName();

        //innerclass so starts with BaseDatabaseTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDatabaseTest.testName.xml", fileName);
    }


    public void testGetDefaultDataSetFileName() throws Exception {

        String fileName = baseDatabaseTestCase.getDefaultDataSetFileName();

        //innerclass so starts with BaseDatabaseTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDatabaseTest.xml", fileName);
    }


    public void testGetExpectedDataSetFileName() throws Exception {

        String fileName = baseDatabaseTestCase.getExpectedDataSetFileName();

        //innerclass so starts with BaseDatabaseTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDatabaseTest.testName-result.xml", fileName);
    }


    public void testUpdateDatabaseSchemaIfNeeded() throws Exception {
//   todo implement
//        baseDatabaseTestCase.updateDatabaseSchemaIfNeeded();
//
//        assertNotNull(baseDatabaseTestCase.getProperties());
//        assertNotNull(baseDatabaseTestCase.getDataSource());
    }


    public static class ConcreteBaseDatabaseTest extends BaseDatabaseTestCase {

        public ConcreteBaseDatabaseTest() {
            super("testName");
        }

        protected String getPropertiesFileName() {
            return null;
        }

    }

}