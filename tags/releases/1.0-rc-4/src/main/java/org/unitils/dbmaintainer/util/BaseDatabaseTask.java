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
package org.unitils.dbmaintainer.util;

import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupports;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;

import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.SQLHandler;

import java.util.List;
import java.util.Properties;

/**
 * Base class for a database task.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class BaseDatabaseTask implements DatabaseTask {

    /* Provides connections to the unit test database*/
    protected SQLHandler sqlHandler;

    /* DbSupport for the default schema */
    protected DbSupport defaultDbSupport;

    /* DbSupport for all schemas */
    protected List<DbSupport> dbSupports;


    /**
     * Initializes the database operation class with the given {@link Properties}, {@link DataSource}.
     *
     * @param configuration The configuration, not null
     * @param sqlHandler    The sql handler, not null
     */
    public void init(Properties configuration, SQLHandler sqlHandler) {
        this.sqlHandler = sqlHandler;
        this.dbSupports = getDbSupports(configuration, sqlHandler);
        this.defaultDbSupport = getDefaultDbSupport(configuration, sqlHandler);

        doInit(configuration);
    }


    /**
     * Allows subclasses to perform some extra configuration using the given configuration.
     *
     * @param configuration The configuration, not null
     */
    abstract protected void doInit(Properties configuration);


}
