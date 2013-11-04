/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.database.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.unitils.core.util.Configurable;

/**
 * Defines the contract of a factory that can provide an instance of a test <code>DataSource</code>.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSourceFactory extends Configurable {

    /**
     * Creates a new <code>DataSource</code>
     *
     * @return The DataSource, not null
     */
    DataSource createDataSource();
    
    /**
     * Configure the default database by {@link DatabaseConfiguration}
     * @param tempConfig
     */
    void init(DatabaseConfiguration tempConfig);
    
    /**
     * Initializes the database operation class with the given {@link Properties}
     *
     * @param configuration The configuration, not null
     * @param databaseName
     */
    public void init(Properties configuration, String databaseName);

}
