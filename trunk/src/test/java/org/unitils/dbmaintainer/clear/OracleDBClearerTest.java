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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DbClearer test for an Oracle database
 */
public class OracleDBClearerTest extends DBClearerTest {

    /**
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        if (isTestedDialectActivated()) {
            super.setUp();

            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                if (triggerExists(conn)) {
                    dropTestTrigger(conn);
                }
                createTestTrigger(conn);
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (isTestedDialectActivated()) {
            super.tearDown();
        }
    }

    public void testClearDatabase_triggers() throws Exception {
        if (isTestedDialectActivated()) {
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
        if (isTestedDialectActivated()) {
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

    protected boolean isTestedDialectActivated() {
        Configuration config = new ConfigurationLoader().loadConfiguration();
        return "oracle".equals(config.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT));
    }
}
