package org.unitils.dbmaintainer.resourcepickingstrategie;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.dbmaintainer.locator.resourcepickingstrategie.impl.UniqueMostRecentPickingStrategie;
import org.unitils.reflectionassert.ReflectionAssert;


/**
 * Test  {@link UniqueMostRecentPickingStrategie}.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ResourcePickingStrategieTest {
    
    private String pathSeperator;

private UniqueMostRecentPickingStrategie strategy;
    
    @Before
    public void init() {
        strategy = new UniqueMostRecentPickingStrategie();
        if (System.getProperty("os.name").startsWith("Windows")) {
            pathSeperator = "\\";
        } else {
            pathSeperator = "/";
        }
    }

    @Test
    public void testAddMostRecentDuplicateFile() throws IOException {
        List<URL> filteredResources = new ArrayList<URL>();
        File file1 = new File("src/test/resources/org/unitils/dbunit/test1/testFile.txt");
        
        URL urlFile1 = new URL("file:///" + file1.getAbsolutePath());
        filteredResources.add(urlFile1);
        
        String resourceSearchName = file1.getAbsolutePath().substring(file1.getAbsolutePath().lastIndexOf(pathSeperator) + 1);
        
        strategy.addMostRecent(filteredResources, urlFile1, resourceSearchName);
        Assert.assertEquals(1, filteredResources.size());
        ReflectionAssert.assertLenientEquals(Arrays.asList(urlFile1), filteredResources);
    }
    
    @Ignore
    @Test
    public void testAddMostRecentDifferentFiles() throws Exception {
        List<URL> filteredResources = new ArrayList<URL>();
        File file1 = new File("src/test/resources/org/unitils/dbunit/test1/testFile.txt");
        File file2 = new File("src/test/resources/org/unitils/dbunit/test2/testFile.txt");
        
        URL urlFile1 = new URL("file:///" + file1.getAbsolutePath());
        URL urlFile2 = new URL("file:///" + file2.getAbsolutePath());
        filteredResources.add(urlFile1);
        
        String resourceSearchName = file1.getAbsolutePath().substring(file1.getAbsolutePath().lastIndexOf(pathSeperator) + 1);
        
        strategy.addMostRecent(filteredResources, urlFile2, resourceSearchName);
    
        
        Assert.assertEquals(1, filteredResources.size());
        ReflectionAssert.assertLenientEquals(Arrays.asList(urlFile2), filteredResources);
    }
    
    @Test
    public void testAddMostRecentEmptyFilteredResources() throws Exception {
        List<URL> filteredResources = new ArrayList<URL>();
        File file1 = new File("src/test/resources/org/unitils/dbunit/test1/testFile.txt");
        
        URL urlFile1 = new URL("file:///" + file1.getAbsolutePath());
        
        String resourceSearchName = file1.getAbsolutePath().substring(file1.getAbsolutePath().lastIndexOf("\\") + 1);
        
        strategy.addMostRecent(filteredResources, urlFile1, resourceSearchName);
        Assert.assertEquals(1, filteredResources.size());
        ReflectionAssert.assertLenientEquals(Arrays.asList(urlFile1), filteredResources);
    }
    
    @Test
    public void testFilter() throws MalformedURLException {
        List<URL> filteredResources = new ArrayList<URL>();
        File file1 = new File("src/test/resources/org/unitils/dbunit/test1/testFile.txt");
        
        URL urlFile1 = new URL("file:///" + file1.getAbsolutePath());
        filteredResources.add(urlFile1);
        String resourceSearchName = file1.getAbsolutePath().substring(file1.getAbsolutePath().lastIndexOf(pathSeperator) + 1);
        
        strategy.filter(filteredResources, resourceSearchName);
        Assert.assertEquals(1, filteredResources.size());
        ReflectionAssert.assertLenientEquals(Arrays.asList(urlFile1), filteredResources);
    }
}
