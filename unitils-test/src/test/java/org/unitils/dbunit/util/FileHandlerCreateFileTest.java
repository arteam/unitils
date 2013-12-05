package org.unitils.dbunit.util;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbunit.util.FileHandler;


/**
 * Test {@link FileHandler#createTempFile(String)}.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FileHandlerCreateFileTest {

    private FileHandler sut;
    
    @Before
    public void setUp() {
        sut = new FileHandler();
    }
    
    @Test
    public void testCreateFile() {
        String resourceName = "/org/unitils/testdata/exampleResourceData.xml";
        File actual = sut.createTempFile(resourceName);
    
        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getAbsolutePath().toLowerCase().startsWith("c:\\temp\\exampleresourcedata-"));
    
    
        //delete temp file
        actual.delete();
    }
    
    @Test
    public void testCreateFileWithSlashAtTheEnd() throws Exception {
        String resourceName = "/org/unitils/testdata/exampleResourceData.xml/";
        File actual = sut.createTempFile(resourceName);
    
        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getAbsolutePath().toLowerCase().startsWith("c:\\temp\\exampleresourcedata-"));
    
    
        //delete temp file
        actual.delete();
    }
    
    @Test
    public void testCreateFileWithoutSlashes() throws Exception {
        String resourceName = "exampleResourceData.xml";
        File actual = sut.createTempFile(resourceName);
    
        Assert.assertTrue(actual.exists());
        Assert.assertTrue(actual.getAbsolutePath().toLowerCase().startsWith("c:\\temp\\exampleresourcedata-"));
        
        //delete temp file
        actual.delete();
    }
    
    @Test
    public void testUnableToCreateFile() {
        String resourceName = "1.xml";
        Assert.assertNull(sut.createTempFile(resourceName));
    
    }
    

}
