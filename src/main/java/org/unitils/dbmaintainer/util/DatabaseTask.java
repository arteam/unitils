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

import java.util.Properties;

import javax.sql.DataSource;

import org.unitils.core.dbsupport.SQLHandler;

/**
 * Task that can be performed on a database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public interface DatabaseTask {

    /**
     * Initializes the database operation class with the given {@link Properties}, {@link DataSource}.
     *
     * @param configuration The configuration, not null
     * @param dataSource    The datasource, not null
     */
    public void init(Properties configuration, SQLHandler sqlHandler);

}
