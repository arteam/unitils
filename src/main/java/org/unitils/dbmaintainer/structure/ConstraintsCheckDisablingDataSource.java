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
package org.unitils.dbmaintainer.structure;

import org.unitils.dbmaintainer.util.BaseDataSourceDecorator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.IdentityHashMap;

/**
 * Wrapper or decorator for a <code>TestDataSource</code> that makes sure that for every <code>Connection</code>
 * that is returned, the method {@link ConstraintsDisabler#disableConstraintsOnConnection(Connection)} is called.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ConstraintsCheckDisablingDataSource extends BaseDataSourceDecorator {

    /* The implementation of ConstraintsDisabler that is used */
    private ConstraintsDisabler constraintsDisabler;

    /* Map used to remember on which Connections we already disabled constraints checking. This is done to avoid that
       a database call is made each time we retrieve a Connection from the connection pool */
    private Map<Connection, Connection> connectionsWithConstraintsCheckingDisabled = new IdentityHashMap<Connection, Connection>();


    /**
     * Creates a new instance that wraps the given <code>DataSource</code>
     *
     * @param wrappedDataSource   The datasource, not null
     * @param constraintsDisabler The constraints disabler, not null
     */
    public ConstraintsCheckDisablingDataSource(DataSource wrappedDataSource, ConstraintsDisabler constraintsDisabler) {
        super(wrappedDataSource);
        this.constraintsDisabler = constraintsDisabler;
    }


    /**
     * Returns a new connection to the database, on which the method
     * {@link ConstraintsDisabler#disableConstraintsOnConnection(Connection)} has been called.
     *
     * @see DataSource#getConnection()
     */
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        if (!connectionsWithConstraintsCheckingDisabled.containsKey(conn)) {
            constraintsDisabler.disableConstraintsOnConnection(conn);
            connectionsWithConstraintsCheckingDisabled.put(conn, conn);
        }
        return conn;
    }


    /**
     * Returns a new connection to the database, on which the method
     * {@link ConstraintsDisabler#disableConstraintsOnConnection(Connection)} has been called.
     *
     * @see DataSource#getConnection(String,String)
     */
    public Connection getConnection(String username, String password) throws SQLException {
        Connection conn = super.getConnection(username, password);
        constraintsDisabler.disableConstraintsOnConnection(conn);
        return conn;
    }

}
