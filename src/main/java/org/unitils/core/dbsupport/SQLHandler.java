package org.unitils.core.dbsupport;

import java.util.Set;

import javax.sql.DataSource;

import org.unitils.core.UnitilsException;

public interface SQLHandler {

	/**
	 * Executes the given statement.
	 *
	 * @param sql The sql statement
	 * @return The nr of updates
	 */
	int executeUpdate(String sql);
	
	/**
	 * Executes the given query. Note that no result is returned: this method is only useful in case you want
	 * to execute a query that has some desired side-effect (in fact, this method perfoms an update which is 
	 * disguised as a query ;-) )
	 * @param sql The sql query
	 */
	void executeQuery(String sql);
	
	/**
     * Executes the given statement and commits.
     *
     * @param sql The sql statement
     * @return The nr of updates
     */
    int executeUpdateAndCommit(String sql);

	/**
	 * Returns the long extracted from the result of the given query. If no value is found, a {@link UnitilsException}
	 * is thrown.
	 *
	 * @param sql The sql string for retrieving the items
	 * @return The long item value
	 */
	long getItemAsLong(String sql);

	/**
	 * Returns the value extracted from the result of the given query. If no value is found, a {@link UnitilsException}
	 * is thrown.
	 *
	 * @param sql The sql string for retrieving the items
	 * @return The string item value
	 */
	String getItemAsString(String sql);

	/**
	 * Returns the items extracted from the result of the given query.
	 *
	 * @param sql The sql string for retrieving the items
	 * @return The items, not null
	 */
	Set<String> getItemsAsStringSet(String sql);

	/**
	 * Returns true if the query returned a record.
	 *
	 * @param sql The sql string for checking the existence
	 * @return True if a record was returned
	 */
	boolean exists(String sql);

	/**
	 * @return The DataSource
	 */
	DataSource getDataSource();

	/**
	 * @return Whether updates are executed on the database or not
	 */
	boolean isDoExecuteUpdates();

}