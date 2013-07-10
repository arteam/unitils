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

import org.dbmaintain.database.Databases;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DatabaseUnitilsGetDatabasesIntegrationTest {

    @Test
    public void getDatabases() {
        Databases result = DatabaseUnitils.getDatabases();
        assertEquals(2, result.getDatabases().size());
        assertEquals("database1", result.getDatabase("database1").getDatabaseName());
        assertEquals("database2", result.getDatabase("database2").getDatabaseName());
    }
}
