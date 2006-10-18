/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.sequences;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Implementation of {@link SequenceUpdater} for an Oracle database
 */
public class OracleSequenceUpdater extends BaseSequenceUpdater {

    /**
     * @see SequenceUpdater#updateSequences()
     */
    public void updateSequences() throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            incrementSequencesWithLowValue(conn);
        } catch (SQLException e) {
            throw new UnitilsException("Error while retrieving database version", e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }
    
    /**
     * Makes sure the value of all sequences is equal or higher than <code>lowestAcceptableSequenceValue</code>
     * 
     * @param conn 
     * @throws SQLException
     * @throws StatementHandlerException 
     */
    protected void incrementSequencesWithLowValue(Connection conn) throws SQLException, StatementHandlerException {
        ResultSet rs = null;
        Statement st = null;
        try {
            st = conn.createStatement();
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
            DbUtils.closeQuietly(null, st, rs);
        }
    }

}
