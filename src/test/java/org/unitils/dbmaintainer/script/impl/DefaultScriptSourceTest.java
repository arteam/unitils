/*
 * Copyright 2006-2007,  Unitils.org
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.version.Version;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.copyDirectory;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.forceDeleteOnExit;
import static org.unitils.thirdparty.org.apache.commons.io.FileUtils.copyFile;

import javax.sql.DataSource;
import java.io.File;

import static java.lang.Long.MAX_VALUE;
import java.util.List;
import java.util.Properties;

/**
 * Tests the DefaultScriptSource
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultScriptSourceTest extends UnitilsJUnit4 {

    /* DataSource for the test database */
    @TestDataSource
    DataSource dataSource = null;

    /* Tested object */
    DefaultScriptSource defaultScriptSource;

    String scriptsDirName;

    /**
     * Cleans test directory and copies test files to it. Initializes test objects
     */
    @Before
    public void setUp() throws Exception {
        // Create test directories
        scriptsDirName = System.getProperty("java.io.tmpdir") + "DefaultScriptSourceTest";
        forceDeleteOnExit(new File(scriptsDirName));

        // Copy test files
        copyDirectory(new File(getClass().getResource("DefaultScriptSourceTest").toURI()), new File(scriptsDirName));
        
        // Initialize FileScriptSource object
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        String scriptLocations = scriptsDirName + "/test_scripts";
        configuration.setProperty(DefaultScriptSource.PROPKEY_SCRIPT_LOCATIONS, scriptLocations);
        String postProcessingScripts = scriptsDirName + "/post_processing_scripts/post-scriptA.sql, " + scriptsDirName + "/post_processing_scripts/post-scriptB.sql";
        configuration.setProperty(DefaultScriptSource.PROPKEY_POSTPROCESSINGSCRIPT_LOCATIONS, postProcessingScripts);
        defaultScriptSource = new DefaultScriptSource();
        defaultScriptSource.init(configuration, new SQLHandler(dataSource));
    }


    /**
     * Test getting the highest not null index and highest modification timestamp
     */
    @Test
    public void testGetHighestVersion() {
        Version result = defaultScriptSource.getHighestVersion();
        assertEquals("x.x.1", result.getIndexesString());
        assertTrue(result.getTimeStamp() > 0);
    }


    /**
     * Tests getting all scripts in the correct order.
     */
    @Test
    public void testGetAllScripts() {
        List<Script> scripts = defaultScriptSource.getAllScripts();

        assertEquals("001_scriptA.sql", scripts.get(0).getName());   // x.1.1
        assertEquals("002_scriptB.sql", scripts.get(1).getName());   // x.1.2
        assertEquals("scriptD.sql", scripts.get(2).getName());       // x.1.x
        assertEquals("002_scriptE.sql", scripts.get(3).getName());   // x.2.2
        assertEquals("scriptF.sql", scripts.get(4).getName());       // x.2.x
        assertEquals("001_scriptG.sql", scripts.get(5).getName());   // x.2.x.1
        assertEquals("scriptH.sql", scripts.get(6).getName());       // x.2.x.x
        assertEquals("001_scriptI.sql", scripts.get(7).getName());   // x.x.1
        assertEquals("scriptJ.sql", scripts.get(8).getName());       // x.x.x
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
				defaultScriptSource.getAllScripts();
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
     * Tests getting all scripts that have an index higher and timestamp.
     */
    @Test
    public void testGetNewScripts() {
        List<Script> scripts = defaultScriptSource.getNewScripts(new Version("x.2.2", 0));

        assertEquals("scriptD.sql", scripts.get(0).getName());       // x.1.x
        assertEquals("scriptF.sql", scripts.get(1).getName());       // x.2.x
        assertEquals("001_scriptG.sql", scripts.get(2).getName());   // x.2.x.1
        assertEquals("scriptH.sql", scripts.get(3).getName());       // x.2.x.x
        assertEquals("001_scriptI.sql", scripts.get(4).getName());   // x.x.1
        assertEquals("scriptJ.sql", scripts.get(5).getName());       // x.x.x
    }


    /**
     * Tests getting all scripts that have an index higher than a given index.
     * Only scripts with indexes are returned, all scripts without an index have a lower modification timestamp.
     */
    @Test
    public void testGetNewScripts_indexesOnly() {
        List<Script> scripts = defaultScriptSource.getNewScripts(new Version("x.2.2", MAX_VALUE));

        assertEquals("001_scriptG.sql", scripts.get(0).getName());   // x.2.x.1
        assertEquals("001_scriptI.sql", scripts.get(1).getName());   // x.x.1
    }


    /**
     * Tests getting all scripts that have a modification timestamp higher than a given timestamp.
     * Only scripts without indexes are returned, all scripts with an index have a higher index.
     */
    @Test
    public void testGetNewScripts_noIndexesOnly() {
        List<Script> scripts = defaultScriptSource.getNewScripts(new Version("x.x.x", 0));

        assertEquals("scriptD.sql", scripts.get(0).getName());       // x.1.x
        assertEquals("scriptF.sql", scripts.get(1).getName());       // x.2.x
        assertEquals("scriptH.sql", scripts.get(2).getName());       // x.2.x.x
        assertEquals("scriptJ.sql", scripts.get(3).getName());       // x.x.x
    }


    /**
     * Verifies that nothing is returned when no new script is found
     */
    @Test
    public void testGetNewScripts_noNewScripts() {
        List<Script> scripts = defaultScriptSource.getNewScripts(new Version("x.x.x", MAX_VALUE));
        assertTrue(scripts.isEmpty());
    }


    /**
     * Test whether an existing script was modified script.
     */
    @Test
    public void testIsExistingScriptsModfied() {
        boolean result = defaultScriptSource.isExistingScriptModified(new Version("x.x.x", 0));
        assertTrue(result);
    }


    /**
     * Test whether an existing script was modified script but all scripts have a higher version than the current version.
     */
    @Test
    public void testIsExistingScriptsModfied_noLowerIndex() {
        boolean result = defaultScriptSource.isExistingScriptModified(new Version("0", 0));
        assertFalse(result);
    }


    /**
     * Test whether an existing script was modified script but no script has a higher modification timestamp.
     */
    @Test
    public void testIsExistingScriptsModfied_noHigherTimestamp() {
        boolean result = defaultScriptSource.isExistingScriptModified(new Version("x.x.x", MAX_VALUE));
        assertFalse(result);
    }


    /**
     * Test getting the post processing scripts.
     */
    @Test
    public void testGetPostProcessingScripts() {
        List<Script> scripts = defaultScriptSource.getPostProcessingScripts();
        assertEquals("post-scriptA.sql", scripts.get(0).getName());
        assertEquals("post-scriptB.sql", scripts.get(1).getName());
    }
}
