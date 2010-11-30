/*
 * Copyright Unitils.org
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
package org.unitils.dataset.database;

import org.dbmaintain.database.IdentifierProcessor;
import org.dbmaintain.database.IdentifierProcessorFactory;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.UnitilsDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapperFactory {

    /* The unitils configuration */
    protected Properties configuration;

    protected Map<String, DataSourceWrapper> dataSourceWrappersPerDatabaseName = new HashMap<String, DataSourceWrapper>();


    public DataSourceWrapperFactory(Properties configuration) {
        this.configuration = configuration;
    }


    public DataSourceWrapper getDataSourceWrapper(String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrappersPerDatabaseName.get(databaseName);
        if (dataSourceWrapper == null) {
            dataSourceWrapper = createDataSourceWrapper(databaseName);
            dataSourceWrappersPerDatabaseName.put(databaseName, dataSourceWrapper);
        }
        return dataSourceWrapper;
    }

    public void invalidateCachedDatabaseMetaData() {
        for (DataSourceWrapper dataSourceWrapper : dataSourceWrappersPerDatabaseName.values()) {
            dataSourceWrapper.reset();
        }
    }


    protected DataSourceWrapper createDataSourceWrapper(String databaseName) {
        UnitilsDataSource unitilsDataSource = DatabaseUnitils.getUnitilsDataSource(databaseName);
        IdentifierProcessor identifierProcessor = createIdentifierProcessor(unitilsDataSource);
        return new DataSourceWrapper(unitilsDataSource, identifierProcessor);
    }

    protected IdentifierProcessor createIdentifierProcessor(UnitilsDataSource unitilsDataSource) {
        String databaseDialect = unitilsDataSource.getDialect();
        String defaultSchemaName = unitilsDataSource.getDefaultSchemaName();
        DataSource dataSource = unitilsDataSource.getDataSource();
        IdentifierProcessorFactory identifierProcessorFactory = new IdentifierProcessorFactory(configuration);
        return identifierProcessorFactory.createIdentifierProcessor(databaseDialect, defaultSchemaName, dataSource);
    }
}