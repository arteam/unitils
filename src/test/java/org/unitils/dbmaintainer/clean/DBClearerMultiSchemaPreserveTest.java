package org.unitils.dbmaintainer.clean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Trigger;
import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.TestSQLUtils;
import org.unitils.core.util.SQLUtils;
import org.unitils.database.annotations.TestDataSource;
import static org.unitils.dbmaintainer.clean.impl.DefaultDBClearer.*;
import org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Test class for the {@link DBClearer} using multiple database schemas with configuration to preserve all items.
 * <p/>
 * This test is currenlty only implemented for HsqlDb
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DBClearerMultiSchemaPreserveTest extends UnitilsJUnit3 {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DBClearerMultiSchemaPreserveTest.class);

    /* DataSource for the test database, is injected */
    @TestDataSource
    private DataSource dataSource = null;

    /* Tested object */
    private DBClearer dbClearer;

    /* The db support for the default PUBLIC schema */
    private DbSupport dbSupportPublic;

    /* The db support for the SCHEMA_A schema */
    private DbSupport dbSupportSchemaA;

    /* The db support for the SCHEMA_B schema */
    private DbSupport dbSupportSchemaB;

    /* True if current test is not for the current dialect */
    private boolean disabled;


    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Properties configuration = new ConfigurationLoader().loadConfiguration();
        this.disabled = !"hsqldb".equals(PropertyUtils.getString(DatabaseModuleConfigUtils.PROPKEY_DATABASE_DIALECT, configuration));
        if (disabled) {
            return;
        }

        // configure 3 schemas
        configuration.setProperty(DbSupportFactory.PROPKEY_DATABASE_SCHEMA_NAMES, "PUBLIC, SCHEMA_A, SCHEMA_B");
        dbSupportPublic = DbSupportFactory.getDbSupport(configuration, dataSource, "PUBLIC");
        dbSupportSchemaA = DbSupportFactory.getDbSupport(configuration, dataSource, "SCHEMA_A");
        dbSupportSchemaB = DbSupportFactory.getDbSupport(configuration, dataSource, "SCHEMA_B");
        // configure items to preserve
        configuration.setProperty(PROPKEY_PRESERVE_TABLES, "public.test_table, " + dbSupportSchemaA.quoted("SCHEMA_A") + "." + dbSupportSchemaA.quoted("TEST_TABLE") + ", " + dbSupportSchemaB.quoted("SCHEMA_B") + ".test_table");
        configuration.setProperty(PROPKEY_PRESERVE_VIEWS, "public.test_view, " + dbSupportSchemaA.quoted("SCHEMA_A") + "." + dbSupportSchemaA.quoted("TEST_VIEW") + ", " + dbSupportSchemaB.quoted("SCHEMA_B") + ".test_view");
        configuration.setProperty(PROPKEY_PRESERVE_SEQUENCES, "public.test_sequence, " + dbSupportSchemaA.quoted("SCHEMA_A") + "." + dbSupportSchemaA.quoted("TEST_SEQUENCE") + ", " + dbSupportSchemaB.quoted("SCHEMA_B") + ".test_sequence");
        configuration.setProperty(PROPKEY_PRESERVE_SYNONYMS, "public.test_synonym, " + dbSupportSchemaA.quoted("SCHEMA_A") + "." + dbSupportSchemaA.quoted("TEST_SYNONYM") + ", " + dbSupportSchemaB.quoted("SCHEMA_B") + ".test_synonym");
        // create clearer instance
        dbClearer = DatabaseModuleConfigUtils.getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource);

        dropTestDatabase();
        createTestDatabase();
    }


    /**
     * Removes all test tables.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dropTestDatabase();
    }


    /**
     * Checks if the tables are correctly dropped.
     */
    public void testClearDatabase_tables() throws Exception {
        assertEquals(1, dbSupportPublic.getTableNames().size());
        assertEquals(1, dbSupportSchemaA.getTableNames().size());
        assertEquals(1, dbSupportSchemaB.getTableNames().size());
        dbClearer.clearSchemas();
        assertEquals(1, dbSupportPublic.getTableNames().size());
        assertEquals(1, dbSupportSchemaA.getTableNames().size());
        assertEquals(1, dbSupportSchemaB.getTableNames().size());
    }


    /**
     * Checks if the views are correctly dropped
     */
    public void testClearDatabase_views() throws Exception {
        assertEquals(1, dbSupportPublic.getViewNames().size());
        assertEquals(1, dbSupportSchemaA.getViewNames().size());
        assertEquals(1, dbSupportSchemaB.getViewNames().size());
        dbClearer.clearSchemas();
        assertEquals(1, dbSupportPublic.getViewNames().size());
        assertEquals(1, dbSupportSchemaA.getViewNames().size());
        assertEquals(1, dbSupportSchemaB.getViewNames().size());
    }


    /**
     * Tests if the triggers are correctly dropped
     */
    public void testClearDatabase_sequences() throws Exception {
        assertEquals(1, dbSupportPublic.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaA.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaB.getSequenceNames().size());
        dbClearer.clearSchemas();
        assertEquals(1, dbSupportPublic.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaA.getSequenceNames().size());
        assertEquals(1, dbSupportSchemaB.getSequenceNames().size());
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase() throws Exception {
        // create schemas
        SQLUtils.executeUpdate("create schema SCHEMA_A AUTHORIZATION DBA", dataSource);
        SQLUtils.executeUpdate("create schema SCHEMA_B AUTHORIZATION DBA", dataSource);
        // create tables
        SQLUtils.executeUpdate("create table TEST_TABLE (col1 varchar(100))", dataSource);
        SQLUtils.executeUpdate("create table SCHEMA_A.TEST_TABLE (col1 varchar(100))", dataSource);
        SQLUtils.executeUpdate("create table SCHEMA_B.TEST_TABLE (col1 varchar(100))", dataSource);
        // create views
        SQLUtils.executeUpdate("create view TEST_VIEW as select col1 from TEST_TABLE", dataSource);
        SQLUtils.executeUpdate("create view SCHEMA_A.TEST_VIEW as select col1 from SCHEMA_A.TEST_TABLE", dataSource);
        SQLUtils.executeUpdate("create view SCHEMA_B.TEST_VIEW as select col1 from SCHEMA_B.TEST_TABLE", dataSource);
        // create sequences
        SQLUtils.executeUpdate("create sequence TEST_SEQUENCE", dataSource);
        SQLUtils.executeUpdate("create sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        SQLUtils.executeUpdate("create sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    private void dropTestDatabase() throws Exception {
        // drop sequences
        TestSQLUtils.executeUpdateQuietly("drop sequence TEST_SEQUENCE", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop sequence SCHEMA_A.TEST_SEQUENCE", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop sequence SCHEMA_B.TEST_SEQUENCE", dataSource);
        // drop views
        TestSQLUtils.executeUpdateQuietly("drop view TEST_VIEW", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop view SCHEMA_A.TEST_VIEW", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop view SCHEMA_B.TEST_VIEW", dataSource);
        // drop tables
        TestSQLUtils.executeUpdateQuietly("drop table TEST_TABLE", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop table SCHEMA_A.TEST_TABLE", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop table SCHEMA_B.TEST_TABLE", dataSource);
        // drop schemas
        TestSQLUtils.executeUpdateQuietly("drop schema SCHEMA_A", dataSource);
        TestSQLUtils.executeUpdateQuietly("drop schema SCHEMA_B", dataSource);
    }


    /**
     * Test trigger for hypersonic.
     *
     * @author Filip Neven
     * @author Tim Ducheyne
     */
    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }

}
