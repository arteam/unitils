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
package org.unitils.dbmaintainer.sequences;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test class for the HsqldbSequenceUpdater
 */
public class HsqldbSequenceUpdaterTest extends SequenceUpdaterTest {

    protected void setUp() throws Exception {
        super.setUp();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTableWithIdentityColumn(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected void tearDown() throws Exception {
        Connection conn = dataSource.getConnection();
        try {
            conn = dataSource.getConnection();
            dropTestTableWithIdentityColumn(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
        super.tearDown();
    }

    private void createTestTableWithIdentityColumn(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create table testidentity (identitycol identity, othercol varchar(1))");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestTableWithIdentityColumn(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("drop table testidentity");
        } catch (SQLException e) {
            // Ignored
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    protected long getNextSequenceValue(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            rs = st.executeQuery("select next value for testsequence from testtable");
            rs.next();
            long sequenceValue = rs.getLong(1);
            return sequenceValue;
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    public void testUpdateIdentityColumns() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(getNextIdentityColumnValue(conn) < LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
            sequenceUpdater.updateSequences();
            long nextIdentityColumnValue = getNextIdentityColumnValue(conn);
            System.out.println("nextIdentityColumnValue = " + nextIdentityColumnValue);
            assertTrue(nextIdentityColumnValue >= LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private long getNextIdentityColumnValue(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            st.execute("insert into testidentity(othercol) values ('x')");
            rs = st.executeQuery("select max(identitycol) from testidentity");
            rs.next();
            long identityCol = rs.getLong(1);
            return identityCol;
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }
}
