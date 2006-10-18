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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * Implementation of {@link SequenceUpdater} for an Hsqldb database
 */
public class HsqldbSequenceUpdater extends BaseSequenceUpdater {

    /**
     * @see SequenceUpdater#updateSequences()
     */
    public void updateSequences() throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            incrementSequencesWithLowValue(conn);
//            incrementIdentityColumnsWithLowValue(conn);
        } catch (SQLException e) {
            throw new UnitilsException("Error while updating sequences", e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }
    
    private void incrementSequencesWithLowValue(Connection conn) throws SQLException, StatementHandlerException {
        List<String> sequenceNames = getSequenceNames(conn);
        for (String sequenceName : sequenceNames) {
            incrementSequence(sequenceName);
        }
    }

    /**
     * @param conn
     * @return The names of all sequences
     * @throws SQLException
     */
    private List<String> getSequenceNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        Statement st = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select SEQUENCE_NAME from INFORMATION_SCHEMA.SYSTEM_SEQUENCES where SEQUENCE_SCHEMA = '" + 
                    schemaName + "' and START_WITH < " + lowestAcceptableSequenceValue);
            List<String> sequenceNames = new ArrayList<String>();
            while (rset.next()) {
                sequenceNames.add(rset.getString("SEQUENCE_NAME"));
            }
            return sequenceNames;
        } finally {
            DbUtils.closeQuietly(null, st, rset);
        }
    }

    /**
     * Sets the next value of the sequence with the given sequence name to the lowest acceptacle sequence value.
     * @param sequenceName
     * @throws StatementHandlerException 
     */
    private void incrementSequence(String sequenceName) throws StatementHandlerException {
        Statement st = null;
        try {
            statementHandler.handle("alter sequence " + sequenceName + " restart with " + lowestAcceptableSequenceValue);
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

}
