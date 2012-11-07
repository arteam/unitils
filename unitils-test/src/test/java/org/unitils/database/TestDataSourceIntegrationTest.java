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

package org.unitils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.unitils.database.annotation.TestDataSource;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlAssert.assertTableEmpty;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class TestDataSourceIntegrationTest extends UnitilsJUnit4 {

    @TestDataSource
    private DataSource defaultDataSource;
    @TestDataSource("database1")
    private DataSource dataSource1;
    @TestDataSource("database2")
    private DataSource dataSource2;


    @Before
    public void initialize() {
        executeUpdate("create table my_table (id int)", "database1");
        executeUpdate("create table my_table (id int)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        assertSame(defaultDataSource, dataSource1);
        assertNotSame(defaultDataSource, dataSource2);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDataSource);
        jdbcTemplate.execute("insert into my_table(id) values (111)");

        assertTableCount(1, "my_table", "database1");
        assertTableEmpty("my_table", "database2");
    }

    @Test
    public void namedDatabase() throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource2);
        jdbcTemplate.execute("insert into my_table(id) values (111)");

        assertTableEmpty("my_table", "database1");
        assertTableCount(1, "my_table", "database2");
    }
}
