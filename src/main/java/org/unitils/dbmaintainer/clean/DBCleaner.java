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
package org.unitils.dbmaintainer.clean;

import org.unitils.dbmaintainer.handler.StatementHandlerException;

/**
 * TODO use this class
 * <p/>
 * Defines the contract for implementations that delete all data from the database, except for the tables that have been
 * configured as <i>tablesToPreserve</i>, and the table in which the database version is stored.
 */
public interface DBCleaner {

    /**
     * Deletes all data from the database, except for the tables that have been
     * configured as <i>tablesToPreserve</i>, and the table in which the database version is stored
     *
     * @throws StatementHandlerException
     */
    void cleanDatabase() throws StatementHandlerException;

}
