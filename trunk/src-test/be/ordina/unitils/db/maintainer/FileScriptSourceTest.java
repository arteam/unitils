/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer;

import be.ordina.unitils.db.maintainer.script.FileScriptSource;
import be.ordina.unitils.util.PropertiesUtils;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class FileScriptSourceTest extends TestCase {

    private static final String DBCHANGE_FILE_CLASSPATH = "be/ordina/unitils/db/maintainer/script1.sql";

    private static final String DBCHANGE_FILE_FILESYSTEM = System.getProperty("java.io.tmpdir") + "script1.sql";

    private FileScriptSource fileDBChangeSource;

    protected void setUp() throws Exception {
        super.setUp();
        Properties testProperties = PropertiesUtils.loadClassProperties(this.getClass());
        fileDBChangeSource = new FileScriptSource();
        fileDBChangeSource.init(testProperties);
        copyFile();
    }

    private void copyFile() throws Exception {
        //todo if file not found => NullPointer exception --> fix with proper checks
        InputStream is = ClassLoader.getSystemResourceAsStream(DBCHANGE_FILE_CLASSPATH);
        OutputStream os = new FileOutputStream(DBCHANGE_FILE_FILESYSTEM);
        IOUtils.copy(is, os);
        is.close();
        os.close();
    }

    public void testGetNextDbChange() {
        //todo implement test
//        String script1contents = fileDBChangeSource.getScript(1L); // Should load script1.sql
//        assertEquals("Contents of script 1", script1contents);
    }

    public void testGetNextDbChange_noMoreChanges() {
        String script2contents = fileDBChangeSource.getScript(2L); // There is no script2.sql, should return null
        assertNull(script2contents);
    }

}
