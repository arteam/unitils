/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.dbmaintainer.sequences;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.unitils.core.UnitilsException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link SequenceUpdater} for an Hsqldb database
 * <p/>
 * // todo javadoc
 * // todo finish updating of identity columns
 * // todo test
 */
public class HsqldbSequenceUpdater extends BaseSequenceUpdater {

    Logger logger = Logger.getLogger(HsqldbSequenceUpdater.class);

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
            incrementIdentityColumnsWithLowValue(conn);
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
     *
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
     *
     * @param sequenceName
     * @throws StatementHandlerException
     */
    private void incrementSequence(String sequenceName) throws StatementHandlerException {
        statementHandler.handle("alter sequence " + sequenceName + " restart with " + lowestAcceptableSequenceValue);
    }

    private void incrementIdentityColumnsWithLowValue(Connection conn) throws SQLException {
        List<String> tableNames = getTableNames(conn);
        for (String tableName : tableNames) {
            List<String> primaryKeyColumnNames = getPrimaryKeyColumnNames(tableName);
            if (primaryKeyColumnNames.size() == 1 && isNumericColumn(conn, tableName, primaryKeyColumnNames.get(0)) &&
                    getMaxValueForColumn(conn, tableName, primaryKeyColumnNames.get(0)) < lowestAcceptableSequenceValue) {
                try {
                    statementHandler.handle("alter table " + tableName + " alter column " + " RESTART WITH " + lowestAcceptableSequenceValue);
                } catch (StatementHandlerException e) {
                    logger.info("Column " + primaryKeyColumnNames.get(0) + " on table " + tableName + " is apperantly " +
                            "not an identity column");
                }
            }
        }
    }

    private List<String> getPrimaryKeyColumnNames(String tableName) {
        return Collections.emptyList(); // todo
    }

    private boolean isNumericColumn(Connection conn, String tableName, String s) {
        return false;  // todo
    }

    private long getMaxValueForColumn(Connection conn, String tableName, String s) {
        return 0L;  // todo
    }

    private List<String> getTableNames(Connection conn) throws SQLException {
        ResultSet rset = null;
        try {
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null,
                    new String[]{"TABLE"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(rset);
        }
    }

}
