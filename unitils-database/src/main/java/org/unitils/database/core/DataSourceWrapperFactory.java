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
package org.unitils.database.core;

import org.apache.commons.dbcp.BasicDataSource;
import org.unitils.database.config.DatabaseConfiguration;

import javax.sql.DataSource;

/**
 * A data source factory that creates a commons DBCP BasicDataSource.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSourceWrapperFactory {


    /**
     * Returns an instance of {@link org.apache.commons.dbcp.BasicDataSource}.
     *
     * @return An instance of <code>BasicDataSource</code>
     */
    public DataSourceWrapper create(DatabaseConfiguration databaseConfiguration) {
        DataSource dataSource = createDataSource(databaseConfiguration);
        return new DataSourceWrapper(dataSource, databaseConfiguration);
    }


    protected DataSource createDataSource(DatabaseConfiguration databaseConfiguration) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(databaseConfiguration.getDriverClassName());
        dataSource.setUsername(databaseConfiguration.getUserName());
        dataSource.setPassword(databaseConfiguration.getPassword());
        dataSource.setUrl(databaseConfiguration.getUrl());
        return dataSource;
    }
}
