package org.unitils.io;

import junit.framework.Assert;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.io.annotation.TemporaryFile;
import org.unitils.io.annotation.TemporaryFolder;

import java.io.File;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */

public class IOModuleIntegrationTemporaryFileTest extends UnitilsJUnit4 {
    @TemporaryFile
    private File defaultFile;

    @TemporaryFolder
    private File defaultFolder;

    @TemporaryFile(value = "CustomIoModuleTestFile")
    private File customFile;

    @TemporaryFolder(value = "CustomIoModuleTestFolder")
    private File customFolder;

    @Test
    public void testTheSetup() {
        assertFile(defaultFile, true, "IOModuleIntegrationTemporaryFileTesttestTheSetup");
        assertFile(defaultFolder, false, "IOModuleIntegrationTemporaryFileTesttestTheSetup");
        assertFile(customFile, true, "CustomIoModuleTestFile");
        assertFile(customFolder, false, "CustomIoModuleTestFolder");

    }

    public void assertFile(File f, Boolean isFile, String fileName) {
        System.out.println(f.getAbsolutePath());

        Assert.assertNotNull(f.getAbsolutePath() + " should not be null", f);
        Assert.assertTrue(f.getAbsolutePath() + " should exist", f.exists());
        Assert.assertEquals(isFile, (Boolean) f.isFile());
        Assert.assertTrue(f.getAbsolutePath() + " should contain " + fileName, f.getAbsolutePath().contains(fileName));
    }


}
