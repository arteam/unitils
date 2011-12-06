package org.unitils.io.temp.impl;


import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.unitils.util.FileUtils.writeStringToFile;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class DefaultTempServiceCreateTempFileTest {

    /* Tested object */
    private DefaultTempService defaultTempService;

    private File rootTempDir;


    @Before
    public void initialize() throws IOException {
        rootTempDir = new File("target/" + DefaultTempServiceCreateTempFileTest.class.getSimpleName());

        defaultTempService = new DefaultTempService(rootTempDir);
        defaultTempService.deleteTempFileOrDir(rootTempDir);
        rootTempDir.mkdirs();
    }


    @Test
    public void createTempFile() {
        File result = defaultTempService.createTempFile("tempFile.tmp");
        File expected = new File(rootTempDir, "tempFile.tmp");

        assertEquals(expected, result);
        assertTrue(result.exists());
        assertTrue(result.isFile());
    }

    @Test
    public void rootTempDirIsCreatedWhenItDoesNotExist() {
        rootTempDir.delete();

        File result = defaultTempService.createTempFile("tempFile.tmp");
        assertTrue(result.exists());
    }

    @Test
    public void fileIsDeletedIfItAlreadyExists() throws Exception {
        File existingFile = defaultTempService.createTempFile("tempFile.tmp");
        writeStringToFile(existingFile, "test");

        File result = defaultTempService.createTempFile("tempFile.tmp");
        assertTrue(result.exists());
        assertEquals(0, result.length());
    }

    @Test(expected = UnitilsException.class)
    public void invalidFileName() {
        defaultTempService.createTempFile("x::://\\^@,.?@#.tmp");
    }

    @Test(expected = UnitilsException.class)
    public void existingFileInUse() throws Exception {
        File existingFile = defaultTempService.createTempFile("tempFile.tmp");
        FileOutputStream out = new FileOutputStream(existingFile);
        try {
            defaultTempService.createTempFile("tempFile.tmp");
        } finally {
            out.close();
        }
    }

}
