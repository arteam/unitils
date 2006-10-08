package org.unitils.dbmaintainer.maintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.util.UnitilsConfiguration;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Neven
 */
public class HsqldbDBClearerTest extends DBClearerTest {

    protected void setUp() throws Exception {
        if (hsqldbDialectActivated()) {
            super.setUp();
        }
    }

    public void testClearDatabase_triggers() throws Exception {
        if (hsqldbDialectActivated()) {
            createTrigger();
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
                    "org.unitils.dbmaintainer.maintainer.clear.HsqldbTestTrigger");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private boolean triggerExists() throws SQLException {
        // We test if the trigger exists, by inserting a row in testtable2, and checking if the trigger has exexucted
        Connection conn = null;
        Statement st = null;
        try {
            HsqldbTestTrigger.setTriggerExecuted(false);
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("insert into testtable2 values('test')");
            return HsqldbTestTrigger.isTriggerExecuted();
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private boolean hsqldbDialectActivated() {
        Configuration config = UnitilsConfiguration.getInstance();
        return "hsqldb".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
