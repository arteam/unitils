package org.unitils.dbmaintainer.maintainer.clear;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

/**
 * 
 */
public class OracleDBClearerTest extends DBClearerTest {

    @Override
    protected void setUp() throws Exception {
        if (oracleDialectActivated()) {
            super.setUp();

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                if (triggerExists(conn)) {
                    dropTestTrigger(conn);
                }
                if (sequenceExists(conn)) {
                    dropTestSequence(conn);
                }
                createTestTrigger(conn);
                createTestSequence(conn);
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (oracleDialectActivated()) {
            super.tearDown();
        }
    }

    public void testClearDatabase_triggers() throws Exception {
        if (oracleDialectActivated()) {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                assertTrue(triggerExists(conn));
                dbClearer.clearDatabase();
                assertFalse(triggerExists(conn));
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    public void testClearDatabase_sequences() throws Exception {
        if (oracleDialectActivated()) {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                assertTrue(sequenceExists(conn));
                dbClearer.clearDatabase();
                assertFalse(sequenceExists(conn));
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    private void createTestTrigger(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create or replace trigger testtrigger before insert on db_version begin " +
                    "select 1 from dual end testtrigger");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestTrigger(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("drop trigger testtrigger");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestSequence(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create sequence testsequence");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestSequence(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("drop sequence testsequence");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private boolean triggerExists(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select trigger_name from user_triggers");
            while (rs.next()) {
                if ("testtrigger".equalsIgnoreCase(rs.getString("TRIGGER_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    private boolean sequenceExists(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select sequence_name from user_sequences");
            while (rs.next()) {
                if ("testsequence".equalsIgnoreCase(rs.getString("SEQUENCE_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    private boolean oracleDialectActivated() {
        Configuration config = new UnitilsConfigurationLoader().loadConfiguration();
        return "oracle".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
