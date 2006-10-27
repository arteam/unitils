package org.unitils.dbmaintainer.sequences;

import org.unitils.UnitilsJUnit3;
import org.unitils.db.annotations.TestDataSource;
import org.unitils.db.annotations.DatabaseTest;
import org.unitils.core.ConfigurationLoader;
import org.unitils.util.ReflectionUtils;
import org.unitils.dbmaintainer.sequences.SequenceUpdater;
import org.unitils.dbmaintainer.sequences.BaseSequenceUpdater;
import org.unitils.dbmaintainer.maintainer.DBMaintainer;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.JDBCStatementHandler;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

@DatabaseTest
public abstract class SequenceUpdaterTest extends UnitilsJUnit3 {

    @TestDataSource
    private DataSource dataSource;

    private SequenceUpdater sequenceUpdater;
    private static final int LOWEST_ACCEPTACLE_SEQUENCE_VALUE = 1000;

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        configuration.setProperty(BaseSequenceUpdater.PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE, LOWEST_ACCEPTACLE_SEQUENCE_VALUE);

        String databaseDialect = configuration.getString(DBMaintainer.PROPKEY_DATABASE_DIALECT);
        StatementHandler statementHandler = new JDBCStatementHandler();
        statementHandler.init(configuration, dataSource);
        sequenceUpdater = ReflectionUtils.createInstanceOfType(configuration.getString(DBMaintainer.PROPKEY_SEQUENCEUPDATER_START + "." + databaseDialect));
        sequenceUpdater.init(configuration, dataSource, statementHandler);

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            createTestTable(conn);
            insertTestRecord(conn);
            createTestSequence(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            dropTestSequence(conn);
            dropTestTable(conn);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    private void insertTestRecord(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("insert into testtable values('test')");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void createTestTable(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("create table testtable (test varchar(10))");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTestTable(Connection conn) {
        Statement st = null;
        try {
            st = conn.createStatement();
            // Make sure previous setup is cleaned up
            st.execute("drop table testtable");
        } catch (SQLException e) {
            // No action is taken
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

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

    public void testUpdateSequences() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            assertTrue(getNextSequenceValue(conn) < LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
            sequenceUpdater.updateSequences();
            assertTrue(getNextSequenceValue(conn) >= LOWEST_ACCEPTACLE_SEQUENCE_VALUE);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    /**
     * Verifies that if a sequence has a value already high enough, the value is not being set to a lower value
     * @throws Exception
     */
    public void testUpdateSequences_valueAlreadyHighEnough() throws Exception {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            sequenceUpdater.updateSequences();
            long updatedSequenceValue = getNextSequenceValue(conn);
            sequenceUpdater.updateSequences();
            assertFalse(getNextSequenceValue(conn) <= updatedSequenceValue);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    protected abstract long getNextSequenceValue(Connection conn) throws SQLException;

}
