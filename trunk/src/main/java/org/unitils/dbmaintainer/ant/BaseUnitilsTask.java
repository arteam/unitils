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
package org.unitils.dbmaintainer.ant;

import org.apache.commons.configuration.Configuration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.unitils.core.Unitils;
import org.unitils.db.config.DataSourceFactory;
import org.unitils.util.ReflectionUtils;

import javax.sql.DataSource;

/**
 * Base ant task for Unitils database operations
 */
public abstract class BaseUnitilsTask extends Task {

    /* Property key of the implementation of the DbSupport interface */
    public static final String PROPKEY_DBSUPPORT_CLASSNAME = "dbMaintainer.dbSupport.className";
    
    /* Property key of the datasource factory classname */
    private static final String PROPKEY_DATASOURCEFACTORY_CLASSNAME = "dataSourceFactory.className";

    /* Property key of the database schema name */
    private static final String PROPKEY_DATABASE_SCHEMANAME = "dataSource.schemaName";

    /* The pooled datasource instance */
    protected DataSource dataSource;

    /* The name of the database schema */
    protected String schemaName;

    /**
     * Configures the connection to the database, and executes the ant task
     *
     * @throws BuildException
     */
    public final void execute() throws BuildException {

        //todo move implementation to module?
        Configuration configuration = Unitils.getInstance().getConfiguration();
        DataSourceFactory dataSourceFactory = ReflectionUtils.createInstanceOfType(configuration.getString(PROPKEY_DATASOURCEFACTORY_CLASSNAME));
        dataSourceFactory.init(configuration);
        dataSource = dataSourceFactory.createDataSource();
        schemaName = configuration.getString(PROPKEY_DATABASE_SCHEMANAME);
        doExecute();
    }

    /**
     * Executes the ant task
     *
     * @throws BuildException
     */
    protected abstract void doExecute() throws BuildException;

}
