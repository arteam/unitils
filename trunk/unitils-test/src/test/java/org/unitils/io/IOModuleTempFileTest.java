package org.unitils.io;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.io.annotation.TempFile;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOModuleTempFileTest extends UnitilsJUnit4 {

    @TempFile
    private File defaultFile;

    @TempFile(value = "customFile.tmp")
    private File customFile;


    @Test
    public void defaultTempFile() {
        assertTrue(defaultFile.isFile());
        assertEquals(IOModuleTempFileTest.class.getName() + "-defaultTempFile.tmp", defaultFile.getName());
    }

    @Test
    public void customTempFile() {
        assertTrue(customFile.isFile());
        assertEquals("customFile.tmp", customFile.getName());

    }
}
