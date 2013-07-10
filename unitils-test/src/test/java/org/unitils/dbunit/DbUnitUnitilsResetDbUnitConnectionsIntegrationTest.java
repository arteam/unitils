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
package org.unitils.dbunit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitilsnew.UnitilsJUnit4;

import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;
import static org.unitils.dbunit.DbUnitUnitils.insertDataSet;
import static org.unitils.dbunit.DbUnitUnitils.resetDbUnitConnections;

/**
 * @author Tim Ducheyne
 */
public class DbUnitUnitilsResetDbUnitConnectionsIntegrationTest extends UnitilsJUnit4 {


    @Before
    public void initialize() throws Exception {
        dropTestTable();
        resetDbUnitConnections();
    }

    @After
    public void cleanUp() throws Exception {
        dropTestTable();
    }


    @Test
    public void defaultDataSet() throws Exception {
        try {
            insertDataSet(DbUnitUnitilsResetDbUnitConnectionsIntegrationTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            // expected, table does not exist yet
        }
        executeUpdate("create table table_a (value varchar(100))");
        try {
            insertDataSet(DbUnitUnitilsResetDbUnitConnectionsIntegrationTest.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            // expected, db unit connection is no longer up to date
        }

        resetDbUnitConnections();
        insertDataSet(DbUnitUnitilsResetDbUnitConnectionsIntegrationTest.class);
    }


    private void dropTestTable() {
        executeUpdateQuietly("drop table table_a");
    }
}
