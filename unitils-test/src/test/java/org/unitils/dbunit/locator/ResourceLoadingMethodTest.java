/*
 * CVS file status:
 * 
 * $$Id: ResourceLoadingMethodTest.java,v 1.2 2010-12-20 09:54:18 tdr Exp $$
 * 
 * Copyright (c) Smals
 */
package org.unitils.dbunit.locator;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.DbUnitModule;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

/**
 * Unit test for simple App.
 * 
 * @author tdr
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ResourceLoadingMethodTest {

    private static final Logger LOGGER = Logger.getLogger(ResourceLoadingMethodTest.class);

    /** */
    @DataSet("org/unitils/testdata/exampleResourceData.xml")
    @Test
    public void testLoadingResource() {
        Assert.assertTrue(true);
    }

    /** */
    @DataSet("../testdata/exampleResourceData.xml")
    @Test
    public void testLoadingDataset() {
        Assert.assertTrue(true);
    }

    /** */
    @DataSet
    @Test
    @Ignore
    @ExpectedDataSet
    public void testLoadingResourceDatasetDefault() {
        LOGGER.debug("STVE :" + DbUnitModule.class.getPackage().toString());
        Assert.assertTrue(true);
    }

    /** */
    @DataSet
    @Test
    public void testLoadingDatasetDefault() {

        Assert.assertTrue(true);
    }

    /** */
    @DataSet("org/unitils/testdata/exampleResourceData.xml")
    public void testLoadingResourceDataFile() {
        Assert.assertTrue(true);
    }

    /** */
    @Test
    @DataSet({
        "org/unitils/testdata/exampleResourceData.xml", "org/unitils/testdata/exampleResourceData.xml"
    })
    public void testLoadingResourceMultipleDataFiles() {
        Assert.assertTrue(true);
    }

    /** */
    @Test
    @Ignore
    @DataSet({
        "org/unitils/testdata/exampleResourceData.xml", "org/unitils/testdata/exampleResourceData.xml"
    })
    @ExpectedDataSet({
        "org/unitils/testdata/exampleResourceData.xml", "org/unitils/testdata/exampleResourceData.xml"
    })
    public void testLoadingExpectedResourceMultipleDataFiles() {
        Assert.assertTrue(true);
    }
    
    /** */
    @DataSet("org/unitils/testdata/exampleResourceData.xml/")
    @Test
    public void testLoadingResourceWithSlashAtTheEnd() {
        Assert.assertTrue(true);
    }
}
