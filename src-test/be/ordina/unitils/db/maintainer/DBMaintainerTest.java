/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer;

import be.ordina.unitils.db.handler.StatementHandlerException;
import be.ordina.unitils.db.maintainer.script.ScriptSource;
import be.ordina.unitils.db.maintainer.version.VersionSource;
import be.ordina.unitils.db.script.SQLScriptRunner;
import be.ordina.unitils.db.constraints.ConstraintsDisabler;
import be.ordina.unitils.db.sequences.SequenceUpdater;
import be.ordina.unitils.db.dtd.DtdGenerator;
import be.ordina.unitils.db.clear.DBClearer;
import be.ordina.unitils.testing.mock.EasyMockTestCase;
import be.ordina.unitils.testing.mock.Mock;
import static org.easymock.classextension.EasyMock.*;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Filip Neven
 */
public class DBMaintainerTest extends EasyMockTestCase {

    @Mock
    private VersionSource mockVersionSource;
    @Mock
    private ScriptSource mockScriptSource;
    @Mock
    private SQLScriptRunner mockScriptRunner;
    @Mock
    private DBClearer mockDbClearer;
    @Mock
    private ConstraintsDisabler mockConstraintsDisabler;
    @Mock
    private SequenceUpdater mockSequenceUpdater;
    @Mock
    private DtdGenerator mockDtdGenerator;

    /**
     * Tested object
     */
    private DBMaintainer dbMaintainer;

    /**
     * Test database update scripts
     */
    private List<VersionScriptPair> versionScriptPairs;

    /**
     * Create an instance of DBMaintainer, linked with mock versions of VersionSource, ScriptSource and
     * SQLScriptRunner
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        dbMaintainer = new DBMaintainer(mockVersionSource, mockScriptSource, mockScriptRunner, mockDbClearer,
                mockConstraintsDisabler, mockSequenceUpdater, mockDtdGenerator);

        versionScriptPairs = new ArrayList<VersionScriptPair>();
        versionScriptPairs.add(new VersionScriptPair(2L, "Script 2"));
        versionScriptPairs.add(new VersionScriptPair(3L, "Script 3"));
    }

    /**
     * Test the default behavior of the test runner: Check if there are scripts available to increment the version
     * of the database, execute them, and increment the version.
     */
    public void testDBMaintainer() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(1L);
        expect(mockScriptSource.getScripts(1L)).andReturn(versionScriptPairs);
        mockDbClearer.clearDatabase();
        mockScriptRunner.execute("Script 2");
        mockVersionSource.setDbVersion(2L);
        mockScriptRunner.execute("Script 3");
        mockVersionSource.setDbVersion(3L);
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
     * database version must not org incremented and a StatementHandlerException must org thrown.
     */
    public void testDBMaintainer_errorInScript() throws Exception {
        expect(mockVersionSource.getDbVersion()).andReturn(2L).anyTimes();
        expect(mockScriptSource.getScripts(2L)).andReturn(versionScriptPairs);
        mockDbClearer.clearDatabase();
        mockScriptRunner.execute("Script 2");
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
