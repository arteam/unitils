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
package org.unitils.dbmaintainer;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.expectLastCall;
import org.unitils.UnitilsJUnit3;
import org.unitils.dbmaintainer.clean.DBClearer;
import org.unitils.dbmaintainer.script.Script;
import org.unitils.dbmaintainer.script.ScriptSource;
import org.unitils.dbmaintainer.script.impl.SQLScriptRunner;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import org.unitils.dbmaintainer.structure.ConstraintsDisabler;
import org.unitils.dbmaintainer.structure.DtdGenerator;
import org.unitils.dbmaintainer.structure.SequenceUpdater;
import org.unitils.dbmaintainer.version.Version;
import org.unitils.dbmaintainer.version.VersionScriptPair;
import org.unitils.dbmaintainer.version.VersionSource;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the main algorithm of the DBMaintainer, using mocks for all implementation classes.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBMaintainerTest extends UnitilsJUnit3 {

    @Mock
    @InjectIntoByType
    private VersionSource mockVersionSource = null;

    @Mock
    @InjectIntoByType
    private ScriptSource mockScriptSource = null;

    @Mock
    @InjectIntoByType
    private SQLScriptRunner mockScriptRunner = null;

    @Mock
    @InjectIntoByType
    private DBClearer mockDbClearer = null;

    @Mock
    @InjectIntoByType
    private ConstraintsDisabler mockConstraintsDisabler = null;

    @Mock
    @InjectIntoByType
    private SequenceUpdater mockSequenceUpdater = null;

    @Mock
    @InjectIntoByType
    private DtdGenerator mockDtdGenerator = null;

    @TestedObject
    private DBMaintainer dbMaintainer;

    /* Test database update scripts */
    private List<VersionScriptPair> versionScriptPairs;

    /* Test database versions */
    private Version version0, version1, version2;


    /**
     * Create an instance of DBMaintainer
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        dbMaintainer = new DBMaintainer();
        dbMaintainer.fromScratchEnabled = true;
        dbMaintainer.keepRetryingAfterError = true;

        versionScriptPairs = new ArrayList<VersionScriptPair>();
        version0 = new Version(0L, 0L);
        version1 = new Version(1L, 1L);
        version2 = new Version(2L, 2L);
        versionScriptPairs.add(new VersionScriptPair(version1, new Script("script1.sql", "Script 1")));
        versionScriptPairs.add(new VersionScriptPair(version2, new Script("script2.sql", "Script 2")));
    }


    /**
     * Tests incremental update of a database: No existing scripts are modified, but new ones are added. The database
     * is not cleared but the new scripts are executed on by one, incrementing the database version each time.
     */
    public void testUpdateDatabase_incremental() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(version0);
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(false);
        expect(mockVersionSource.isLastUpdateSucceeded()).andReturn(true);
        expect(mockScriptSource.getNewScripts(version0)).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(version1);
        mockVersionSource.registerUpdateSucceeded(true);
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(version2);
        mockVersionSource.registerUpdateSucceeded(true);
        mockConstraintsDisabler.disableConstraints();
        mockSequenceUpdater.updateSequences();
        mockDtdGenerator.generateDtd();
        expect(mockScriptSource.getAllCodeScripts()).andReturn(new ArrayList<Script>());
        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }


    /**
     * Tests updating the database from scratch: Existing scripts have been modified. The database is cleared first
     * and all scripts are executed.
     */
    public void testUpdateDatabase_fromScratch() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(version0);
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(true);
        mockDbClearer.clearSchema();
        expect(mockScriptSource.getAllScripts()).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(version1);
        mockVersionSource.registerUpdateSucceeded(true);
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(version2);
        mockVersionSource.registerUpdateSucceeded(true);
        mockConstraintsDisabler.disableConstraints();
        mockSequenceUpdater.updateSequences();
        mockDtdGenerator.generateDtd();
        expect(mockScriptSource.getAllCodeScripts()).andReturn(new ArrayList<Script>());
        replay();

        // Execute test
        dbMaintainer.updateDatabase();
    }


    /**
     * Tests the behavior in case there is an error in a script supplied by the ScriptSource. In this case, the
     * database version must not org incremented and a StatementHandlerException must be thrown.
     */
    public void testUpdateDatabase_errorInScript() throws Exception {
        expect(mockVersionSource.getDbVersion()).andReturn(version0).anyTimes();
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(false);
        expect(mockVersionSource.isLastUpdateSucceeded()).andReturn(true);
        expect(mockScriptSource.getNewScripts(version0)).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        expectLastCall().andThrow(new StatementHandlerException("Test exception"));
        mockVersionSource.setDbVersion(version1);
        mockVersionSource.registerUpdateSucceeded(false);
        replay();

        try {
            dbMaintainer.updateDatabase();
            fail("A StatementHandlerException should have been thrown");
        } catch (StatementHandlerException e) {
            // Expected
        }
    }


    /**
     * Tests checking from scratch update but no scripts modified and last update was successful.
     */
    public void testCheckUpdateDatabaseFromScratch_notNeeded() throws Exception {
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(false);
        expect(mockVersionSource.isLastUpdateSucceeded()).andReturn(true);
        replay();

        boolean result = dbMaintainer.updateDatabaseFromScratch(version1);
        assertFalse(result);
    }


    /**
     * Tests checking from scratch update. Needed because scripts were modified.
     */
    public void testCheckUpdateDatabaseFromScratch_modifiedScripts() throws Exception {
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(true);
        replay();

        boolean result = dbMaintainer.updateDatabaseFromScratch(version1);
        assertTrue(result);
    }


    /**
     * Tests checking from scratch update. Needed because last update failed.
     */
    public void testCheckUpdateDatabaseFromScratch_lastUpdateFailed() throws Exception {
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(false);
        expect(mockVersionSource.isLastUpdateSucceeded()).andReturn(false);
        replay();

        boolean result = dbMaintainer.updateDatabaseFromScratch(version1);
        assertTrue(result);
    }


    /**
     * Tests checking from scratch update. Last update failed but no scripts were modified and
     * keepRetryingAfterError is true.
     */
    public void testCheckUpdateDatabaseFromScratch_lastUpdateFailedButIgnored() throws Exception {
        dbMaintainer.keepRetryingAfterError = false;
        expect(mockScriptSource.existingScriptsModified(version0)).andReturn(false);
        expect(mockVersionSource.isLastUpdateSucceeded()).andReturn(false);
        replay();

        boolean result = dbMaintainer.updateDatabaseFromScratch(version1);
        assertFalse(result);
    }

}
