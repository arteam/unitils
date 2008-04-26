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
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.DefaultScriptRunner;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DataSetStructureGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.version.Version;
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

    @Mock
    @InjectIntoByType
    protected VersionSource mockVersionSource;

    @Mock
    @InjectIntoByType
    protected ScriptSource mockScriptSource;

    @Mock
    @InjectIntoByType
    protected DefaultScriptRunner mockScriptRunner;

    @Mock
    @InjectIntoByType
    protected DBClearer mockDbClearer;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    protected ConstraintsDisabler mockConstraintsDisabler;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    protected SequenceUpdater mockSequenceUpdater;

    @Mock(calls = Calls.LENIENT)
    @InjectIntoByType
    protected DataSetStructureGenerator mockDataSetStructureGenerator;

    @TestedObject
    protected DBMaintainer dbMaintainer;

    /* Test database update scripts */
    protected List<Script> scripts, postProcessingScripts;

    /* Test database versions */
    protected Version version0, version1, version2;


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
        version0 = new Version("0", 0L);
        version1 = new Version("1", 1L);
        version2 = new Version("2", 2L);
        scripts.add(new Script("script1.sql", "content", version1));
        scripts.add(new Script("script2.sql", "content", version2));

        postProcessingScripts = new ArrayList<Script>();
        postProcessingScripts.add(new Script("post-script1.sql", "content", version0));
        postProcessingScripts.add(new Script("post-script2.sql", "content", version0));
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
        expectPostProcessingScripts(postProcessingScripts);

        // Record expected behavior
        mockVersionSource.setUpdateSucceeded(false);
        mockDbClearer.clearSchemas();
        expectExecuteScriptsAndSetDbVersion();
        mockVersionSource.setUpdateSucceeded(true);

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
        mockVersionSource.setUpdateSucceeded(false);
        mockDbClearer.clearSchemas();
        expectExecuteScriptsAndSetDbVersion();
        mockVersionSource.setUpdateSucceeded(true);

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
        mockVersionSource.setUpdateSucceeded(false);
        mockScriptRunner.execute(scripts.get(0));
        expectLastCall().andThrow(new UnitilsException("Test exception"));
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
        expectPostProcessingScripts(postProcessingScripts);

        mockVersionSource.setUpdateSucceeded(false);
        expectExecuteScriptsAndSetDbVersion();
        mockVersionSource.setUpdateSucceeded(true);
        replay();

        dbMaintainer.updateDatabase();
    }

    @Test
    public void testUpdateDatabase_ErrorInPostProcessingCodeScripts() {
        // Set database version and available script expectations
        expectNewScriptsAdded();
        expectPostProcessingScripts(postProcessingScripts);

        mockVersionSource.setUpdateSucceeded(false);
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
        expectDbVersion(version0);
        expectLastUpdateSucceeded(true);
        expectModifiedScripts(false);
        expectNewScripts(Collections.EMPTY_LIST);
    }

    private void expectNewScriptsAdded() {
        expectDbVersion(version0);
        expectLastUpdateSucceeded(true);
        expectModifiedScripts(false);
        expectNewScripts(scripts);
    }

    private void expectLastUpdateFailed() {
        expectDbVersion(version0);
        expectLastUpdateSucceeded(false);
        expectModifiedScripts(false);
        expectAllScripts(scripts);
    }

    @SuppressWarnings({"unchecked"})
    private void expectNoPostProcessingCodeScripts() {
        expectPostProcessingScripts(Collections.EMPTY_LIST);
    }


    private void expectExistingScriptModified() {
        expectDbVersion(version0);
        expectLastUpdateSucceeded(true);
        expectModifiedScripts(true);
        expectAllScripts(scripts);
    }


    private void expectExecuteScriptsAndSetDbVersion() {
        mockScriptRunner.execute(scripts.get(0));
        mockVersionSource.setDbVersion(version1);
        mockScriptRunner.execute(scripts.get(1));
        mockVersionSource.setDbVersion(version2);
        mockScriptRunner.execute(postProcessingScripts.get(0));
        mockScriptRunner.execute(postProcessingScripts.get(1));
    }


    private void expectNewScripts(List<Script> scripts) {
        expect(mockScriptSource.getNewScripts(null)).andStubReturn(scripts);
    }


    private void expectDbVersion(Version dbVersion) {
        expect(mockVersionSource.getDbVersion()).andStubReturn(dbVersion);
    }

    private void expectLastUpdateSucceeded(boolean lastUpdateSucceeded) {
        expect(mockVersionSource.isLastUpdateSucceeded()).andStubReturn(lastUpdateSucceeded);
    }


    private void expectModifiedScripts(boolean modifiedScripts) {
        expect(mockScriptSource.isExistingScriptModified(null)).andStubReturn(modifiedScripts);
    }


    private void expectPostProcessingScripts(List<Script> postProcessingCodeScripts) {
        expect(mockScriptSource.getPostProcessingScripts()).andStubReturn(postProcessingCodeScripts);
    }


    private void expectAllScripts(List<Script> scripts) {
        expect(mockScriptSource.getAllScripts()).andReturn(scripts);
    }

}
