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
package org.unitils.dbmaintainer;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.expectLastCall;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.clean.DBCodeClearer;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.SQLCodeScriptRunner;
import org.unitils.dbmaintainer.script.impl.SQLScriptRunner;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;
import org.unitils.dbmaintainer.version.VersionSource;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.Calls;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tests the main algorithm of the DBMaintainer, using mocks for all implementation classes.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainerTest extends UnitilsJUnit4 {

    @Mock @InjectIntoByType
    VersionSource mockVersionSource;

    @Mock @InjectIntoByType
    ScriptSource mockScriptSource;

    @Mock @InjectIntoByType
    SQLScriptRunner mockScriptRunner;
    
    @Mock @InjectIntoByType
    SQLCodeScriptRunner mockCodeScriptRunner;

    @Mock @InjectIntoByType
    DBClearer mockDbClearer;

    @Mock @InjectIntoByType
    DBCodeClearer mockDbCodeClearer;

    @Mock(calls = Calls.LENIENT) @InjectIntoByType
    ConstraintsDisabler mockConstraintsDisabler;

    @Mock(calls = Calls.LENIENT) @InjectIntoByType
    SequenceUpdater mockSequenceUpdater;

    @Mock(calls = Calls.LENIENT) @InjectIntoByType
    DataSetStructureGenerator mockDataSetStructureGenerator;

    @TestedObject
    DBMaintainer dbMaintainer;

    /* Test database update scripts */
    List<VersionScriptPair> versionScriptPairs;

    /* Test database versions */
    Version version0, version1, version2;
    
    /* Test code scripts */
    List<Script> codeScripts;


    /**
     * Create an instance of DBMaintainer
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        dbMaintainer = new DBMaintainer();
        dbMaintainer.fromScratchEnabled = true;
        dbMaintainer.keepRetryingAfterError = true;
        dbMaintainer.disableConstraintsEnabled = true;

        versionScriptPairs = new ArrayList<VersionScriptPair>();
        version0 = new Version(0L, 0L);
        version1 = new Version(1L, 1L);
        version2 = new Version(2L, 2L);
        versionScriptPairs.add(new VersionScriptPair(version1, new Script("script1.sql", "Script 1")));
        versionScriptPairs.add(new VersionScriptPair(version2, new Script("script2.sql", "Script 2")));
        
        codeScripts = new ArrayList<Script>();
        codeScripts.add(new Script("codescript1.sql", "Codescript 1"));
        codeScripts.add(new Script("codescript2.sql", "Codescript 2"));
    }
    
    
    @Test
    public void testNoUpdateNeeded() {
    	// Set database version and available script expectations
    	expectNoScriptModifications();
    	expectPostProcessingCodeScripts(codeScripts);
    	expectNoCodeScriptModifications();
    	
    	// No expected behavior
    	replay();
    	
    	dbMaintainer.updateDatabase();
    }


    /**
     * Tests incremental update of a database: No existing scripts are modified, but new ones are added. The database
     * is not cleared but the new scripts are executed on by one, incrementing the database version each time.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase_Incremental() throws Exception {
        // Set database version and available script expectations
    	expectNewScriptsAdded();
    	expectNoPostProcessingCodeScripts();
    	expectNoCodeScriptModifications();

    	// Record expected behavior
    	mockVersionSource.setUpdateSucceeded(false);
        expectExecuteScriptsAndSetDbVersion();
        mockVersionSource.setUpdateSucceeded(true);

        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }


	/**
     * Tests updating the database from scratch: Existing scripts have been modified. The database is cleared first
     * and all scripts are executed.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase_FromScratch() throws Exception {
    	// Set database version and available script expectations
    	expectExistingScriptModified();
    	expectPostProcessingCodeScripts(codeScripts);
    	expectNoCodeScriptModifications();

    	// Record expected behavior
    	mockVersionSource.setUpdateSucceeded(false);
        mockDbClearer.clearSchemas();
        mockDbCodeClearer.clearSchemasCode();
        expectExecuteScriptsAndSetDbVersion();
        expectExecuteCodeScripts();
        mockVersionSource.setUpdateSucceeded(true);
        
        mockVersionSource.setCodeUpdateSucceeded(false);
        expectExecuteCodeScripts();
        expectSetCodeScriptsVersion();
        mockVersionSource.setCodeUpdateSucceeded(true);
        
        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }
    
    @Test
    public void testUpdateDatabase_LastUpdateFailed() {
    	// Set database version and available script expectations
    	expectLastUpdateFailed();
    	expectPostProcessingCodeScripts(codeScripts);
    	expectNoCodeScriptModifications();
    	
    	// Record expected behavior
    	mockVersionSource.setUpdateSucceeded(false);
        mockDbClearer.clearSchemas();
        mockDbCodeClearer.clearSchemasCode();
        expectExecuteScriptsAndSetDbVersion();
        expectExecuteCodeScripts();
        mockVersionSource.setUpdateSucceeded(true);
        
        mockVersionSource.setCodeUpdateSucceeded(false);
        expectExecuteCodeScripts();
        expectSetCodeScriptsVersion();
        mockVersionSource.setCodeUpdateSucceeded(true);
        
        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }


    /**
     * Tests the behavior in case there is an error in a script supplied by the ScriptSource. In this case, the
     * database version must not org incremented and a StatementHandlerException must be thrown.
     */
    @Test
    public void testUpdateDatabase_ErrorInScript() throws Exception {
    	// Set database version and available script expectations
    	expectNewScriptsAdded();
    	expectNoPostProcessingCodeScripts();
    	expectNoCodeScriptModifications();
    	
    	// Record expected behavior
    	mockVersionSource.setUpdateSucceeded(false);
    	mockScriptRunner.execute("Script 1"); expectLastCall().andThrow(new UnitilsException("Test exception"));
        mockVersionSource.setDbVersion(version1);
        replay();

        try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
    }
    
    @Test
    public void testUpdateDatabase_PostProcessingCodeScriptsAvailable() {
    	// Set database version and available script expectations
    	expectNewScriptsAdded();
    	expectPostProcessingCodeScripts(codeScripts);
    	expectNoCodeScriptModifications();
    	
    	mockVersionSource.setUpdateSucceeded(false);
    	expectExecuteScriptsAndSetDbVersion();
    	expectExecuteCodeScripts();
    	mockVersionSource.setUpdateSucceeded(true);
    	replay();
    	
    	dbMaintainer.updateDatabase();
    }
    
    @Test
    public void testUpdateDatabase_ErrorInPostProcessingCodeScripts() {
    	// Set database version and available script expectations
    	expectNewScriptsAdded();
    	expectPostProcessingCodeScripts(codeScripts);
    	
    	mockVersionSource.setUpdateSucceeded(false);
    	expectExecuteScriptsAndSetDbVersion();
    	mockCodeScriptRunner.execute("Codescript 1"); expectLastCall().andThrow(new UnitilsException("Test exception"));
    	replay();
    	
    	try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
    }
    
    @Test
    public void testUpdateDatabase_CodeScriptsUpdated() {
    	// Set database version and available script expectations
    	expectNoScriptModifications();
    	expectNoPostProcessingCodeScripts();
    	expectCodeScriptsModified();
    	
    	mockVersionSource.setCodeUpdateSucceeded(false);
    	expectExecuteCodeScripts();
    	mockVersionSource.setCodeScriptsTimestamp(1L);
    	mockVersionSource.setCodeUpdateSucceeded(true);
    	
    	replay();
    	
    	dbMaintainer.updateDatabase();
    }
    
    @Test
    public void testUpdateDatabase_ErrorInCodeScripts() {
    	// Set database version and available script expectations
    	expectNoScriptModifications();
    	expectNoPostProcessingCodeScripts();
    	expectCodeScriptsModified();

    	mockVersionSource.setCodeUpdateSucceeded(false);
    	mockCodeScriptRunner.execute("Codescript 1"); expectLastCall().andThrow(new UnitilsException("Test exception"));
    	replay();
    	
    	try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
    }

	private void expectNoScriptModifications() {
		expectDbVersion(version0);
    	expectLastUpdateSucceeded(true);
    	expectModifiedScripts(false);
    	expectNewScripts(Collections.EMPTY_LIST);
	}

	private void expectNewScriptsAdded() {
		expectDbVersion(version0);
    	expectLastUpdateSucceeded(true);
    	expectModifiedScripts(false);
    	expectNewScripts(versionScriptPairs);
	}
	
	private void expectLastUpdateFailed() {
		expectDbVersion(version0);
    	expectLastUpdateSucceeded(false);
    	expectModifiedScripts(false);
    	expectAllScripts(versionScriptPairs);
	}
	
	private void expectNoPostProcessingCodeScripts() {
		expectPostProcessingCodeScripts(Collections.EMPTY_LIST);
	}

	private void expectNoCodeScriptModifications() {
		expectDbCodeVersion(0L);
    	expectLastCodeUpdateSucceeded(true);
    	expectCodeScriptsTimestamp(0L);
    	expectAllCodeScripts(codeScripts);
	}
	
	private void expectCodeScriptsModified() {
		expectDbCodeVersion(0L);
    	expectLastCodeUpdateSucceeded(true);
    	expectCodeScriptsTimestamp(1L);
    	expectAllCodeScripts(codeScripts);
	}
	
	private void expectExistingScriptModified() {
		expectDbVersion(version0);
    	expectLastUpdateSucceeded(true);
    	expectModifiedScripts(true);
    	expectAllScripts(versionScriptPairs);
	}
	
	private void expectExecuteScriptsAndSetDbVersion() {
		mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(version1);
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(version2);
	}
	
	private void expectExecuteCodeScripts() {
		mockCodeScriptRunner.execute("Codescript 1");
		mockCodeScriptRunner.execute("Codescript 2");
	}
	
	private void expectSetCodeScriptsVersion() {
		mockVersionSource.setCodeScriptsTimestamp(0L);
	}
    
    
    private void expectNewScripts(List<VersionScriptPair> versionScriptPairs) {
		expect(mockScriptSource.getNewScripts(null)).andStubReturn(versionScriptPairs);
	}


    private void expectDbVersion(Version dbVersion) {
    	expect(mockVersionSource.getDbVersion()).andStubReturn(dbVersion);
	}
    
    private void expectLastUpdateSucceeded(boolean lastUpdateSucceeded) {
    	expect(mockVersionSource.isLastUpdateSucceeded()).andStubReturn(lastUpdateSucceeded);
    }
	
    private void expectDbCodeVersion(long dbCodeTimestamp) {
    	expect(mockVersionSource.getCodeScriptsTimestamp()).andStubReturn(dbCodeTimestamp);
	}
    
    private void expectLastCodeUpdateSucceeded(boolean lastCodeUpdateSucceeded) {
    	expect(mockVersionSource.isLastCodeUpdateSucceeded()).andStubReturn(lastCodeUpdateSucceeded);
    }
	
    private void expectModifiedScripts(boolean modifiedScripts) {
		expect(mockScriptSource.isExistingScriptsModified(null)).andStubReturn(modifiedScripts);
	}
	
    private void expectPostProcessingCodeScripts(List<Script> postProcessingCodeScripts) {
		expect(mockScriptSource.getAllPostProcessingCodeScripts()).andStubReturn(postProcessingCodeScripts);
	}
	
    private void expectCodeScriptsTimestamp(long codeScriptsTimestamp) {
		expect(mockScriptSource.getCodeScriptsTimestamp()).andStubReturn(codeScriptsTimestamp);
	}
    
    private void expectAllCodeScripts(List<Script> allCodeScripts) {
		expect(mockScriptSource.getAllCodeScripts()).andStubReturn(allCodeScripts);
	}

	private void expectAllScripts(List<VersionScriptPair> versionScriptPairs) {
		expect(mockScriptSource.getAllScripts()).andReturn(versionScriptPairs);
	}

}
