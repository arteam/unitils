package org.unitils.dbmaintainer.maintainer.script;

import org.unitils.dbmaintainer.maintainer.VersionScriptPair;
import org.unitils.dbmaintainer.maintainer.version.Version;
import org.unitils.util.PropertiesUtils;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author Filip Neven
 */
public class FileScriptSourceTest extends TestCase {

    private static final String DBCHANGE_FILE_DIRECTORY = System.getProperty("java.io.tmpdir") + "/FileScriptSourceTest";

    private static final String DBCHANGE_FILE1_CLASSPATH = "/be/ordina/unitils/dbmaintainer/maintainer/script/001_script.sql";

    private static final String DBCHANGE_FILE2_CLASSPATH = "/be/ordina/unitils/dbmaintainer/maintainer/script/002_script.sql";

    private static final String DBCHANGE_FILE1_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + "/001_script.sql";

    private static final String DBCHANGE_FILE2_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + "/002_script.sql";

    private static final String[][] scriptSourceProperties = {
            {"dbMaintainer.fileScriptSource.dir", DBCHANGE_FILE_DIRECTORY},
            {"dbMaintainer.fileScriptSource.fileExtension", "sql"}
    };

    private Version versionIndex0, versionIndex1, versionIndex2, versionTimestampOld;

    private ScriptSource fromScratchFileScriptSource;
    private long file2Timestamp;

    protected void setUp() throws Exception {
        super.setUp();

        // Clean up test directory
        File testDir = new File(DBCHANGE_FILE_DIRECTORY);
        FileUtils.deleteDirectory(testDir);
        testDir.mkdirs();

        // Copy test files
        File f1 = copyFile(DBCHANGE_FILE1_CLASSPATH, DBCHANGE_FILE1_FILESYSTEM);
        long file1Timestamp = f1.lastModified();
        File f2 = copyFile(DBCHANGE_FILE2_CLASSPATH, DBCHANGE_FILE2_FILESYSTEM);
        file2Timestamp = f2.lastModified();

        // Initialize version objects
        versionIndex0 = new Version(0L, file2Timestamp);
        versionIndex1 = new Version(1L, file2Timestamp);
        versionIndex2 = new Version(2L, file2Timestamp);
        versionTimestampOld = new Version(1L, file1Timestamp - 1L);

        // Initialize FileScriptSourceObject
        Properties testProperties = PropertiesUtils.asProperties(scriptSourceProperties);
        fromScratchFileScriptSource = new FileScriptSource();
        //fromScratchFileScriptSource.init(testProperties); //todo implement
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

    private void checkScript1(VersionScriptPair script) {
        checkScript(script, 1L, file2Timestamp, "Contents of script 1");
    }

    private void checkScript2(VersionScriptPair script) {
        checkScript(script, 2L, file2Timestamp, "Contents of script 2");
    }

    private void checkScript(VersionScriptPair script, long versionIndex, long versionTimestamp, String scriptContents) {
        assertEquals(new Long(versionIndex), new Long(script.getVersion().getIndex()));
        assertEquals(new Long(versionTimestamp), new Long(script.getVersion().getTimeStamp()));
        assertEquals(scriptContents, script.getScript());
    }
}
