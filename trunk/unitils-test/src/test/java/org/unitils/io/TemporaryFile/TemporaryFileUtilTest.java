package org.unitils.io.TemporaryFile;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TemporaryFileUtilTest {

    private TemporaryFileUtil fileUtil;

    private File parentFolder;

    @Before
    public void setup() throws IOException {
        parentFolder = new File("target/test-classes/tmp");

        org.unitils.thirdparty.org.apache.commons.io.FileUtils.deleteDirectory(parentFolder);
        parentFolder.mkdirs();

        fileUtil = new TemporaryFileUtil(parentFolder);
    }

    @Test
    public void createTemporaryFileTest() {

        File result = fileUtil.createTemporaryFile("file-createTemporaryFileTest.tmp");
        File expected = new File(parentFolder, "file-createTemporaryFileTest.tmp");

        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getAbsolutePath(), result.getAbsolutePath());
        Assert.assertTrue("The folder should have been created", expected.exists());
        Assert.assertTrue(expected.isFile());
    }

    @Test
    public void createTemporaryFolderTest() {

        File result = fileUtil.createTemporaryFolder("folder-createTemporaryFolderTest.tmp");
        File expected = new File(parentFolder, "folder-createTemporaryFolderTest.tmp");

        Assert.assertNotNull(result);
        Assert.assertEquals(expected.getAbsolutePath(), result.getAbsolutePath());
        Assert.assertTrue("The folder should have been created", expected.exists());
        Assert.assertTrue(expected.isDirectory());
    }

    @Test
    public void removeTemporaryFileTest() throws IOException {
        File file = new File(parentFolder, "removeTemporaryFileTest.txt");
        file.createNewFile();

        fileUtil.removeTemporaryFile(file);
        Assert.assertFalse("File should have been removed", file.exists());

    }

    @Test
    public void removeTemporaryFolderTest() throws IOException {
        File file = new File(parentFolder, "removeTemporaryFolderTest");
        file.mkdirs();

        fileUtil.removeTemporaryFile(file);
        Assert.assertFalse("File should have been removed", file.exists());

    }


}
