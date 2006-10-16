package org.unitils.dbmaintainer.maintainer.script;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.unitils.core.UnitilsConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;
import org.unitils.reflectionassert.ReflectionAssert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Filip Neven
 */
public class FileScriptSourceTest extends TestCase {

    private static final String DBCHANGE_FILE_DIRECTORY = System.getProperty("java.io.tmpdir") + "/FileScriptSourceTest/";

    private static final String DBCHANGE_FILE1 = "001_script.sql";

    private static final String DBCHANGE_FILE2 = "002_script.sql";

    private static final String DBCHANGE_FILE1_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE1;

    private static final String DBCHANGE_FILE2_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE2;

    private Version versionIndex0, versionIndex1, versionIndex2, versionTimestampOld;

    private ScriptSource fromScratchFileScriptSource;

    private long file2Timestamp;

    private static ReflectionAssert reflectionAssert = new ReflectionAssert();

    protected void setUp() throws Exception {
        super.setUp();

        // Clean up test directory
        File testDir = new File(DBCHANGE_FILE_DIRECTORY);
        FileUtils.deleteDirectory(testDir);
        testDir.mkdirs();

        // Copy test files
        File f1 = copyFile(DBCHANGE_FILE1, DBCHANGE_FILE1_FILESYSTEM);
        long file1Timestamp = f1.lastModified();
        File f2 = copyFile(DBCHANGE_FILE2, DBCHANGE_FILE2_FILESYSTEM);
        file2Timestamp = f2.lastModified();

        // Initialize version objects
        versionIndex0 = new Version(0L, file2Timestamp);
        versionIndex1 = new Version(1L, file2Timestamp);
        versionIndex2 = new Version(2L, file2Timestamp);
        versionTimestampOld = new Version(1L, file1Timestamp - 1L);

        // Initialize FileScriptSourceObject
        Configuration configuration = new UnitilsConfigurationLoader().loadConfiguration();
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_DIR, DBCHANGE_FILE_DIRECTORY);
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_FILEEXTENSION, "sql");
        fromScratchFileScriptSource = new FileScriptSource();
        fromScratchFileScriptSource.init(configuration);
    }

    private File copyFile(String fileInClassPath, String systemPath) throws Exception {
        //todo if file not found => NullPointer exception --> fix with proper checks
        InputStream is = getClass().getResourceAsStream(fileInClassPath);
        OutputStream os = new FileOutputStream(systemPath);
        IOUtils.copy(is, os);
        is.close();
        os.close();
        return new File(systemPath);
    }

    public void testGetScripts_incremental_fromVersionIndex0() {
        List<VersionScriptPair> scripts = fromScratchFileScriptSource.getScripts(versionIndex0); // Should load script1.sql and script2.sql
        checkScript1(scripts.get(0));
        checkScript2(scripts.get(1));
    }

    public void testGetScripts_incremental_fromVersionIndex1() {
        List<VersionScriptPair> scripts = fromScratchFileScriptSource.getScripts(versionIndex1); // Should load script2.sql
        checkScript2(scripts.get(0));
    }

    public void testGetScripts_noMoreChanges() {
        List<VersionScriptPair> scripts = fromScratchFileScriptSource.getScripts(versionIndex2); // There is no script2.sql, should return null
        assertTrue(scripts.isEmpty());
    }

    public void testGetNextDbChange_fromScratch() {
        List<VersionScriptPair> scripts = fromScratchFileScriptSource.getScripts(versionTimestampOld);
        checkScript1(scripts.get(0));
        checkScript2(scripts.get(1));
    }

    public void testShouldRunFromScratch_incremental() {
        assertFalse(fromScratchFileScriptSource.shouldRunFromScratch(versionIndex0));
        assertFalse(fromScratchFileScriptSource.shouldRunFromScratch(versionIndex1));
        assertFalse(fromScratchFileScriptSource.shouldRunFromScratch(versionIndex2));
    }

    public void testShouldRunFromScratch_fromScratch() {
        assertTrue(fromScratchFileScriptSource.shouldRunFromScratch(versionTimestampOld));
    }

    private void checkScript1(VersionScriptPair versionScriptPair) {
        reflectionAssert.assertEquals(new VersionScriptPair(new Version(1L, file2Timestamp), "Contents of script 1"),
                versionScriptPair);
    }

    private void checkScript2(VersionScriptPair versionScriptPair) {
        reflectionAssert.assertEquals(new VersionScriptPair(new Version(2L, file2Timestamp), "Contents of script 2"),
                versionScriptPair);
    }


}
