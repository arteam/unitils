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
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.util.ReflectionUtils;

import java.sql.*;

/**
 * Base test class for the DBClearer. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 */
@DatabaseTest
abstract public class DBClearerTest extends UnitilsJUnit3 {

    /**
     * DataSource for the test database, is injected
     */
    @TestDataSource
    protected javax.sql.DataSource dataSource;

    /**
     * Tested object
     */
    protected DBClearer dbClearer;

    /**
     * Test database schema name
     */
    protected String schemaName;

    /**
     * Configures the tested object. Creates a test table, injex, view and sequence
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);

        dbClearer = ReflectionUtils.createInstanceOfType(configuration.getString(DBMaintainer.PROPKEY_DBCLEARER_START + '.' +
                configuration.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT)));
        dbClearer.init(configuration, dataSource, statementHandler);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestView(conn);
            dropTestTables(conn);
            dropTestSequence(conn);
            createTestTables(conn);
            createTestIndex(conn);
            createTestView(conn);
            createTestSequence(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }

    }

    /**
     * Checks if the tables are correctly dropped.
     *
     * @throws Exception
     */
    public void testClearDatabase_tables() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(tableExists("testtable1"));
            dbClearer.clearDatabase();
            assertFalse(tableExists("testtable1"));
        }
    }

    /**
     * Checks if the views are correctly dropped
     *
     * @throws Exception
     */
    public void testClearDatabase_views() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(tableExists("testview"));
            dbClearer.clearDatabase();
            assertFalse(tableExists("testview"));
        }
    }

    /**
     * Creates the test tables
     *
     * @param conn
     * @throws SQLException
     */
    private void createTestTables(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create table testtable1 (col1 varchar(10))");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Creates the test index
     *
     * @param conn
     * @throws SQLException
     */
    private void createTestIndex(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.execute("create index testindex on testtable1(col1)");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Drops the test tables
     *
     * @param conn
     */
    private void dropTestTables(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable1");
        } catch (SQLException e) {
            // no action taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Creates the test view
     *
     * @param conn
     * @throws SQLException
     */
    private void createTestView(Connection conn) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create view testview as select col1 from testtable1");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Drops the test view
     *
     * @param conn
     */
    private void dropTestView(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop view testview");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Creates the test sequence
     *
     * @param conn
     */
    private void createTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Drops the test sequence
     *
     * @param conn
     */
    private void dropTestSequence(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop sequence testsequence");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    /**
     * Checks whether the table with the given name exists
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    private boolean tableExists(String tableName) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, schemaName, tableName.toUpperCase(), null);
            while (rs.next()) {
                if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
            return false;
        } finally {
            DbUtils.closeQuietly(conn, null, rs);
        }
    }

    /**
     * Checks whether the database dialect that is tested in the current implementation is the currenlty configured
     * database dialect.
     *
     * @return True if the tested dbms is equal to the configured one, false otherwise
     */
    abstract protected boolean isTestedDialectActivated();
}
