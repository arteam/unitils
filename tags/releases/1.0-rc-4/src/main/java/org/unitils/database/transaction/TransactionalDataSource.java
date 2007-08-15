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
package org.unitils.database.transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Extension to the <code>DataSource</code> interface that allows for retrieving transaction aware connections.
 * For example a connection can be returned that hooks into Spring's transaction management.
 * <p/>
 * Tests should typically do their own connection handling instead of using these transactional connections.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public interface TransactionalDataSource extends DataSource {


    /**
     * Retrieves a connection that can participate in a transaction.
     *
     * @return The connection, not null
     */
    public Connection getTransactionalConnection() throws SQLException;

}
