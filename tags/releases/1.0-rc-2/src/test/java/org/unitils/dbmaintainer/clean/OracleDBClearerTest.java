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
package org.unitils.dbmaintainer.clean;

import static org.unitils.core.dbsupport.TestSQLUtils.*;
import static org.unitils.core.util.SQLUtils.executeUpdate;

/**
 * DbClearer test for an Oracle database
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDBClearerTest extends DBClearerTest {


    /**
     * Creates a new clearer test
     */
    public OracleDBClearerTest() {
        super("oracle");
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    protected void createTestDatabase() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create or replace trigger test_trigger before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end test_trigger", dataSource);
        executeUpdate("create or replace trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end \"Test_CASE_Trigger\"", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected void cleanupTestDatabase() throws Exception {
        dropTestTables(dbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(dbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(dbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(dbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(dbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(dbSupport, "test_type", "\"Test_CASE_Type\"");
    }

}
