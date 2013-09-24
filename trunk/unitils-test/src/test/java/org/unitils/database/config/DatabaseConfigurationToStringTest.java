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
package org.unitils.database.config;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class DatabaseConfigurationToStringTest {

    /* Tested object */
    private DatabaseConfiguration databaseConfiguration;


    @Test
    public void values() {
        databaseConfiguration = new DatabaseConfiguration("myDatabaseName", "myDialect", "myDriver", "myUrl", "myUser", "myPassword", "myDefaultSchema", asList("schema1", "schema2"), false, false);

        String result = databaseConfiguration.toString();
        assertEquals("database name: 'myDatabaseName', driver class name: 'myDriver', url: 'myUrl', user name: 'myUser', password: <not shown>, default schema name: 'myDefaultSchema', schema names: [schema1, schema2]", result);
    }

    @Test
    public void noDatabaseName() {
        databaseConfiguration = new DatabaseConfiguration(null, "myDialect", "myDriver", "myUrl", "myUser", "myPassword", "myDefaultSchema", asList("schema1", "schema2"), false, false);

        String result = databaseConfiguration.toString();
        assertEquals("driver class name: 'myDriver', url: 'myUrl', user name: 'myUser', password: <not shown>, default schema name: 'myDefaultSchema', schema names: [schema1, schema2]", result);
    }

    @Test
    public void nullValues() {
        databaseConfiguration = new DatabaseConfiguration(null, null, null, null, null, null, null, null, false, false);

        String result = databaseConfiguration.toString();
        assertEquals("driver class name: <null>, url: <null>, user name: <null>, password: <not shown>, default schema name: <null>, schema names: <null>", result);
    }
}
