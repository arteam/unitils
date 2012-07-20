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

package org.unitils.database.core.impl;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.config.DatabaseConfigurations;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class DefaultDataSourceProviderGetDatabaseNamesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DefaultDataSourceProvider defaultDatabaseProvider;

    private Mock<DatabaseConfigurations> databaseConfigurationsMock;


    @Before
    public void initialize() {
        defaultDatabaseProvider = new DefaultDataSourceProvider(databaseConfigurationsMock.getMock(), null);
    }


    @Test
    public void getDatabaseNames() throws Exception {
        databaseConfigurationsMock.returns(asList("name 1", "name 2")).getDatabaseNames();

        List<String> result = defaultDatabaseProvider.getDatabaseNames();

        assertEquals(asList("name 1", "name 2"), result);
    }

    @Test
    public void noDatabases() throws Exception {
        List<String> result = defaultDatabaseProvider.getDatabaseNames();

        assertTrue(result.isEmpty());
    }
}
