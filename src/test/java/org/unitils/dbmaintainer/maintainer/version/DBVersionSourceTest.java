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
package org.unitils.dbmaintainer.maintainer.version;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.time.DateUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test class for {@link org.unitils.dbmaintainer.maintainer.version.DBVersionSource}. The implementation is tested using
 * a test database. The dbms that is used depends on the database configuration in test/resources/unitils.properties
 */
@DatabaseTest
public class DBVersionSourceTest extends UnitilsJUnit3 {

    /* The tested instance */
    private VersionSource dbVersionSource;

    /* The dataSource */
    @TestDataSource
    private javax.sql.DataSource dataSource;

    /**
     * Initialize test fixture
     *
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        StatementHandler statementHandler = DatabaseModuleConfigUtils.getConfiguredStatementHandlerInstance(configuration,
                dataSource);
        dbVersionSource = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(VersionSource.class,
                configuration, dataSource, statementHandler);
    }

    /**
     * Tests retrieval of the version, when there is no version table yet (first use)
     *
     * @throws Exception
     */
    public void testGetDBVersion_noVersionTable() throws Exception {
        assertRefEquals(new Version(0L, 0L), dbVersionSource.getDbVersion());
    }

    /**
     * Tests retrieval of the version, when the table is still empty (first use)
     *
     * @throws Exception
     */
    public void testGetDBVersion_emptyTable() throws Exception {
        clearDBVersionTable();
        assertRefEquals(new Version(0L, 0L), dbVersionSource.getDbVersion());
    }

    /**
     * Test normal retrieval of the version
     *
     * @throws Exception
     */
    public void testGetDBVersion() throws Exception {
        Version expectedVersion = new Version(3L, DateUtils.parseDate("2006-10-08 12:00", new String[]{"yyyy-MM-dd hh:mm"}).getTime());
        assertRefEquals(expectedVersion, dbVersionSource.getDbVersion());
    }

    /**
     * Tests setting the version
     *
     * @throws Exception
     */
    public void testSetDBVersion() throws Exception {
        Version version = new Version(2L, DateUtils.parseDate("2006-10-09 14:00", new String[]{"yyyy-MM-dd hh:mm"}).getTime());
        dbVersionSource.setDbVersion(version);
        assertRefEquals(version, dbVersionSource.getDbVersion());
    }

    /**
     * Tests whether the dbVersion can be correctly set when the db_version table is empty
     *
     * @throws Exception
     */
    public void testSetDBVersion_emptyTable() throws Exception {
        clearDBVersionTable();
        testSetDBVersion();
    }

    public void testRegisterUpdateSucceeded_succeeded() throws Exception {
        dbVersionSource.registerUpdateSucceeded(true);
        assertTrue(dbVersionSource.lastUpdateSucceeded());
    }

    public void testRegisterUpdateSucceeded_notSucceeded() throws Exception {
        dbVersionSource.registerUpdateSucceeded(false);
        assertFalse(dbVersionSource.lastUpdateSucceeded());
    }

    /**
     * Deletes all records from the db_version table
     *
     * @throws SQLException
     */
    private void clearDBVersionTable() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            st.execute("delete from db_version");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

}
