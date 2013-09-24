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
package org.unitils.database;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.database.core.DataSourceWrapper;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.unitils.database.DatabaseUnitils.getDataSource;

/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsGetDataSourceWrapperIntegrationTest {

    private DataSource defaultDataSource;
    private DataSource dataSource2;


    @Before
    public void initialize() {
        defaultDataSource = getDataSource();
        dataSource2 = getDataSource("database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        DataSourceWrapper dataSourceWrapper = DatabaseUnitils.getDataSourceWrapper();
        assertSame(defaultDataSource, dataSourceWrapper.getWrappedDataSource());
    }

    @Test
    public void namedDatabase() throws Exception {
        DataSourceWrapper dataSourceWrapper = DatabaseUnitils.getDataSourceWrapper("database2");
        assertSame(dataSource2, dataSourceWrapper.getWrappedDataSource());
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            DatabaseUnitils.getDataSourceWrapper("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        DataSourceWrapper dataSourceWrapper = DatabaseUnitils.getDataSourceWrapper(null);
        assertSame(defaultDataSource, dataSourceWrapper.getWrappedDataSource());
    }
}
