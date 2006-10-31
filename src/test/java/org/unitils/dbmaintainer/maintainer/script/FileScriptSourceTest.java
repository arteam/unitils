package org.unitils.dbmaintainer.maintainer.script;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Tests the FileScriptSource
 */
public class FileScriptSourceTest extends TestCase {

    /**
     * Temp dir where test script files are put during the tests
     */
    private static final String DBCHANGE_FILE_DIRECTORY = System.getProperty("java.io.tmpdir") + "/FileScriptSourceTest/";

    /**
     * First test script file
     */
    private static final String DBCHANGE_FILE1 = "001_script.sql";

    /**
     * Second test script file
     */
    private static final String DBCHANGE_FILE2 = "002_script.sql";

    /**
     * Path of first test script file on the file system
     */
    private static final String DBCHANGE_FILE1_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE1;

    /**
     * Path of second test script file on the file system
     */
    private static final String DBCHANGE_FILE2_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE2;

    private Version versionIndex0, versionIndex1, versionIndex2, versionTimestampOld;

    /**
     * Tested object
     */
    private ScriptSource fileScriptSource;

    private long file2Timestamp;

    /**
     * Cleans test directory and copies test files to it. Initializes test objects
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        // Clean up test directory
        File testDir = new File(DBCHANGE_FILE_DIRECTORY);
        FileUtils.deleteDirectory(testDir);
        testDir.mkdirs();

        // Copy test files
        File f1 = copyFile(DBCHANGE_FILE1, DBCHANGE_FILE1_FILESYSTEM);
        File f2 = copyFile(DBCHANGE_FILE2, DBCHANGE_FILE2_FILESYSTEM);
        file2Timestamp = f2.lastModified();

        // Initialize version objects
        versionIndex0 = new Version(0L, file2Timestamp);
        versionIndex1 = new Version(1L, file2Timestamp);
        versionIndex2 = new Version(2L, file2Timestamp);
        versionTimestampOld = new Version(1L, 0L);

        // Initialize FileScriptSourceObject
        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_DIR, DBCHANGE_FILE_DIRECTORY);
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_FILEEXTENSION, "sql");
        fileScriptSource = new FileScriptSource();
        fileScriptSource.init(configuration);
    }

    /**
     * Copies file from classpath to the given system path
     *
     * @param fileInClassPath
     * @param systemPath
     * @return
     * @throws Exception
     */
    private File copyFile(String fileInClassPath, String systemPath) throws Exception {
        InputStream is = getClass().getResourceAsStream(fileInClassPath);
        OutputStream os = new FileOutputStream(systemPath);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return new File(systemPath);
    }

    /**
     * Tests wether the FileScriptSource indicates that no existing scripts are modified when the current version is
     * one of the existing file versions, containing the existing file's timestamps.
     */
    public void testExistingScriptsModfied_notModified() {
        assertFalse(fileScriptSource.existingScriptsModified(versionIndex0));
        assertFalse(fileScriptSource.existingScriptsModified(versionIndex1));
        assertFalse(fileScriptSource.existingScriptsModified(versionIndex2));
    }

    /**
     * Tests wether the FileScriptSource indicates that one or more existing script are modified when the current version
     * has a timestamp older than the scripts
     */
    public void testExistingScriptsModfied_modified() {
        assertTrue(fileScriptSource.existingScriptsModified(versionTimestampOld));
    }

    /**
     * Tests that script 1 and script 2 are returned when the current version is version 0
     */
    public void testGetNewScripts_fromVersionIndex0() {
        List<VersionScriptPair> scripts = fileScriptSource.getNewScripts(versionIndex0); // Should load script1.sql and script2.sql
        checkScript1(scripts.get(0));
        checkScript2(scripts.get(1));
    }

    /**
     * Tests that script 2 is returned when the current version is version 1
     */
    public void testGetNewScripts_fromVersionIndex1() {
        List<VersionScriptPair> scripts = fileScriptSource.getNewScripts(versionIndex1); // Should load script2.sql
        checkScript2(scripts.get(0));
    }

    /**
     * Verifies that nothing is returned when the current version is version 2
     */
    public void testGetNewScripts_noMoreChanges() {
        List<VersionScriptPair> scripts = fileScriptSource.getNewScripts(versionIndex2); // There is no script2.sql, should return null
        assertTrue(scripts.isEmpty());
    }

    /**
     * Verifies that script 1 and script 2 are returned when requesting all scripts
     */
    public void testGetAllScripts() {
        List<VersionScriptPair> scripts = fileScriptSource.getAllScripts();
        checkScript1(scripts.get(0));
        checkScript2(scripts.get(1));
    }

    /**
     * Checks if script 1 is returned with the correct version
     *
     * @param versionScriptPair
     */
    private void checkScript1(VersionScriptPair versionScriptPair) {
        assertRefEquals(new VersionScriptPair(new Version(1L, file2Timestamp), "Contents of script 1"), versionScriptPair);
    }

     /**
     * Checks if script 1 is returned with the correct version
     *
     * @param versionScriptPair
     */
    private void checkScript2(VersionScriptPair versionScriptPair) {
        assertRefEquals(new VersionScriptPair(new Version(2L, file2Timestamp), "Contents of script 2"), versionScriptPair);
    }


}
