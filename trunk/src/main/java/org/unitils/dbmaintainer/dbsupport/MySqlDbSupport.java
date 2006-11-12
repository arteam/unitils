package org.unitils.dbmaintainer.dbsupport;

import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.handler.StatementHandler;

import javax.sql.DataSource;
import java.util.Set;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * 
 */
public class MySqlDbSupport extends DbSupport {

    public MySqlDbSupport() {
    }

    public Set<String> getSequenceNames() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<String> getTriggerNames() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropView(String viewName) throws SQLException, StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dropTable(String tableName) throws SQLException, StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getNextValueOfSequence(String sequenceName) throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean supportsIdentityColumns() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        throw new UnsupportedOperationException("Retrieval of table constraint names is not supported in MySQL");
    }

    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        throw new UnsupportedOperationException("Disabling of individual constraints is not supported in MySQL");
    }

    public String getLongDataType() {
        return "BIGINT";
    }
}
