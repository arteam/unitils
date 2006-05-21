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
public class BaseDAOTestCaseTest extends TestCase {

    /* Class under test */
    private BaseDAOTestCase baseDAOTestCase;

    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        baseDAOTestCase = new ConcreteBaseDAOTest();
    }

    /**
     * Test for {@link BaseDAOTestCase#setUp()}.
     */
    public void testSetUp() throws Exception {

        // todo implement
        //baseDAOTestCase.setUp();

        //assertNotNull(baseDAOTestCase.getProperties());
        //assertNotNull(baseDAOTestCase.getDataSource());
    }

    public void testGetConnection() throws Exception {
//        todo implement
//        baseDAOTestCase.getConnection();
//
//        assertNotNull(baseDAOTestCase.getProperties());
//        assertNotNull(baseDAOTestCase.getDataSource());
    }


    public void testGetDataSet() throws Exception {
        // todo implement
        //   baseDAOTestCase.getDataSet();

        //   assertNotNull(baseDAOTestCase.getProperties());
        //   assertNotNull(baseDAOTestCase.getDataSource());
    }


    public void testGetExpectedDataSet() throws Exception {
        // todo implement
        //baseDAOTestCase.getExpectedDataSet();

        //assertNotNull(baseDAOTestCase.getProperties());
        //assertNotNull(baseDAOTestCase.getDataSource());
    }


    public void testGetTestDataSetFileName() throws Exception {

        String fileName = baseDAOTestCase.getTestDataSetFileName();

        //innerclass so starts with BaseDAOTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDAOTest.testName.xml", fileName);
    }


    public void testGetDefaultDataSetFileName() throws Exception {

        String fileName = baseDAOTestCase.getDefaultDataSetFileName();

        //innerclass so starts with BaseDAOTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDAOTest.xml", fileName);
    }


    public void testGetExpectedDataSetFileName() throws Exception {

        String fileName = baseDAOTestCase.getExpectedDataSetFileName();

        //innerclass so starts with BaseDAOTestCaseTest$
        StringAssert.assertEndsWith("ConcreteBaseDAOTest.testName-result.xml", fileName);
    }


    public void testUpdateDatabaseSchemaIfNeeded() throws Exception {
//   todo implement
//        baseDAOTestCase.updateDatabaseSchemaIfNeeded();
//
//        assertNotNull(baseDAOTestCase.getProperties());
//        assertNotNull(baseDAOTestCase.getDataSource());
    }


    public static class ConcreteBaseDAOTest extends BaseDAOTestCase {

        public ConcreteBaseDAOTest() {
            super("testName");
        }

    }

}