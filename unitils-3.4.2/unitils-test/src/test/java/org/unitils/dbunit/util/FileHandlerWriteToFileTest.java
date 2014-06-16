package org.unitils.dbunit.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junitx.framework.FileAssert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;


/**
 * Test {@link FileHandler#writeToFile(java.io.File, java.io.InputStream)}.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FileHandlerWriteToFileTest {
    
    private File expectedFile;
    
    private FileHandler sut;

    @Before
    public void setUp() {
        expectedFile = new File("src/test/resources/org/unitils/dbunit/test1/testFile.txt");
        sut = new FileHandler();
    }
    
    @Test
    public void testOk() throws IOException {
        File actualFile = File.createTempFile("FileHandlerWriteToFileTest-", ".txt");
        sut.writeToFile(actualFile, new FileInputStream(expectedFile));
        FileAssert.assertBinaryEquals(expectedFile, actualFile);
    }
    
    @Test
    public void testMarkSupported() throws IOException {
        File actualFile = File.createTempFile("FileHandlerWriteToFileTest-", ".txt");
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(expectedFile));
        sut.writeToFile(actualFile, inputStream);
        
        FileAssert.assertBinaryEquals(expectedFile, actualFile);
    }
    
    @Test
    public void testException() throws IOException {
        File actualFile = File.createTempFile("FileHandlerWriteToFileTest-", ".txt");
        BufferedInputStream inputStream = new BufferedInputStream(null);
        sut.writeToFile(actualFile, inputStream);
    }
    
    

}
