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
package org.unitils.dbmaintainer.handler;

import org.apache.commons.configuration.Configuration;

import javax.sql.DataSource;

/**
 * Defines the contract for classes that handle SQL statements. E.g. by logging these statements or by executing them
 * on a database.
 */
public interface StatementHandler {

    /**
     * Provides a <code>Configuration</code> and a <code>TestDataSource</code> object for initialization
     *
     * @param dataSource
     */
    void init(Configuration configuration, DataSource dataSource);

    /**
     * Handles the given SQL statement
     *
     * @param statement the SQL statement
     * @throws StatementHandlerException If the statement could not org handled correctly
     */
    void handle(String statement) throws StatementHandlerException;

}
