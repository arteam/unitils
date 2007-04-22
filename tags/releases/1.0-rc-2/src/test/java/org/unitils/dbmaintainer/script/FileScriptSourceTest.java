/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.script;

import junit.framework.TestCase;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.script.impl.FileScriptSource;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDeleteOnExit;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.copy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * Tests the FileScriptSource
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class FileScriptSourceTest extends TestCase {

    /* Temp dir where test script files are put during the tests */
    private static final String DBCHANGE_FILE_DIRECTORY = System.getProperty("java.io.tmpdir") + "/FileScriptSourceTest/";

    /* First test script file */
    private static final String DBCHANGE_FILE1 = "001_script.sql";

    /* Second test script file */
    private static final String DBCHANGE_FILE2 = "002_script.sql";

    /* Path of first test script file on the file system */
    private static final String DBCHANGE_FILE1_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE1;

    /* Path of second test script file on the file system */
    private static final String DBCHANGE_FILE2_FILESYSTEM = DBCHANGE_FILE_DIRECTORY + DBCHANGE_FILE2;

    /* Version objects: represent the different versions of the scripts and the database */
    private Version versionIndex0, versionIndex1, versionIndex2, versionTimestampOld;

    /* Tested object */
    private FileScriptSource fileScriptSource;

    /* The timestamp of the DBCHANGE_FILE2 file. */
    private long file2Timestamp;


    /**
     * Cleans test directory and copies test files to it. Initializes test objects
     */
    protected void setUp() throws Exception {
        super.setUp();

        // Create test directory
        File testDir = new File(DBCHANGE_FILE_DIRECTORY);
        testDir.mkdirs();
        forceDeleteOnExit(testDir);

        // Copy test files
        copyFile(DBCHANGE_FILE1, DBCHANGE_FILE1_FILESYSTEM);
        File f2 = copyFile(DBCHANGE_FILE2, DBCHANGE_FILE2_FILESYSTEM);
        file2Timestamp = f2.lastModified();

        // Initialize version objects
        versionIndex0 = new Version(0L, file2Timestamp);
        versionIndex1 = new Version(1L, file2Timestamp);
        versionIndex2 = new Version(2L, file2Timestamp);
        versionTimestampOld = new Version(1L, 0L);

        // Initialize FileScriptSourceObject
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_LOCATIONS, DBCHANGE_FILE_DIRECTORY);
        configuration.setProperty(FileScriptSource.PROPKEY_SCRIPTFILES_FILEEXTENSION, "sql");

        fileScriptSource = new FileScriptSource();
        fileScriptSource.doInit(configuration);
    }


    /**
     * Copies file from classpath to the given system path
     *
     * @param fileInClassPath the from file name, not null
     * @param systemPath      the to file, not null
     * @return the to file, not null
     */
    private File copyFile(String fileInClassPath, String systemPath) throws Exception {
        InputStream is = getClass().getResourceAsStream(fileInClassPath);
        OutputStream os = new FileOutputStream(systemPath);
        copy(is, os);
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
     * @param versionScriptPair the version and script to check, not null
     */
    private void checkScript1(VersionScriptPair versionScriptPair) {
        assertRefEquals(new VersionScriptPair(new Version(1L, file2Timestamp), new Script(DBCHANGE_FILE1, "Contents of script 1")), versionScriptPair);
    }


    /**
     * Checks if script 1 is returned with the correct version
     *
     * @param versionScriptPair the version and script to check, not null
     */
    private void checkScript2(VersionScriptPair versionScriptPair) {
        assertRefEquals(new VersionScriptPair(new Version(2L, file2Timestamp), new Script(DBCHANGE_FILE2, "Contents of script 2")), versionScriptPair);
    }

}
