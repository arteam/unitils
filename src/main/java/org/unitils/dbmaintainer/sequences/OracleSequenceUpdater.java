package org.unitils.dbmaintainer.sequences;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Filip Neven
 */
public class OracleSequenceUpdater implements SequenceUpdater {

    private static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    /**
     * The <code>DataSource</code> that provides the connection to the database
     */
    private DataSource dataSource;

    /**
     * The StatementHandler on which the sequence update statements will be executed
     */
    private StatementHandler statementHandler;

    /**
     * The lowest acceptable sequence value
     */
    private long lowestAcceptableSequenceValue;

    /**
     * @see SequenceUpdater#init(Configuration, DataSource, StatementHandler)
     */
    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        this.dataSource = dataSource;
        this.statementHandler = statementHandler;

        lowestAcceptableSequenceValue = configuration.getLong(PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE);
    }

    /**
     * @see SequenceUpdater#updateSequences()
     */
    public void updateSequences() throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            incrementSequencesWithLowValue(conn, st);
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving database version", e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Makes sure the value of all sequences is equal or higher than <code>lowestAcceptableSequenceValue</code>
     *
     * @param st
     * @throws SQLException
     */
    private void incrementSequencesWithLowValue(Connection conn, Statement st) throws SQLException, StatementHandlerException {
        ResultSet rs = null;
        Statement st1 = null;
        try {
            st1 = conn.createStatement();
            rs = st.executeQuery("select SEQUENCE_NAME, LAST_NUMBER, INCREMENT_BY from USER_SEQUENCES where LAST_NUMBER < "
                    + lowestAcceptableSequenceValue);
            while (rs.next()) {
                String sequenceName = rs.getString("SEQUENCE_NAME");
                long lastNumber = rs.getLong("LAST_NUMBER");
                long incrementBy = rs.getLong("INCREMENT_BY");
                String sqlChangeIncrement = "alter sequence " + sequenceName + " increment by " +
                        (lowestAcceptableSequenceValue - lastNumber);
                statementHandler.handle(sqlChangeIncrement);
                String sqlNextSequenceValue = "select " + sequenceName + ".NEXTVAL from DUAL";
                statementHandler.handle(sqlNextSequenceValue);
                String sqlResetIncrement = "alter sequence " + sequenceName + " increment by " + incrementBy;
                statementHandler.handle(sqlResetIncrement);
            }
        } finally {
            DbUtils.closeQuietly(null, st1, rs);
        }
    }

}
