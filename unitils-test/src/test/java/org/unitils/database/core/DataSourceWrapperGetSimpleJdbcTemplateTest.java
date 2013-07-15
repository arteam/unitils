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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperGetSimpleJdbcTemplateTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapper dataSourceWrapper;

    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dataSourceWrapper = new DataSourceWrapper(dataSource, null);
    }


    @Test
    public void getSimpleJdbcTemplate() {
        SimpleJdbcTemplate result = dataSourceWrapper.getSimpleJdbcTemplate();
        assertSame(dataSource, ((JdbcTemplate) result.getJdbcOperations()).getDataSource());
    }

    @Test
    public void cacheSimpleJdbcTemplate() {
        SimpleJdbcTemplate result1 = dataSourceWrapper.getSimpleJdbcTemplate();
        SimpleJdbcTemplate result2 = dataSourceWrapper.getSimpleJdbcTemplate();

        assertSame(result1, result2);
    }
}
