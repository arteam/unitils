/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database.core.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.database.config.DatabaseConfigurations;
import org.unitils.database.core.DataSourceProvider;
import org.unitils.database.core.DataSourceWrapper;
import org.unitils.database.core.DataSourceWrapperFactory;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tim Ducheyne
 */
public class DefaultDataSourceProvider implements DataSourceProvider {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DefaultDataSourceProvider.class);

    protected Map<DatabaseConfiguration, DataSourceWrapper> dataSourceWrappers = new IdentityHashMap<DatabaseConfiguration, DataSourceWrapper>(3);

    protected DatabaseConfigurations databaseConfigurations;
    protected DataSourceWrapperFactory dataSourceWrapperFactory;


    public DefaultDataSourceProvider(DatabaseConfigurations databaseConfigurations, DataSourceWrapperFactory dataSourceWrapperFactory) {
        this.databaseConfigurations = databaseConfigurations;
        this.dataSourceWrapperFactory = dataSourceWrapperFactory;
    }


    public List<String> getDatabaseNames() {
        return databaseConfigurations.getDatabaseNames();
    }

    public DataSourceWrapper getDataSourceWrapper(String databaseName) {
        DatabaseConfiguration databaseConfiguration = databaseConfigurations.getDatabaseConfiguration(databaseName);

        DataSourceWrapper dataSourceWrapper = dataSourceWrappers.get(databaseConfiguration);
        if (dataSourceWrapper == null) {
            dataSourceWrapper = createDataSourceWrapper(databaseConfiguration);
            dataSourceWrappers.put(databaseConfiguration, dataSourceWrapper);
        }
        return dataSourceWrapper;
    }


    protected DataSourceWrapper createDataSourceWrapper(DatabaseConfiguration databaseConfiguration) {
        logger.info("Creating data source for " + databaseConfiguration);
        return dataSourceWrapperFactory.create(databaseConfiguration);
    }
}
