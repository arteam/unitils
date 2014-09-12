/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dbmaintainer.script.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.copyDirectory;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.copyFile;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDeleteOnExit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * Tests the DefaultScriptSource
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class DefaultScriptSourceTest extends UnitilsJUnit4 {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /* DataSource for the test database */
    @TestDataSource
    DataSource dataSource = null;

    /* Tested object */
    DefaultScriptSource scriptSource;

    String scriptsDirName;

    List<ExecutedScript> alreadyExecutedScripts;

    Date executionDate;

    private List<String> schemas;
    private String dialect;
    
    private Properties configuration;

    /**
     * Cleans test directory and copies test files to it. Initializes test objects
     */
    @Before
    public void setUp() throws Exception {
        executionDate = new Date();
        dialect = "oracle";
        // Create test directories
        String tmpDir = System.getProperty("java.io.tmpdir");
        if(!tmpDir.endsWith("/")) {
            tmpDir += "/";
        }
        scriptsDirName = tmpDir + "DefaultScriptSourceTest";
        forceDeleteOnExit(new File(scriptsDirName));

        // Copy test files
        copyDirectory(new File(getClass().getResource("DefaultScriptSourceTest").toURI()), new File(scriptsDirName));

        alreadyExecutedScripts = new ArrayList<ExecutedScript>(asList(
            getExecutedScript("1_scripts/001_scriptA.sql"),
            getExecutedScript("1_scripts/002_scriptB.sql"),
            getExecutedScript("2_scripts/002_scriptE.sql"),
            getExecutedScript("2_scripts/scriptF.sql"),
            getExecutedScript("2_scripts/subfolder/001_scriptG.sql"),
            getExecutedScript("2_scripts/subfolder/scriptH.sql"),
            getExecutedScript("scripts/001_scriptI.sql"),
            getExecutedScript("scripts/scriptJ.sql")
            ));

        // Initialize FileScriptSource object
        configuration = new Properties();
        String scriptsLocations = scriptsDirName + "/test_scripts";
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, scriptsLocations);
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_EXTENSIONS, "sql");
        configuration.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_DIRNAME, "postprocessing");
        configuration.setProperty(DefaultScriptSource.PROPKEY_USESCRIPTFILELASTMODIFICATIONDATES, "false");
        configuration.setProperty("dbMaintainer.generateDataSetStructure.enabled", "true");
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2, exclude3");
        
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2, exclude3");

        scriptSource = new DefaultScriptSource();
        scriptSource.init(configuration);
        schemas = new ArrayList<String>();
        schemas.add("public");
    }


    private ExecutedScript getExecutedScript(String scriptFileName) throws NoSuchAlgorithmException, IOException {
        return new ExecutedScript(new Script(scriptFileName, 0L, getCheckSum(scriptFileName)), executionDate, true);
    }


    private String getCheckSum(String fileName) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        InputStream is = new DigestInputStream(new FileInputStream(scriptsDirName + "/test_scripts/" + fileName), digest);

        while (is.read() != -1);
        return getHexPresentation(digest.digest());
    }

    private String getHexPresentation(byte[] byteArray) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            result.append(Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }


    /**
     * Tests getting all scripts in the correct order.
     */
    @Test
    public void testGetAllUpdateScripts() {
        List<Script> scripts = scriptSource.getAllUpdateScripts(dialect, schemas.get(0), true);

        assertEquals("1_scripts/001_scriptA.sql", scripts.get(0).getFileName());   // x.1.1
        assertEquals("1_scripts/002_scriptB.sql", scripts.get(1).getFileName());   // x.1.2
        assertEquals("1_scripts/scriptD.sql", scripts.get(2).getFileName());       // x.1.x
        assertEquals("2_scripts/002_scriptE.sql", scripts.get(3).getFileName());   // x.2.2
        assertEquals("2_scripts/scriptF.sql", scripts.get(4).getFileName());       // x.2.x
        assertEquals("2_scripts/subfolder/001_scriptG.sql", scripts.get(5).getFileName());   // x.2.x.1
        assertEquals("2_scripts/subfolder/scriptH.sql", scripts.get(6).getFileName());       // x.2.x.x
        assertEquals("scripts/001_scriptI.sql", scripts.get(7).getFileName());   // x.x.1
        assertEquals("scripts/scriptJ.sql", scripts.get(8).getFileName());       // x.x.x
    }

    @Test
    public void testDuplicateIndex() throws Exception {
        File duplicateIndexScript = null;
        try {
            File scriptA = new File(scriptsDirName
                + "/test_scripts/1_scripts/001_scriptA.sql");
            duplicateIndexScript = new File(scriptsDirName
                + "/test_scripts/1_scripts/001_duplicateIndexScript.sql");
            copyFile(scriptA, duplicateIndexScript);
            try {
                scriptSource.getAllUpdateScripts(dialect, schemas.get(0), true);
                fail("Expected a UnitilsException because of a duplicate script");
            } catch (UnitilsException e) {
                // expected
            }
        } finally {
            try {
                duplicateIndexScript.delete();
            } catch(Exception e) {
                // Safely ignore NPE or any IOException...
            }
        }
    }


    /**
     * Tests getting all scripts that have an index higher than the highest of the already executed scripts or
     * whose content has changed.
     */
    @Test
    public void testGetNewScripts() {
        alreadyExecutedScripts.set(5, new ExecutedScript(new Script("2_scripts/subfolder/scriptH.sql", 0L, "xxx"), executionDate, true));

        List<Script> scripts = scriptSource.getNewScripts(new Version("2.x.1"), new HashSet<ExecutedScript>(alreadyExecutedScripts),dialect, schemas.get(0), true);

        assertEquals("1_scripts/scriptD.sql", scripts.get(0).getFileName());       			// x.1.x 		was added
        assertEquals("2_scripts/subfolder/scriptH.sql", scripts.get(1).getFileName());      // x.2.x.x		was changed
        assertEquals("scripts/001_scriptI.sql", scripts.get(2).getFileName());   			// x.x.1		higher version
    }


    @Test
    public void testIsExistingScriptsModfied_noModifications() {
        assertFalse(scriptSource.isExistingIndexedScriptModified(new Version("x.x.x"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
    }


    @Test
    public void testIsExistingScriptsModfied_modifiedScript() {
        alreadyExecutedScripts.set(1, new ExecutedScript(new Script("1_scripts/002_scriptB.sql", 0L, "xxx"), executionDate, true));

        assertTrue(scriptSource.isExistingIndexedScriptModified(new Version("x.x.x"), new HashSet<ExecutedScript>(alreadyExecutedScripts),dialect, schemas.get(0), true));
    }


    @Test
    public void testIsExistingScriptsModfied_scriptAdded() {
        alreadyExecutedScripts.remove(1);

        assertTrue(scriptSource.isExistingIndexedScriptModified(new Version("x.x.x"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
    }


    @Test
    public void testIsExistingScriptsModfied_scriptRemoved() {
        alreadyExecutedScripts.add(new ExecutedScript(new Script("1_scripts/003_scriptB.sql", 0L, "xxx"), executionDate, true));

        assertTrue(scriptSource.isExistingIndexedScriptModified(new Version("x.x.x"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
    }


    @Test
    public void testIsExistingScriptsModfied_newScript() {
        alreadyExecutedScripts.remove(1);

        assertFalse(scriptSource.isExistingIndexedScriptModified(new Version("1.1"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
    }


    @Test
    public void testIsExistingScriptsModfied_higherIndexScriptModified() {
        alreadyExecutedScripts.set(1, new ExecutedScript(new Script("1_scripts/002_scriptB.sql", 0L, "xxx"), executionDate, true));

        assertFalse(scriptSource.isExistingIndexedScriptModified(new Version("1.1"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
        assertTrue(scriptSource.isExistingIndexedScriptModified(new Version("1.2"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true));
    }


    /**
     * Test whether an existing script was modified script but all scripts have a higher version than the current version.
     */
    @Test
    public void testIsExistingScriptsModfied_noLowerIndex() {
        boolean result = scriptSource.isExistingIndexedScriptModified(new Version("0"), new HashSet<ExecutedScript>(alreadyExecutedScripts), dialect, schemas.get(0), true);
        assertFalse(result);
    }


    /**
     * Test getting the post processing scripts.
     */
    @Test
    public void testGetPostProcessingScripts() {
        List<Script> scripts = scriptSource.getPostProcessingScripts(dialect, schemas.get(0), true);
        assertEquals("postprocessing/post-scriptA.sql", scripts.get(0).getFileName());
        assertEquals("postprocessing/post-scriptB.sql", scripts.get(1).getFileName());
    }

    @Test
    public void testCheckIfFileMustBeAddedToScriptList() throws Exception {
        String schema1 = "USERS";
        String schema2 = "pEoplE";

        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("test123.sql", "public", false));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("test123.sql", "public", true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("@users_addusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("@usersaddusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("1@users_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("1_@users_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@users_addusers.sql", schema1, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("01@users_addusers.sql", schema1, true));

        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("01@users_addpeople.sql", schema2, true));
        Assert.assertFalse(scriptSource.checkIfScriptContainsCorrectDatabaseName("1@people_addusers.sql", schema1, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@people_addusers.sql", schema2, true));
        Assert.assertTrue(scriptSource.checkIfScriptContainsCorrectDatabaseName("01_@people_addUsers.sql", schema2, true));
    }

    /**
     * test {@link DefaultScriptSource#checkIfThereAreNoQualifiers(String)}
     * @throws Exception
     */
    @Test
    public void testCheckIfThereAreNoQualifiers() throws Exception {
        Assert.assertTrue(scriptSource.checkIfThereAreNoQualifiers("01_products.sql"));
        Assert.assertFalse(scriptSource.checkIfThereAreNoQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.checkIfThereAreNoQualifiers("#refdata_#postgres_products.sql"));
    }

    @Test
    public void testContainsOneOfQualifiers_withoutIncludes() throws Exception {
        scriptSource = new DefaultScriptSource();
        Properties configuration = new Properties();
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2, exclude3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2, exclude3");

        scriptSource.init(configuration);

        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_#include2_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("#include1_#include2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#include1_#exclude2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#exclude1_products.sql"));
    }

    @Test
    public void testContainsOneOfQualifiers_withIncludes() throws Exception {
        scriptSource = new DefaultScriptSource();
        Properties configuration = new Properties();
        configuration.setProperty(DefaultScriptSource.PROPKEY_QUALIFIERS, "include1, include2, include3, exclude1, exclude2, exclude3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        configuration.setProperty(DefaultScriptSource.PROPKEY_EXCLUDE_QUALIFIERS, "exclude1, exclude2, exclude3");

        scriptSource.init(configuration);

        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("01_#include1_#include2_products.sql"));
        Assert.assertTrue(scriptSource.containsOneOfQualifiers("#include1_#include2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("#refdata_#postgres_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#include1_#exclude2_products.sql"));
        Assert.assertFalse(scriptSource.containsOneOfQualifiers("01_#exclude1_products.sql"));
    }

    @Test
    public void testGetScriptsAt_multiUserSupport() throws Exception {
        File parentFile = tempFolder.newFolder("test1");
        tempFolder.newFile("test1/file1.txt");
        tempFolder.newFile("test1/file2.sql");
        tempFolder.newFile("test1/@users_addusers.sql");
        tempFolder.newFile("test1/01_@users_addusers.sql");
        tempFolder.newFile("test1/1@people_addusers.sql");


        List<Script> actual = new ArrayList<Script>();

        scriptSource.getScriptsAt(actual, parentFile.getParentFile().getAbsolutePath(), "test1", "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }

        Assert.assertEquals(3, actual.size());
        ReflectionAssert.assertReflectionEquals(Arrays.asList("test1/file2.sql", "test1/@users_addusers.sql", "test1/01_@users_addusers.sql"), actualNames, ReflectionComparatorMode.LENIENT_ORDER);

    }

    @Test
    public void testGetScriptsAt_qualifiers() throws Exception {
        String nameFolder = "getscriptsat_qualifiers";
        File parentFile = tempFolder.newFolder(nameFolder);

        tempFolder.newFile(nameFolder + "/01_#include1_products.sql");
        tempFolder.newFile(nameFolder + "/#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#refdata_#postgres_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#exclude2_products.sql");
        
        scriptSource = new DefaultScriptSource();
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        scriptSource.init(configuration);

        
        List<Script> actual = new ArrayList<Script>();

        scriptSource.getScriptsAt(actual, parentFile.getParentFile().getAbsolutePath(), nameFolder, "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }

        Assert.assertEquals(3, actualNames.size());

        ReflectionAssert.assertLenientEquals(Arrays.asList(nameFolder + "/01_#include1_products.sql", nameFolder + "/#include1_#include2_products.sql", nameFolder + "/01_#include1_#include2_products.sql"), actualNames);
    }
    
    @Test
    public void testGetScriptsAt_qualifiersAndMultiUserSupport_defaultDatabase() throws Exception {
        String nameFolder = "getscriptsat";
        File parentFile = tempFolder.newFolder(nameFolder);
        
        
        tempFolder.newFile(nameFolder + "/01_#include1_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include2_@users_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include2_@people_products.sql");
        
        tempFolder.newFile(nameFolder + "/#include1_@people_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/@users_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#include2_products.sql");
        tempFolder.newFile(nameFolder + "/01_#refdata_#postgres_products.sql");
        tempFolder.newFile(nameFolder + "/01_#include1_#exclude2_products.sql");
        
        List<Script> actual = new ArrayList<Script>();
        
        scriptSource = new DefaultScriptSource();
        configuration.setProperty(DefaultScriptSource.PROPKEY_INCLUDE_QUALIFIERS, "include1, include2, include3");
        scriptSource.init(configuration);

        scriptSource.getScriptsAt(actual, parentFile.getParentFile().getAbsolutePath(), nameFolder, "users", true);
        List<String> actualNames = new ArrayList<String>();
        for (Script script : actual) {
            actualNames.add(script.getFileName());
        }
        
        List<String> expected = new ArrayList<String>();
        expected.add(nameFolder + "/01_#include1_products.sql");
        expected.add(nameFolder + "/01_#include2_@users_products.sql");
        expected.add(nameFolder + "/@users_#include1_#include2_products.sql");
        expected.add(nameFolder + "/#include1_#include2_products.sql");
        expected.add(nameFolder + "/01_#include1_#include2_products.sql");
        
        Assert.assertEquals(5, actual.size());
        ReflectionAssert.assertReflectionEquals(expected, actualNames, ReflectionComparatorMode.LENIENT_ORDER);
    }
}
