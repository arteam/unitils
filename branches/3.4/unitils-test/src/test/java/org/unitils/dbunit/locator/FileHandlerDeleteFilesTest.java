package org.unitils.dbunit.locator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link FileHandler#deleteFiles(List)}.
 * 
 * @author wiw
 * 
 * @since 3.4
 * 
 */
public class FileHandlerDeleteFilesTest {

    private FileHandler sut = new FileHandler();
    
    @Test
    public void testDeleteFiles() throws IOException {
        List<File> lstFiles = new ArrayList<File>();
        File tempFile1 = File.createTempFile("FileHandlerDeleteFilesTest-", ".txt");
        File tempFile2 = File.createTempFile("FileHandlerDeleteFilesTest-", ".txt");
        File tempFile3 = File.createTempFile("FileHandlerDeleteFilesTest-", ".txt");
        lstFiles.add(tempFile1);
        lstFiles.add(tempFile2);
        lstFiles.add(tempFile3);
        
        
        tempFile3.delete();
        
        
        sut.deleteFiles(lstFiles);
        
        Assert.assertFalse(tempFile1.exists());
        Assert.assertFalse(tempFile2.exists());
        Assert.assertFalse(tempFile3.exists());
    }

}
