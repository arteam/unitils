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
import org.unitils.core.UnitilsException;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlAssert.assertTableEmpty;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsGetDataSourceIntegrationTest {

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
        DataSource dataSource = DatabaseUnitils.getDataSource();

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("insert into my_table(id) values (111)");

        assertTableCount(1, "my_table", "database1");
        assertTableEmpty("my_table", "database2");
    }

    @Test
    public void namedDatabase() throws Exception {
        DataSource dataSource = DatabaseUnitils.getDataSource("database2");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("insert into my_table(id) values (111)");

        assertTableEmpty("my_table", "database1");
        assertTableCount(1, "my_table", "database2");
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            DatabaseUnitils.getDataSource("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        DataSource dataSource = DatabaseUnitils.getDataSource(null);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("insert into my_table(id) values (111)");

        assertTableCount(1, "my_table", "database1");
        assertTableEmpty("my_table", "database2");
    }

    @Test
    public void constructionForCoverage() {
        new DatabaseUnitils();
    }
}
