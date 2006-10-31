package org.unitils.dbmaintainer.clear;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbmaintainer.clear.DBClearerTest;

/**
 * DBClearer test for a hsqldb database
 */
public class HsqldbDBClearerTest extends DBClearerTest {

    /**
     * Calls the superclass fixture + creates a test trigger
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {

        if (isTestedDialectActivated()) {
            super.setUp();

            if (triggerExists("testtrigger")) {
                dropTestTrigger();
            }
            createTestTrigger();
        }
    }

    /**
     * Tests if the triggers are correctly dropped
     *
     * @throws Exception
     */
    public void testClearDatabase_triggers() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(triggerExists("testtrigger"));
            dbClearer.clearDatabase();
            assertFalse(triggerExists("testtrigger"));
        }
    }

    /**
     * Creates the test trigger
     * @throws SQLException
     */
    private void createTestTrigger() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create trigger testtrigger before insert on testtable1 call "
                    + "\"org.unitils.dbmaintainer.clear.HsqldbTestTrigger\"");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Drops the test trigger
     *
     * @throws SQLException
     */
    private void dropTestTrigger() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("drop trigger testtrigger");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Checks wether the trigger with the given name exists
     *
     * @param triggerName
     * @return True if the trigger exists
     * @throws SQLException
     */
    private boolean triggerExists(String triggerName) throws SQLException {
        // We test if the trigger exists, by inserting a row in testtable2, and checking if the
        // trigger has exexucted
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select trigger_name from information_schema.system_triggers");
            while (rs.next()) {
                if (triggerName.equalsIgnoreCase(rs.getString("TRIGGER_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    /**
     * Verifies wether the hsqldb dialect is activated
     * @return True if the hsqldb dialect is activated, false otherwise
     */
    protected boolean isTestedDialectActivated() {
        Configuration config = new ConfigurationLoader().loadConfiguration();
        return "hsqldb".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
