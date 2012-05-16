/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitilsnew.database.config.DatabaseConfiguration;
import org.unitilsnew.database.config.DatabaseConfigurations;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperManager {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(DataSourceWrapperManager.class);

    protected static Map<String, DataSourceWrapper> dataSourceWrappers = new HashMap<String, DataSourceWrapper>(3);

    protected DatabaseConfigurations databaseConfigurations;
    protected DataSourceWrapperFactory dataSourceWrapperFactory;
    protected TransactionManager transactionManager;


    public DataSourceWrapperManager(DatabaseConfigurations databaseConfigurations, DataSourceWrapperFactory dataSourceWrapperFactory, TransactionManager transactionManager) {
        this.databaseConfigurations = databaseConfigurations;
        this.dataSourceWrapperFactory = dataSourceWrapperFactory;
        this.transactionManager = transactionManager;
    }


    public synchronized DataSourceWrapper getDataSourceWrapper(String databaseName) {
        if (isBlank(databaseName)) {
            databaseName = null;
        }
        DataSourceWrapper dataSourceWrapper = dataSourceWrappers.get(databaseName);
        if (dataSourceWrapper == null) {
            dataSourceWrapper = createDataSourceWrapper(databaseName);
            dataSourceWrappers.put(databaseName, dataSourceWrapper);
        }
        DataSource dataSource = dataSourceWrapper.getDataSource(false);
        transactionManager.registerDataSource(dataSource);
        return dataSourceWrapper;
    }


    protected DataSourceWrapper createDataSourceWrapper(String databaseName) {
        DatabaseConfiguration databaseConfiguration = databaseConfigurations.getDatabaseConfiguration(databaseName);
        logger.info("Creating data source for " + databaseConfiguration);
        return dataSourceWrapperFactory.create(databaseConfiguration);
    }
}
