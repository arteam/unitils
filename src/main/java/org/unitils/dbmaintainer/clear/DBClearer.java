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
package org.unitils.dbmaintainer.clear;

import org.apache.commons.configuration.Configuration;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.dbsupport.DbSupport;

import javax.sql.DataSource;

/**
 * Defines the contract for implementations that clear a database schema. This means, all the tables, views, constraints,
 * triggers and sequences are dropped, so that the database schema is empty.
 */
public interface DBClearer {

    /**
     * Clears the database schema. This means, all the tables, views, constraints, triggers and sequences are
     * dropped, so that the database schema is empty.
     *
     * @throws StatementHandlerException
     */
    void clearDatabase() throws StatementHandlerException;

}
