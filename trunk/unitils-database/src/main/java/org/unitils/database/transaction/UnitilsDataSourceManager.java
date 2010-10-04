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
package org.unitils.database.transaction;

import org.dbmaintain.database.DatabaseInfo;
import org.dbmaintain.database.DatabaseInfoFactory;
import org.dbmaintain.database.Databases;
import org.dbmaintain.database.DatabasesFactory;
import org.dbmaintain.database.impl.DefaultSQLHandler;
import org.springframework.context.ApplicationContext;
import org.unitils.core.UnitilsException;
import org.unitils.database.UnitilsDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDataSourceManager {

    /* The configuration of Unitils */
    protected Properties configuration;

    private Databases databases;
    private Map<String, DataSource> dataSourcesPerDatabaseName = new HashMap<String, DataSource>();


    public UnitilsDataSourceManager(Properties configuration) {
        this.configuration = configuration;
    }


    public DataSource getDataSource(String databaseName, ApplicationContext applicationContext) {
        DataSource dataSource = dataSourcesPerDatabaseName.get(databaseName);
        if (dataSource == null) {
            dataSource = createDataSource(databaseName, applicationContext);
            dataSourcesPerDatabaseName.put(databaseName, dataSource);
        }
        return dataSource;
    }

    public Databases getDatabases() {
        if (databases == null) {
            DatabaseInfoFactory databaseInfoFactory = new DatabaseInfoFactory(configuration);
            List<DatabaseInfo> databaseInfos = databaseInfoFactory.getDatabaseInfos();
            DatabasesFactory databasesFactory = new DatabasesFactory(configuration, new DefaultSQLHandler());
            databases = databasesFactory.createDatabases(databaseInfos);
        }
        return databases;
    }


    protected DataSource createDataSource(String databaseName, ApplicationContext applicationContext) {
        DataSource dataSource = getDataSourceFromApplicationContext(databaseName, applicationContext);
        if (dataSource != null) {
            return dataSource;
        }
        Databases databases = getDatabases();
        if (databaseName == null) {
            return databases.getDefaultDatabase().getDataSource();
        }
        return databases.getDatabase(databaseName).getDataSource();
    }


    protected DataSource getDataSourceFromApplicationContext(String databaseName, ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return null;
        }
        String foundBeanName = null;
        DataSource dataSource = null;

        Map<String, UnitilsDataSource> unitilsDataSourceBeans = applicationContext.getBeansOfType(UnitilsDataSource.class);
        for (Map.Entry<String, UnitilsDataSource> entry : unitilsDataSourceBeans.entrySet()) {
            String beanName = entry.getKey();
            UnitilsDataSource unitilsDataSource = entry.getValue();

            if (unitilsDataSource.hasName(databaseName)) {
                if (dataSource != null) {
                    if (beanName == null) {
                        beanName = "<no-name>";
                    }
                    if (databaseName == null) {
                        throw new UnitilsException("Unable to configure default unitils data source. More than one default data source was configured in UnitilsDataSourceBean with names " + beanName + " and " + foundBeanName);
                    }
                    throw new UnitilsException("Unable to configure unitils data source for database name " + databaseName + ". More than one data source was configured for this database name in UnitilsDataSourceBean with names " + beanName + " and " + foundBeanName);
                }
                foundBeanName = beanName;
                dataSource = unitilsDataSource.getDataSource();
            }

        }
        return dataSource;
    }
}
