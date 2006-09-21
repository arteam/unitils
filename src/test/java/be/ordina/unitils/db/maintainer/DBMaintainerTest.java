/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer;

import be.ordina.unitils.BaseUnitilsEasyMockTestCase;
import be.ordina.unitils.db.clear.DBClearer;
import be.ordina.unitils.db.constraints.ConstraintsDisabler;
import be.ordina.unitils.db.dtd.DtdGenerator;
import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.script.ScriptSource;
import be.ordina.unitils.db.maintainer.version.Version;
import be.ordina.unitils.db.maintainer.version.VersionSource;
import be.ordina.unitils.db.script.SQLScriptRunner;
import be.ordina.unitils.db.sequences.SequenceUpdater;
import be.ordina.unitils.testing.mock.AutoInjectMocks;
import be.ordina.unitils.testing.mock.Mock;
import be.ordina.unitils.testing.mock.inject.InjectionType;
import be.ordina.unitils.testing.mock.inject.PropertyAccessType;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.expectLastCall;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 */
public class DBMaintainerTest extends BaseUnitilsEasyMockTestCase {

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
        dbMaintainer.setFromScratchEnabled(true);

        versionScriptPairs = new ArrayList<VersionScriptPair>();
        version0 = new Version(0L, 0L);
        version1 = new Version(1L, 1L);
        version2 = new Version(2L, 2L);
        versionScriptPairs.add(new VersionScriptPair(version1, "Script 1"));
        versionScriptPairs.add(new VersionScriptPair(version2, "Script 2"));

        injectMocks();
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
