/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.maintainer.version;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.time.DateUtils;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandler;
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
    private DBVersionSource dbVersionSource;

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

        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);
        dbVersionSource = new DBVersionSource();
        dbVersionSource.init(configuration, dataSource, statementHandler);
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

    public void testSetDBVersion_emptyTable() throws Exception {
        clearDBVersionTable();
        testSetDBVersion();
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
