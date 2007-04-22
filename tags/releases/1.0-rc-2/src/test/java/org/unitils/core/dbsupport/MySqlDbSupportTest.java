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
package org.unitils.core.dbsupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.unitils.core.dbsupport.TestSQLUtils.*;
import static org.unitils.core.util.SQLUtils.executeUpdate;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.Arrays;
import java.util.Set;

/**
 * Tests for the MySql database support.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MySqlDbSupportTest extends DbSupportTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(MySqlDbSupportTest.class);


    /**
     * Creates a new test for the MySqlDbSupport
     */
    public MySqlDbSupportTest() {
        super(new MySqlDbSupport());
    }


    /**
     * Tests getting the table names.
     * Overriden for MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
     */
    public void testGetTableNames() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTableNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("test_table"), dbSupport.toCorrectCaseIdentifier("Test_CASE_Table")), result);
    }


    /**
     * Tests getting the view names.
     * Overriden for MySQL quoting behavior: quoted identifiers are not treated as case sensitive.
     */
    public void testGetViewNames() throws Exception {
        if (disabled) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getViewNames();
        assertLenEquals(Arrays.asList(dbSupport.toCorrectCaseIdentifier("test_view"), dbSupport.toCorrectCaseIdentifier("Test_CASE_View")), result);
    }


    /**
     * Tests getting the trigger names.
     * <p/>
     * Overriden for MySQL trigger behavior: trigger names are case-sensitive
     */
    public void testGetTriggerNames() throws Exception {
        if (disabled || !dbSupport.supportsTriggers()) {
            logger.warn("Test is not for current dialect. Skipping test.");
            return;
        }
        Set<String> result = dbSupport.getTriggerNames();
        assertLenEquals(Arrays.asList("test_trigger", "Test_CASE_Trigger"), result);
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    protected void createTestDatabase() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected void cleanupTestDatabase() throws Exception {
        dropTestTables(dbSupport, "test_table", "`Test_CASE_Table`");
        dropTestViews(dbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(dbSupport, "test_trigger", "`Test_CASE_Trigger`");
    }

}
