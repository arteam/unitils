/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.database.util;

import java.sql.*;
import java.util.Map;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class BaseConnectionProxy implements Connection {

    private Connection targetConnection;

    public BaseConnectionProxy(Connection wrappedConnection) {
        this.targetConnection = wrappedConnection;
    }

    public Statement createStatement() throws SQLException {
        return targetConnection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return targetConnection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return targetConnection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return targetConnection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        targetConnection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return targetConnection.getAutoCommit();
    }

    public void commit() throws SQLException {
        targetConnection.commit();
    }

    public void rollback() throws SQLException {
        targetConnection.rollback();
    }

    public void close() throws SQLException {
        targetConnection.close();
    }

    public boolean isClosed() throws SQLException {
        return targetConnection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return targetConnection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        targetConnection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return targetConnection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        targetConnection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return targetConnection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        targetConnection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return targetConnection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return targetConnection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        targetConnection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return targetConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return targetConnection.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        targetConnection.setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        targetConnection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return targetConnection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException {
        return targetConnection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return targetConnection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        targetConnection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        targetConnection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return targetConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return targetConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return targetConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return targetConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int columnIndexes[]) throws SQLException {
        return targetConnection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String columnNames[]) throws SQLException {
        return targetConnection.prepareStatement(sql, columnNames);
    }

    public Connection getTargetConnection() {
        return targetConnection;
    }
}
