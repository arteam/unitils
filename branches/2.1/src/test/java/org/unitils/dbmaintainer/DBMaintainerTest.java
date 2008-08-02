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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.replay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.DefaultScriptRunner;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.version.ExecutedScriptInfoSource;
import org.unitils.easymock.annotation.Mock;
import org.unitils.easymock.util.Calls;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

/**
 * Tests the main algorithm of the DBMaintainer, using mocks for all implementation classes.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainerTest extends UnitilsJUnit4 {

    @Mock
    @InjectIntoByType
    ExecutedScriptInfoSource mockVersionSource;

    @Mock
    @InjectIntoByType
    ScriptSource mockScriptSource;

    @Mock
    @InjectIntoByType
    DefaultScriptRunner mockScriptRunner;

    @Mock
    @InjectIntoByType
    DBClearer mockDbClearer;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    ConstraintsDisabler mockConstraintsDisabler;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    SequenceUpdater mockSequenceUpdater;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    DataSetStructureGenerator mockDataSetStructureGenerator;

    @TestedObject
    DBMaintainer dbMaintainer;

    /* Test database update scripts */
    List<Script> scripts, postProcessingScripts;
    
    List<ExecutedScript> alreadyExecutedScripts;


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

        scripts = new ArrayList<Script>();
        Script script1 = new Script("01_script1.sql", 0L, "checksum1");
		scripts.add(script1);
        Script script2 = new Script("script2.sql", 0L, "checksum2");
		scripts.add(script2);

        alreadyExecutedScripts = new ArrayList<ExecutedScript>();
        alreadyExecutedScripts.add(new ExecutedScript(script1, null, true));
        alreadyExecutedScripts.add(new ExecutedScript(script2, null, true));
        
        postProcessingScripts = new ArrayList<Script>();
        postProcessingScripts.add(new Script("post-script1.sql", 0L, "post-checksum1"));
        postProcessingScripts.add(new Script("post-script2.sql", 0L, "post-checksum2"));
        
        expect(mockVersionSource.getExecutedScripts()).andStubReturn(new HashSet<ExecutedScript>(alreadyExecutedScripts));
    }

    @Test
    public void testNoUpdateNeeded() {
        // Set database version and available script expectations
        expectNoScriptModifications();
        expectPostProcessingScripts(postProcessingScripts);

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
        expectPostProcessingScripts(postProcessingScripts);

        // Record expected behavior
        expectExecuteScriptsAndSetDbVersion();

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
        expectPostProcessingScripts(postProcessingScripts);

        // Record expected behavior
        mockDbClearer.clearSchemas();
        mockVersionSource.clearAllExecutedScripts();
        expectExecuteScriptsAndSetDbVersion();

        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }


    @Test
    public void testUpdateDatabase_LastUpdateFailed() {
        // Set database version and available script expectations
        expectLastUpdateFailed();
        expectPostProcessingScripts(postProcessingScripts);

        // Record expected behavior
        mockDbClearer.clearSchemas();
        mockVersionSource.clearAllExecutedScripts();
        expectExecuteScriptsAndSetDbVersion();

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

        // Record expected behavior
        mockVersionSource.registerExecutedScript(new ExecutedScript(scripts.get(0), null, false));
        mockScriptRunner.execute(scripts.get(0));
        expectLastCall().andThrow(new UnitilsException("Test exception"));
        replay();

        try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
    }

    @Test
    public void testUpdateDatabase_ErrorInPostProcessingCodeScripts() {
        // Set database version and available script expectations
        expectNewScriptsAdded();
        expectPostProcessingScripts(postProcessingScripts);

        expectExecuteScriptsAndSetDbVersion();
        expectLastCall().andThrow(new UnitilsException("Test exception"));
        replay();

        try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
    }


    @SuppressWarnings({"unchecked"})
    private void expectNoScriptModifications() {
        expectModifiedScripts(false);
        expectNewScripts(Collections.EMPTY_LIST);
    }

    private void expectNewScriptsAdded() {
        expectModifiedScripts(false);
        expectNewScripts(scripts);
    }

    private void expectLastUpdateFailed() {
        expectErrorInExistingIndexedScript();
        expectModifiedScripts(false);
        expectAllScripts(scripts);
    }

    @SuppressWarnings({"unchecked"})
    private void expectNoPostProcessingCodeScripts() {
        expectPostProcessingScripts(Collections.EMPTY_LIST);
    }


    private void expectExistingScriptModified() {
        expectModifiedScripts(true);
        expectAllScripts(scripts);
    }


    private void expectExecuteScriptsAndSetDbVersion() {
    	mockVersionSource.registerExecutedScript(new ExecutedScript(scripts.get(0), null, null));
        mockScriptRunner.execute(scripts.get(0));
        mockVersionSource.updateExecutedScript(new ExecutedScript(scripts.get(0), null, null));
        mockVersionSource.registerExecutedScript(new ExecutedScript(scripts.get(1), null, null));
        mockScriptRunner.execute(scripts.get(1));
        mockVersionSource.updateExecutedScript(new ExecutedScript(scripts.get(1), null, null));
        mockScriptRunner.execute(postProcessingScripts.get(0));
        mockScriptRunner.execute(postProcessingScripts.get(1));
    }


    private void expectNewScripts(List<Script> scripts) {
        expect(mockScriptSource.getNewScripts(null, null)).andStubReturn(scripts);
    }


    private void expectErrorInExistingIndexedScript() {
        alreadyExecutedScripts.get(0).setSuccessful(false);
    }


    private void expectModifiedScripts(boolean modifiedScripts) {
        expect(mockScriptSource.isExistingIndexedScriptModified(null, null)).andStubReturn(modifiedScripts);
    }


    private void expectPostProcessingScripts(List<Script> postProcessingCodeScripts) {
        expect(mockScriptSource.getPostProcessingScripts()).andStubReturn(postProcessingCodeScripts);
    }


    private void expectAllScripts(List<Script> scripts) {
        expect(mockScriptSource.getAllUpdateScripts()).andReturn(scripts);
    }

}
