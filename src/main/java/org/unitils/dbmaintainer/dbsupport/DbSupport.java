package org.unitils.dbmaintainer.dbsupport;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class that implements a number of common operations on a database schema. Operations that can be implemented
 * using general JDBC or ANSI SQL constructs, are impelemented in this base abstract class. Operations that are DBMS
 * specific are abstract, and their implementation is left to DBMS specific subclasses.
 */
abstract public class DbSupport {

    /**
     * Logger instance
     */
    private static Logger logger = Logger.getLogger(DbSupport.class);

    /**
     * The name of the database schema
     */
    protected String schemaName;

    /**
     * StatementHandler by which all updates to the database are handled
     */
    protected StatementHandler statementHandler;

    /**
     * Gives access to the database
     */
    protected DataSource dataSource;

    /**
     * Creates a new, unconfigured instance. To have a instance that can be used, the {@link #init} method must be
     * called first.
     */
    protected DbSupport() {
    }

    /**
     * Initializes this DbSupport object with the given schemaName, statementHandler and dataSource
     * @param dataSource
     * @param schemaName
     * @param statementHandler
     */
    public void init(DataSource dataSource, String schemaName, StatementHandler statementHandler) {
        this.schemaName = schemaName;
        this.statementHandler = statementHandler;
        this.dataSource = dataSource;
    }

    /**
     * Returns the names of all tables in the database.
     *
     * @return the names of all tables in the database.
     * @throws java.sql.SQLException
     */
    public Set<String> getTableNames() throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            Set<String> tableNames = new HashSet<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null, null);
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName.toUpperCase());
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(conn, null, rset);
        }
    }

    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return the names of all views
     * @throws SQLException
     */
    public List<String> getViewNames() throws SQLException {
        Connection conn = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            List<String> tableNames = new ArrayList<String>();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            rset = databaseMetadata.getTables(null, schemaName.toUpperCase(), null,
                    new String[]{"VIEW"});
            while (rset.next()) {
                String tableName = rset.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            return tableNames;
        } finally {
            DbUtils.closeQuietly(conn, null, rset);
        }
    }

    /**
     * @return The names of all sequences in the database
     * @throws SQLException
     */
    abstract public Set<String> getSequenceNames() throws SQLException;

    /**
     * @return The names of all triggers in the database
     *
     * @throws SQLException
     */
    abstract public Set<String> getTriggerNames() throws SQLException;

    /**
     * Removes the view with the given name from the database
     *
     * @param viewName
     * @throws SQLException
     * @throws org.unitils.dbmaintainer.handler.StatementHandlerException
     */
    abstract public void dropView(String viewName) throws SQLException,
            StatementHandlerException;

    /**
     * Removes the table with the given name from the database
     *
     * @param tableName
     * @throws SQLException
     * @throws StatementHandlerException
     */
    abstract public void dropTable(String tableName) throws SQLException,
            StatementHandlerException;

    /**
     * Drops the sequence with the given name from the database
     *
     * @param sequenceName
     * @throws StatementHandlerException
     */
    public void dropSequence(String sequenceName) throws StatementHandlerException {
        statementHandler.handle("drop sequence " + sequenceName);
    }

    /**
     * Drops the trigger with the given name from the database
     *
     * @param triggerName
     * @throws StatementHandlerException
     */
    public void dropTrigger(String triggerName) throws StatementHandlerException {
        statementHandler.handle("drop trigger " + triggerName);
    }

    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName
     * @return the value of the sequence with the given name
     */
    abstract public long getNextValueOfSequence(String sequenceName) throws SQLException;

    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName
     * @param newSequenceValue
     * @throws StatementHandlerException
     */
    abstract public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException, SQLException;

    /**
     * Indicates whether the underlying DBMS supports identity columns
     *
     * @return true if identity is supported, false otherwise
     */
    abstract public boolean supportsIdentityColumns();

    /**
     * @param tableName
     * @return The names of the primary key columns of the table with the given name
     * @throws SQLException
     */
    public Set<String> getPrimaryKeyColumnNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rset = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rset = metaData.getPrimaryKeys(null, schemaName, tableName);
            Set<String> primaryKeyColumnNames = new HashSet<String>();
            while (rset.next()) {
                primaryKeyColumnNames.add(rset.getString("COLUMN_NAME"));
            }
            return primaryKeyColumnNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rset);
        }
    }

    /**
     * Increments the identity value for the specified primary key on the specified table to the given value. If there
     * is no identity specified on the given primary key, the method silently finishes without effect.
     *
     * @param tableName
     * @param primaryKeyColumnName
     * @param identityValue
     */
    abstract public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue);

    /**
     * Disables foreign key checking on all subsequent operations that are performed on the given connection object
     * @param conn
     */
    abstract public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn);

    /**
     * Removes the not-null constraint on the specified column and table
     * @param tableName
     * @param columnName
     * @throws StatementHandlerException
     */
    abstract public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException;

    /**
     * Returns the names of all columns that have a 'not-null' constraint on them
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public Set<String> getNotNullColummnNames(String tableName) throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            rs = metaData.getColumns(null, schemaName, tableName, null);
            Set<String> notNullColumnNames = new HashSet<String>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                // Check if the column is not the primary key column

                boolean nullable = rs.getBoolean("NULLABLE");
                if (!nullable) {
                    notNullColumnNames.add(columnName);
                }
            }
            return notNullColumnNames;
        } finally {
            DbUtils.closeQuietly(conn, null, rs);
        }
    }

    /**
     * Returns the constraint names for the table with the given name
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    abstract public Set<String> getTableConstraintNames(String tableName) throws SQLException;

    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName
     * @param constraintName
     * @throws StatementHandlerException
     */
    abstract public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException;

    /**
     * @return Column type suitable to store values of the Java <code>java.lang.Long</code> type
     */
    abstract public String getLongDataType();
}
