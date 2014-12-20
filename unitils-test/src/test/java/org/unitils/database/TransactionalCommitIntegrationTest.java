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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotation.Transactional;

import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.database.util.TransactionMode.COMMIT;

/**
 * @author Tim Ducheyne
 */
@Transactional(COMMIT)
public class TransactionalCommitIntegrationTest extends UnitilsJUnit4 {


    @BeforeClass
    public static void initialize() {
        cleanup();
        executeUpdate("create table my_table (id int)");
    }

    @AfterClass
    public static void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
    }


    @Test
    public void insert1() throws Exception {
        executeUpdate("insert into my_table(id) values (111)");
        assertTableCount(1, "my_table");
    }

    @Test
    public void insert2() throws Exception {
        executeUpdate("insert into my_table(id) values (111)");
        assertTableCount(2, "my_table");
    }
}
