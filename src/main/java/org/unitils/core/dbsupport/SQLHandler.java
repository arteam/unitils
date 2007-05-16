package org.unitils.core.dbsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;
import org.hibernate.connection.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.HashSet;

/**
 * Class to which database updates and queries are passed. Is in fact a utility class, but is a concrete instance to
 * enable decorating it or switching it with another implementation, allowing things like a dry run, creating a script
 * file or logging updates to a log file or database table.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class SQLHandler {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(SQLHandler.class);

    /* The DataSource that provides access to the database, on which all queries and updates are executed */
    private DataSource dataSource;

    /* Boolean that indicates whether database updates have to executed on the database or not. Setting this value
       to false can be useful when running in dry mode */
    private boolean doExecuteUpdates;


    /**
     * Constructs a new instance that connects to the given DataSource
     *
     * @param dataSource The data source, not null
     */
    public SQLHandler(DataSource dataSource) {
        this(dataSource, true);
    }


    /**
     * Constructs a new instance that connects to the given DataSource
     *
     * @param dataSource The data source, not null
     * @param doExecuteUpdates Boolean indicating whether updates should effectively be executed on the underlying
     *        database
     */
    public SQLHandler(DataSource dataSource, boolean doExecuteUpdates) {
        this.dataSource = dataSource;
        this.doExecuteUpdates = doExecuteUpdates;
    }

    /**
     * Executes the given statement.
     *
     * @param sql        The sql string for retrieving the items
     * @return The nr of updates
     */
    public int executeUpdate(String sql) {
        logger.info(sql);

        if (doExecuteUpdates) {
            Connection connection = null;
            Statement statement = null;
            try {
                connection = dataSource.getConnection();
                statement = connection.createStatement();
                return statement.executeUpdate(sql);

            } catch (Exception e) {
                throw new UnitilsException("Error while executing statement: " + sql, e);
            } finally {
                DbUtils.closeQuietly(connection, statement, null);
            }
        }
        return 0;
    }


    /**
     * Executes the given code update statement.
     *
     * @param sql        The sql string for retrieving the items
     * @return The nr of updates
     */
    public int executeCodeUpdate(String sql) {
        logger.info(sql);

        if (doExecuteUpdates) {
            Connection connection = null;
            Statement statement = null;
            try {
                connection = dataSource.getConnection();
                statement = connection.createStatement();
                return statement.executeUpdate(sql);

            } catch (Exception e) {
                throw new UnitilsException("Error while executing statement: " + sql, e);
            } finally {
                DbUtils.closeQuietly(connection, statement, null);
            }
        }
        return 0;
    }


    /**
     * Returns the long extracted from the result of the given query. If no value is found, a {@link UnitilsException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @return The long item value
     */
    public long getItemAsLong(String sql) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new UnitilsException("No item value found: " + sql);
    }


    /**
     * Returns the value extracted from the result of the given query. If no value is found, a {@link UnitilsException}
     * is thrown.
     *
     * @param sql        The sql string for retrieving the items
     * @return The string item value
     */
    public String getItemAsString(String sql) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }

        // in case no value was found, throw an exception
        throw new UnitilsException("No item value found: " + sql);
    }


    /**
     * Returns the items extracted from the result of the given query.
     *
     * @param sql        The sql string for retrieving the items
     * @return The items, not null
     */
    public Set<String> getItemsAsStringSet(String sql) {
        logger.debug(sql);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            Set<String> result = new HashSet<String>();
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
            return result;

        } catch (Exception e) {
            throw new UnitilsException("Error while executing statement: " + sql, e);
        } finally {
            closeQuietly(connection, statement, resultSet);
        }
    }

    /**
     * @return The DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     *
     * @return Whether updates are executed on the database or not
     */
    public boolean isDoExecuteUpdates() {
        return doExecuteUpdates;
    }
}
