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

import org.dbmaintain.database.Database;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsGetDatabaseIntegrationTest {

    @Test
    public void defaultDatabase() {
        Database result = DatabaseUnitils.getDatabase();
        assertEquals("database1", result.getDatabaseName());
    }

    @Test
    public void namedDatabase() {
        Database result = DatabaseUnitils.getDatabase("database2");
        assertEquals("database2", result.getDatabaseName());
    }

    @Test
    public void defaultDatabaseWhenNullName() {
        Database result = DatabaseUnitils.getDatabase(null);
        assertEquals("database1", result.getDatabaseName());
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            DatabaseUnitils.getDatabase("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get database with name: xxx\n" +
                    "Reason: DatabaseException: No database configured with name: xxx", e.getMessage());
        }
    }
}
