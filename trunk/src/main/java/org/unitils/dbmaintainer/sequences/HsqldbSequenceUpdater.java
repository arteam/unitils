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
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.configuration.Configuration;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;

/**
 * Implementation of {@link SequenceUpdater} for an Hsqldb database
 */
public class HsqldbSequenceUpdater extends BaseSequenceUpdater {

    public void init(Configuration configuration, DataSource dataSource, StatementHandler statementHandler) {
        super.init(configuration, dataSource, statementHandler);
    }

    /**
     * @see SequenceUpdater#updateSequences()
     */
    public void updateSequences() throws StatementHandlerException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            String dummyTableName = createDummyTableWithDummyRecord(conn);
            incrementSequencesWithLowValue(conn, dummyTableName);
            dropTable(conn, dummyTableName);
//            incrementIdentityColumnsWithLowValue(conn);
        } catch (SQLException e) {
            throw new UnitilsException("Error while updating sequences", e);
        } finally {
            DbUtils.closeQuietly(conn, st, null);
        }
    }

    private String createDummyTableWithDummyRecord(Connection conn) throws SQLException {
        int i = 0;
        String tempTableName;
        do {
            tempTableName = "temptable" + (i++);
        } while (tableExists(conn, tempTableName));
        createTable(conn, tempTableName);
        insertRecord(conn, tempTableName);
        return tempTableName;
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        ResultSet rs = null;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getTables(null, schemaName, tableName, null);
            return rs.next();
        } finally {
            DbUtils.closeQuietly(rs);
        }
    }

    private void createTable(Connection conn, String tableName) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("create table " + tableName + " (tmp varchar(1))");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void dropTable(Connection conn, String tableName) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("drop table " + tableName);
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void insertRecord(Connection conn, String tableName) throws SQLException {
        Statement st = null;
        try {
            st = conn.createStatement();
            st.executeUpdate("insert into " + tableName + " values ('x')");
        } finally {
            DbUtils.closeQuietly(st);
        }
    }

    private void incrementSequencesWithLowValue(Connection conn, String dummyTableName) throws SQLException, StatementHandlerException {
        List<String> sequenceNames = getSequenceNames(conn);
        for (String sequenceName : sequenceNames) {
            if (getSequenceValue(sequenceName, conn, dummyTableName) < lowestAcceptableSequenceValue) {
                incrementSequence(sequenceName);
            }
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
                    schemaName + "'");
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
     * Returns the value of the sequence with the given name
     * @param sequenceName
     * @param conn
     * @param tableName
     * @return the value of the sequence with the given name
     */
    private long getSequenceValue(String sequenceName, Connection conn, String tableName) throws SQLException {
        Statement st = null;
        ResultSet rset = null;
        try {
            st = conn.createStatement();
            rset = st.executeQuery("select next value for " + sequenceName + " from " + tableName);
            rset.next();
            long sequenceValue = rset.getLong(1);
            return sequenceValue;
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
        statementHandler.handle("alter sequence " + sequenceName + " restart with " + lowestAcceptableSequenceValue);
    }

}
