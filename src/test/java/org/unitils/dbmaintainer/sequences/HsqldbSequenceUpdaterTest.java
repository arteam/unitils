package org.unitils.dbmaintainer.sequences;

import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Test class for the HsqldbSequenceUpdater 
 */
public class HsqldbSequenceUpdaterTest extends SequenceUpdaterTest {

    protected long getNextSequenceValue(Connection conn) throws SQLException {
        Statement st = null;
        ResultSet rset = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select next value for testsequence from testtable");
            rset.next();
            long sequenceValue = rset.getLong(1);
            return sequenceValue;
        } finally {
            DbUtils.closeQuietly(null, st, rset);
        }
    }
}
