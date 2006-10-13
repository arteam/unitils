/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer;

import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.expectLastCall;
import org.unitils.dbmaintainer.clear.DBClearer;
import org.unitils.dbmaintainer.constraints.ConstraintsDisabler;
import org.unitils.dbmaintainer.dtd.DtdGenerator;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.maintainer.script.ScriptSource;
import org.unitils.dbmaintainer.maintainer.version.Version;
import org.unitils.dbmaintainer.maintainer.version.VersionSource;
import org.unitils.dbmaintainer.script.SQLScriptRunner;
import org.unitils.dbmaintainer.sequences.SequenceUpdater;
import org.unitils.easymock.EasyMockTestCase;
import org.unitils.easymock.annotation.Mock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 */
public class DBMaintainerTest extends EasyMockTestCase {

    @Mock
    private VersionSource mockVersionSource = null;
    @Mock
    private ScriptSource mockScriptSource = null;
    @Mock
    private SQLScriptRunner mockScriptRunner = null;
    @Mock
    private DBClearer mockDbClearer = null;
    @Mock
    private ConstraintsDisabler mockConstraintsDisabler = null;
    @Mock
    private SequenceUpdater mockSequenceUpdater = null;
    @Mock
    private DtdGenerator mockDtdGenerator = null;

    /* Tested object */
    private DBMaintainer dbMaintainer = null;

    /* Test database update scripts */
    private List<VersionScriptPair> versionScriptPairs;

    /* Test database versions */
    private Version version0, version1, version2;


    /**
     * Create an instance of DBMaintainer, linked with mock versions of VersionSource, ScriptSource and
     * SQLScriptRunner
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();

        dbMaintainer = new DBMaintainer();
        dbMaintainer.setFromScratchEnabled(true);

        versionScriptPairs = new ArrayList<VersionScriptPair>();
        version0 = new Version(0L, 0L);
        version1 = new Version(1L, 1L);
        version2 = new Version(2L, 2L);
        versionScriptPairs.add(new VersionScriptPair(version1, "Script 1"));
        versionScriptPairs.add(new VersionScriptPair(version2, "Script 2"));
    }

    /**
     * Test the default behavior of the test runner: Check if there are scripts available to increment the version
     * of the database, execute them, and increment the version.
     */
    public void testDBMaintainer_incremental() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(version0);
        expect(mockScriptSource.shouldRunFromScratch(version0)).andReturn(false);
        expect(mockScriptSource.getScripts(version0)).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(version1);
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(version2);
        mockConstraintsDisabler.disableConstraints();
        mockSequenceUpdater.updateSequences();
        mockDtdGenerator.generateDtd();

        replay();

        // Execute test
        dbMaintainer.updateDatabase();

        // Verify results
        verify();
    }

    /**
     * Test the default behavior of the test runner: Check if there are scripts available to increment the version
     * of the database, execute them, and increment the version.
     */
    public void testDBMaintainer_fromScratch() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(version0);
        expect(mockScriptSource.shouldRunFromScratch(version0)).andReturn(true);
        mockDbClearer.clearDatabase();
        expect(mockScriptSource.getScripts(version0)).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(version1);
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(version2);
        mockConstraintsDisabler.disableConstraints();
        mockSequenceUpdater.updateSequences();
        mockDtdGenerator.generateDtd();

        replay();

        // Execute test
        dbMaintainer.updateDatabase();

        // Verify results
        verify();
    }

    /**
     * Tests the behavior in case there is an error in a script supplied by the ScriptSource. In this case, the
     * database version must not org incremented and a StatementHandlerException must be thrown.
     */
    public void testDBMaintainer_errorInScript() throws Exception {
        expect(mockVersionSource.getDbVersion()).andReturn(version0).anyTimes();
        expect(mockScriptSource.shouldRunFromScratch(version0)).andReturn(false);
        expect(mockScriptSource.getScripts(version0)).andReturn(versionScriptPairs);
        mockScriptRunner.execute("Script 1");
        expectLastCall().andThrow(new StatementHandlerException("Test exception"));

        replay();

        try {
            dbMaintainer.updateDatabase();
            fail("A StatementHandlerException should have been thrown");
        } catch (StatementHandlerException e) {
            // Expected
        }

        verify();
    }

}
