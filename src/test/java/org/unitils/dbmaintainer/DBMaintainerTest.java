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
package org.unitils.dbmaintainer;

import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.ExecutedScript;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptContentHandle;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.DefaultScriptRunner;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.version.ExecutedScriptInfoSource;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.ArgumentMatchers;
import org.unitils.mock.argumentmatcher.ArgumentMatcher;

/**
 * Tests the main algorithm of the DBMaintainer, using mocks for all implementation classes.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainerTest extends UnitilsJUnit4 {

//    @InjectIntoByType
    org.unitils.mock.Mock<ExecutedScriptInfoSource> mockExecutedScriptInfoSource;

//    @InjectIntoByType
    org.unitils.mock.Mock<ScriptSource> mockScriptSource;

//    @InjectIntoByType
    org.unitils.mock.Mock<DefaultScriptRunner> mockScriptRunner;

//    @InjectIntoByType
    org.unitils.mock.Mock<DBClearer> mockDbClearer;

//    @InjectIntoByType
    org.unitils.mock.Mock<ConstraintsDisabler> mockConstraintsDisabler;

//    @InjectIntoByType
    org.unitils.mock.Mock<SequenceUpdater> mockSequenceUpdater;

//    @InjectIntoByType
    org.unitils.mock.Mock<DataSetStructureGenerator> mockDataSetStructureGenerator;

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
        dbMaintainer.versionSource = mockExecutedScriptInfoSource.getInstance();
        dbMaintainer.scriptSource = mockScriptSource.getInstance();
        dbMaintainer.scriptRunner = mockScriptRunner.getInstance();
        dbMaintainer.dbClearer = mockDbClearer.getInstance();
        dbMaintainer.constraintsDisabler = mockConstraintsDisabler.getInstance();
        dbMaintainer.sequenceUpdater = mockSequenceUpdater.getInstance();
        dbMaintainer.dataSetStructureGenerator = mockDataSetStructureGenerator.getInstance();

        scripts = new ArrayList<Script>();
        Script script1 = new Script("01_script1.sql", 0L, createScriptContentHandle());
		scripts.add(script1);
        Script script2 = new Script("02_script2.sql", 0L, createScriptContentHandle());
		scripts.add(script2);

        alreadyExecutedScripts = new ArrayList<ExecutedScript>();
        alreadyExecutedScripts.add(new ExecutedScript(script1, null, true));
        alreadyExecutedScripts.add(new ExecutedScript(script2, null, true));
        
        postProcessingScripts = new ArrayList<Script>();
        postProcessingScripts.add(new Script("post-script1.sql", 0L, createScriptContentHandle()));
        postProcessingScripts.add(new Script("post-script2.sql", 0L, createScriptContentHandle()));
        
        mockExecutedScriptInfoSource.returns(new HashSet<ExecutedScript>(alreadyExecutedScripts)).getExecutedScripts();
    }


    private ScriptContentHandle createScriptContentHandle() {
        return new DummyScriptContentHandle();
    }


    @Test
    public void testNoUpdateNeeded() {
        // Set database version and available script expectations
        expectNoScriptModifications();
        expectPostProcessingScripts(postProcessingScripts);

        dbMaintainer.updateDatabase();
        
        assertNoMoreInvocations();
    }


    /**
     * Tests incremental update of a database: No existing scripts are modified, but new ones are added. The database
     * is not cleared but the new scripts are executed on by one, incrementing the database version each time.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase_Incremental() throws Exception {
        expectNewScriptsAdded();
        expectPostProcessingScripts(postProcessingScripts);

        dbMaintainer.updateDatabase();
        
        assertScriptsExecutedAndDbVersionSet();
    }


    /**
     * Tests updating the database from scratch: Existing scripts have been modified. The database is cleared first
     * and all scripts are executed.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateDatabase_FromScratch() throws Exception {
        expectExistingScriptModified();
        expectPostProcessingScripts(postProcessingScripts);

        dbMaintainer.updateDatabase();
        
        mockDbClearer.assertInvoked().clearSchemas();
        mockExecutedScriptInfoSource.assertInvoked().clearAllExecutedScripts();
        assertScriptsExecutedAndDbVersionSet();
    }


    @Test
    public void testUpdateDatabase_LastUpdateFailed() {
        expectLastUpdateFailed();
        expectPostProcessingScripts(postProcessingScripts);

        dbMaintainer.updateDatabase();
        
        mockDbClearer.assertInvoked().clearSchemas();
        mockExecutedScriptInfoSource.assertInvoked().clearAllExecutedScripts();
        assertScriptsExecutedAndDbVersionSet();
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
        mockScriptRunner.raises(new UnitilsException("Test exception")).execute(scripts.get(0).getScriptContentHandle());

        try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
        
        mockExecutedScriptInfoSource.assertInvoked().registerExecutedScript(new ExecutedScript(scripts.get(0), null, false));
    }

    @Test
    public void testUpdateDatabase_ErrorInPostProcessingCodeScripts() {
        // Set database version and available script expectations
        expectNewScriptsAdded();
        expectPostProcessingScripts(postProcessingScripts);
        mockScriptRunner.raises(new UnitilsException("Test exception")).execute(ArgumentMatchers.same(postProcessingScripts.get(1).getScriptContentHandle()));

        try {
            dbMaintainer.updateDatabase();
            fail("A UnitilsException should have been thrown");
        } catch (UnitilsException e) {
            // Expected
        }
        
        assertScriptsExecutedAndDbVersionSet();
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


    private void assertScriptsExecutedAndDbVersionSet() {
    	mockExecutedScriptInfoSource.assertInvokedInOrder().registerExecutedScript(new ExecutedScript(scripts.get(0), null, null));
        mockScriptRunner.assertInvokedInOrder().execute(scripts.get(0).getScriptContentHandle());
        mockExecutedScriptInfoSource.assertInvokedInOrder().updateExecutedScript(new ExecutedScript(scripts.get(0), null, null));
        mockExecutedScriptInfoSource.assertInvokedInOrder().registerExecutedScript(new ExecutedScript(scripts.get(1), null, null));
        mockScriptRunner.assertInvokedInOrder().execute(scripts.get(1).getScriptContentHandle());
        mockExecutedScriptInfoSource.assertInvokedInOrder().updateExecutedScript(new ExecutedScript(scripts.get(1), null, null));
        mockScriptRunner.assertInvokedInOrder().execute(postProcessingScripts.get(0).getScriptContentHandle());
        mockScriptRunner.assertInvokedInOrder().execute(postProcessingScripts.get(1).getScriptContentHandle());
    }


    private void expectNewScripts(List<Script> scripts) {
        mockScriptSource.returns(scripts).getNewScripts(null, null);
    }


    private void expectErrorInExistingIndexedScript() {
        alreadyExecutedScripts.get(0).setSuccessful(false);
    }


    private void expectModifiedScripts(boolean modifiedScripts) {
        mockScriptSource.returns(modifiedScripts).isExistingIndexedScriptModified(null, null);
    }


    private void expectPostProcessingScripts(List<Script> postProcessingCodeScripts) {
        mockScriptSource.returns(postProcessingCodeScripts).getPostProcessingScripts();
    }


    private void expectAllScripts(List<Script> scripts) {
        mockScriptSource.returns(scripts).getAllUpdateScripts();
    }
    
    private static class DummyScriptContentHandle extends ScriptContentHandle {

        @Override
        protected InputStream getScriptInputStream() {
            return null;
        }
        
    }

}
