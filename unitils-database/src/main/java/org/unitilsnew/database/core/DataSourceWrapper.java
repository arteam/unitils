/*
 * Copyright 2012,  Unitils.org
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
package org.unitilsnew.database.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.core.UnitilsException;
import org.unitilsnew.database.config.DatabaseConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapper {

    /* The logger instance for this class */
    protected static final Log logger = LogFactory.getLog(DataSourceWrapper.class);

    protected DatabaseConfiguration databaseConfiguration;
    /* The data source that should be used, not null */
    protected DataSource wrappedDataSource;
    protected TransactionAwareDataSourceProxy transactionAwareDataSourceProxy;
    protected SimpleJdbcTemplate simpleJdbcTemplate;


    // todo javadoc
    // todo exceptions when null values
    // todo spring bean
    // todo test connection => does not happen when datasource is created (verify clear exceptions)


    public DataSourceWrapper(DatabaseConfiguration databaseConfiguration, DataSource wrappedDataSource) {
        this.databaseConfiguration = databaseConfiguration;
        this.wrappedDataSource = wrappedDataSource;
    }


    public DataSource getDataSource(boolean wrapDataSourceInTransactionalProxy) {
        if (!wrapDataSourceInTransactionalProxy || wrappedDataSource instanceof TransactionAwareDataSourceProxy) {
            // no wrapping requested or needed
            return wrappedDataSource;
        }
        if (transactionAwareDataSourceProxy == null) {
            transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(wrappedDataSource);
        }
        return transactionAwareDataSourceProxy;
    }

    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
        if (simpleJdbcTemplate == null) {
            simpleJdbcTemplate = new SimpleJdbcTemplate(wrappedDataSource);
        }
        return simpleJdbcTemplate;
    }

    /**
     * @return A connection from the data source, not null
     */
    public Connection getConnection() {
        try {
            return DataSourceUtils.getConnection(wrappedDataSource);
        } catch (Exception e) {
            throw new UnitilsException("Unable to connect to database for " + databaseConfiguration + ".", e);
        }
    }

    public void releaseConnection(Connection connection) {
        DataSourceUtils.releaseConnection(connection, wrappedDataSource);
    }


    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    /**
     * @return The data source that should be used, not null
     */
    public DataSource getWrappedDataSource() {
        return wrappedDataSource;
    }


    /**
     * Executes the given update statement.
     *
     * @param sql The sql string for retrieving the items
     * @return The nr of updates
     */
    public int executeUpdate(String sql, Object... args) {
        logger.debug(sql);
        try {
            SimpleJdbcTemplate simpleJdbcTemplate = getSimpleJdbcTemplate();
            return simpleJdbcTemplate.update(sql, args);
        } catch (Exception e) {
            throw new UnitilsException("Unable to execute statement: '" + sql + "'.", e);
        }
    }

    /**
     * Executes the given statement ignoring all exceptions.
     *
     * @param sql The sql string for retrieving the items
     * @return The nr of updates, -1 if not successful
     */
    public int executeUpdateQuietly(String sql, Object... args) {
        try {
            return executeUpdate(sql, args);
        } catch (Exception e) {
            // Ignored
            return -1;
        }
    }


    /**
     * @param tableName The table, not null
     * @return The nr of rows in the table
     */
    public long getTableCount(String tableName) {
        if (isBlank(tableName)) {
            throw new UnitilsException("Unable to get table count. Table name is null or empty.");
        }
        return getLong("select count(1) from " + tableName);
    }

    /**
     * @param tableName The table, not null
     * @return True if the table is empty
     */
    public boolean isTableEmpty(String tableName) {
        return getTableCount(tableName) == 0;
    }


    /**
     * Returns the value extracted from the result of the given query.
     * If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The string value
     */
    public String getString(String sql, Object... args) {
        return getObject(sql, String.class, args);
    }

    /**
     * Returns the strings extracted from the result of the given query.
     *
     * @param sql The sql string for retrieving the items
     * @return The strings, not null
     */
    public List<String> getStringList(String sql, Object... args) {
        return getObjectList(sql, String.class, args);
    }

    /**
     * Returns the value extracted from the result of the given query. If no value is found, a {@link org.unitils.core.UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The string item value
     */
    public List<List<String>> getRowsAsString(String sql, Object... args) {
        logger.debug(sql);
        try {
            SimpleJdbcTemplate simpleJdbcTemplate = getSimpleJdbcTemplate();
            return simpleJdbcTemplate.query(sql, new RowParameterizedRowMapper(), args);
        } catch (Exception e) {
            throw new UnitilsException("Unable to execute statement: '" + sql + "'.", e);
        }
    }

    protected static class RowParameterizedRowMapper implements ParameterizedRowMapper<List<String>> {
        public List<String> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<String> row = new ArrayList<String>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String value = resultSet.getString(i);
                row.add(value);
            }
            return row;
        }
    }


    /**
     * Returns the boolean extracted from the result of the given query.
     * If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The boolean value
     */
    public boolean getBoolean(String sql, Object... args) {
        Boolean result = getObject(sql, Boolean.class, args);
        if (result == null) {
            // todo unit test
            throw new UnitilsException("Unable to get boolean value. Statement returned a null value: '" + sql + "'.");
        }
        return result;
    }

    /**
     * Returns the booleans extracted from the result of the given query.
     *
     * @param sql The sql string for retrieving the items
     * @return The booleans, not null
     */
    public List<Boolean> getBooleanList(String sql, Object... args) {
        return getObjectList(sql, Boolean.class, args);
    }


    /**
     * Returns the int extracted from the result of the given query.
     * If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The int value
     */
    public int getInteger(String sql, Object... args) {
        Integer result = getObject(sql, Integer.class, args);
        if (result == null) {
            // todo unit test
            throw new UnitilsException("Unable to get int value. Statement returned a null value: '" + sql + "'.");
        }
        return result;
    }

    /**
     * Returns the integers extracted from the result of the given query.
     *
     * @param sql The sql string for retrieving the items
     * @return The integers, not null
     */
    public List<Integer> getIntegerList(String sql, Object... args) {
        return getObjectList(sql, Integer.class, args);
    }


    /**
     * Returns the long extracted from the result of the given query.
     * If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The long value
     */
    public long getLong(String sql, Object... args) {
        Long result = getObject(sql, Long.class, args);
        if (result == null) {
            // todo unit test
            throw new UnitilsException("Unable to get long value. Statement returned a null value: '" + sql + "'.");
        }
        return result;
    }

    /**
     * Returns the longs extracted from the result of the given query.
     *
     * @param sql The sql string for retrieving the items
     * @return The longs, not null
     */
    public List<Long> getLongList(String sql, Object... args) {
        return getObjectList(sql, Long.class, args);
    }


    /**
     * Returns the boolean extracted from the result of the given query.
     * If no value is found, a {@link UnitilsException} is thrown.
     *
     * @param sql The sql string for retrieving the items
     * @return The boolean value
     */
    public <T> T getObject(String sql, Class<T> type, Object... args) {
        if (type == null) {
            // todo unit test
            throw new UnitilsException("Unable to get value. Type cannot be null.");
        }
        logger.debug(sql);
        try {
            SimpleJdbcTemplate simpleJdbcTemplate = getSimpleJdbcTemplate();
            return simpleJdbcTemplate.queryForObject(sql, type, args);

        } catch (EmptyResultDataAccessException e) {
            throw new UnitilsException("Unable to get value. Statement did not produce any results: '" + sql + "'.", e);
        } catch (IncorrectResultSizeDataAccessException e) {
            // todo unit test
            throw new UnitilsException("Unable to get value. Statement produced more than 1 result: '" + sql + "'.", e);
        } catch (Exception e) {
            throw new UnitilsException("Unable to execute statement: '" + sql + "'.", e);
        }
    }

    /**
     * Returns the strings extracted from the result of the given query.
     *
     * @param sql The sql string for retrieving the items
     * @return The strings, not null
     */
    public <T> List<T> getObjectList(String sql, Class<T> type, Object... args) {
        if (type == null) {
            // todo unit test
            throw new UnitilsException("Unable to get value list. Type cannot be null.");
        }
        logger.debug(sql);
        try {
            SimpleJdbcTemplate simpleJdbcTemplate = getSimpleJdbcTemplate();
            ParameterizedSingleColumnRowMapper<T> rowMapper = ParameterizedSingleColumnRowMapper.newInstance(type);
            return simpleJdbcTemplate.query(sql, rowMapper, args);
        } catch (Exception e) {
            throw new UnitilsException("Unable to execute statement: '" + sql + "'.", e);
        }
    }
}
