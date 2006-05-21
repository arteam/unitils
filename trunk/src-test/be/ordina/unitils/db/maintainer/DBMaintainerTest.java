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
import be.ordina.unitils.testing.mock.EasyMockTestCase;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Filip Neven
 */
public class DBMaintainerTest extends EasyMockTestCase {

    /**
     * Mock objects
     */
    private VersionSource mockVersionSource;
    private ScriptSource mockScriptSource;
    private SQLScriptRunner mockScriptRunner;

    /**
     * Tested object
     */
    private DBMaintainer dbMaintainer;

    /**
     * Create an instance of DBMaintainer, linked with mock versions of VersionSource, ScriptSource and
     * SQLScriptRunner
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        mockVersionSource = getMock(VersionSource.class);
        mockScriptSource = getMock(ScriptSource.class);
        mockScriptRunner = getMock(SQLScriptRunner.class);
        dbMaintainer = new DBMaintainer(mockVersionSource, mockScriptSource, mockScriptRunner);
    }

    /**
     * Test the default behavior of the test runner: Check if there are scripts available to increment the version
     * of the database, execute them, and increment the version.
     */
    public void testDBMaintainer() throws Exception {
        // Record behavior
        expect(mockVersionSource.getDbVersion()).andReturn(2L);
        expect(mockScriptSource.getScript(3L)).andReturn("Script 1");
        mockScriptRunner.execute("Script 1");
        mockVersionSource.setDbVersion(3L);
        expect(mockScriptSource.getScript(4L)).andReturn(null);

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
        expect(mockVersionSource.getDbVersion()).andReturn(3L).anyTimes();
        expect(mockScriptSource.getScript(4L)).andReturn("Erroneous script");
        mockScriptRunner.execute("Erroneous script");
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
