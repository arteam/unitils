package org.unitils.dbmaintainer.maintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 */
public class HsqldbDBClearerTest extends DBClearerTest {

    protected void setUp() throws Exception {
        if (hsqldbDialectActivated()) {
            super.setUp();

            createTrigger();
        }
    }

    protected void tearDown() throws Exception {
        if (hsqldbDialectActivated()) {
            if (triggerExists()) {
                dropTrigger();
            }

            super.tearDown();
        }
    }

    public void testClearDatabase_triggers() throws Exception {
        if (hsqldbDialectActivated()) {
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
            st.execute("create trigger testtrigger before insert on testtable2 call " +
                    "\"org.unitils.dbmaintainer.maintainer.clear.HsqldbTestTrigger\"");
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
        // We test if the trigger exists, by inserting a row in testtable2, and checking if the trigger has exexucted
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select trigger_name from information_schema.system_triggers");
            while (rs.next()) {
                if ("TESTTRIGGER".equals(rs.getString("TRIGGER_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    private boolean hsqldbDialectActivated() {
        Configuration config = UnitilsConfiguration.getInstance();
        return "hsqldb".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
