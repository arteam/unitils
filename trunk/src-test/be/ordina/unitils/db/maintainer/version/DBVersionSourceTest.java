/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.db.maintainer.version;

import be.ordina.unitils.db.maintainer.version.DBVersionSource;
import be.ordina.unitils.testing.dao.BaseDatabaseTestCase;
import be.ordina.unitils.util.PropertiesUtils;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Test class for {@link DBVersionSource}
 */
public class DBVersionSourceTest extends BaseDatabaseTestCase {

    private static final String[][] dbVersionSourceProperties = {
            {"dbMaintainer.dbVersionSource.tableName", "db_version"},
            {"dbMaintainer.dbVersionSource.columnName", "version"}
    };

    /**
     * The tested instance
     */
    private DBVersionSource dbVersionSource;

    /**
     * Initialize test fixture
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {
        super.setUp();
        Properties properties = PropertiesUtils.asProperties(dbVersionSourceProperties);
        dbVersionSource = new DBVersionSource();
        dbVersionSource.init(properties, getDataSource());
    }

    /**
     * Tests normal behavior of the class: Retrieving and incrementing the version
     */
    public void testGetDBVersion() {
        assertEquals(3, dbVersionSource.getDbVersion());
    }

    /**
     * Tests retrieval of the version, when the table is still empty (first use)
     *
     * @throws Exception
     */
    public void testGetDBVersion_emptyTable() throws Exception {
        clearDBVersionTable();
        assertEquals(0, dbVersionSource.getDbVersion());
    }

    public void testSetDBVersion() {
        dbVersionSource.setDbVersion(2L);
        assertEquals(2L, dbVersionSource.getDbVersion());
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
            conn = getDataSource().getConnection();
            st = conn.createStatement();
            st.execute("delete from db_version");
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

}
