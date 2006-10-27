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
 */
public class HsqldbDBClearerTest extends DBClearerTest {

    private boolean hsqldbDialectActivated;

    @Override
    protected void setUp() throws Exception {

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        hsqldbDialectActivated = "hsqldb".equals(configuration
                .getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));

        if (hsqldbDialectActivated) {
            super.setUp();

            if (triggerExists()) {
                dropTrigger();
            }
            createTrigger();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (hsqldbDialectActivated) {
            super.tearDown();
        }
    }

    public void testClearDatabase_triggers() throws Exception {
        if (hsqldbDialectActivated) {
            assertTrue(triggerExists());
            dbClearer.clearDatabase();
            assertFalse(triggerExists());
        }
    }

    private void createTrigger() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("create trigger testtrigger before insert on db_version call "
                    + "\"org.unitils.dbmaintainer.clear.HsqldbTestTrigger\"");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private void dropTrigger() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("drop trigger");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private boolean triggerExists() throws SQLException {
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
                if ("testtrigger".equalsIgnoreCase(rs.getString("TRIGGER_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

}
