/*
 * Copyright 2006-2007,  Unitils.org
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

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Interface for different sorts of Factories of a TestDataSource.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DataSourceFactory {

    /**
     * Initializes itself using the properties in the given configuration.
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration);


    /**
     * Creates a new <code>TestDataSource</code>
     *
     * @return The TestDataSource, not null
     */
    public DataSource createDataSource();

}
