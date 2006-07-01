package be.ordina.unitils.db.sequences;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import be.ordina.unitils.util.PropertiesUtils;

/**
 * @author Filip Neven
 */
public class OracleSequenceUpdater implements SequenceUpdater {

    private static final String PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE = "sequenceUpdater.sequencevalue.lowestacceptable";

    private static final Logger logger = Logger.getLogger(OracleSequenceUpdater.class);

    /**
     * The <code>DataSource</code> that provides the connection to the database
     */
    private DataSource dataSource;

    /**
     * The lowest acceptable sequence value
     */
    private long lowestAcceptableSequenceValue;

    /**
     * @see SequenceUpdater#init(java.util.Properties, javax.sql.DataSource)
     */
    public void init(Properties properties, DataSource dataSource) {
        this.dataSource = dataSource;
        lowestAcceptableSequenceValue = PropertiesUtils.getLongPropertyRejectNull(properties, PROPKEY_LOWEST_ACCEPTABLE_SEQUENCE_VALUE);
    }

    /**
     * @see SequenceUpdater#updateSequences()
     */
    public void updateSequences() {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            List<String> sequencesWithLowValue = getSequencesWithLowValue(st);
            for (String sequenceWithLowValue : sequencesWithLowValue) {
                incrementSequence(sequenceWithLowValue, st);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving database version", e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    /**
     * Retrieves the names of all sequences that have a value lower than <code>lowestAcceptableSequenceValue</code>
     * @param st
     * @return the names of all sequences that have a value lower than <code>lowestAcceptableSequenceValue</code>
     * @throws SQLException
     */
    private List<String> getSequencesWithLowValue(Statement st) throws SQLException {
        ResultSet rs = null;
        try {
            List<String> sequences = new ArrayList<String>();
            rs = st.executeQuery("select SEQUENCE_NAME from USER_SEQUENCES where LAST_NUMBER < " + lowestAcceptableSequenceValue);
            while (rs.next()) {
                sequences.add(rs.getString("SEQUENCE_NAME"));
            }
            return sequences;
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }

    /**
     * Increments the sequence with the given name with <code>lowestAcceptableSequenceValue</code>
     * @param sequenceWithLowValue
     * @param st
     * @throws SQLException
     */
    private void incrementSequence(String sequenceWithLowValue, Statement st) throws SQLException {
        String sql = "alter sequence " + sequenceWithLowValue + " increment by " + lowestAcceptableSequenceValue;
        logger.info(sql);
        st.execute(sql);
    }

}
