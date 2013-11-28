package org.unitils.dbunit.locator;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;

/**
 * Unit test for simple App.
 * 
 * @author tdr
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
@DataSet
public class ResourceLoadingDefaultClassTest {

    /** */
    @Test
    @ExpectedDataSet("org/unitils/testdata/exampleResourceLoadingData.xml")
    public void testLoadingResource() {
        Assert.assertTrue(true);
    }


}
