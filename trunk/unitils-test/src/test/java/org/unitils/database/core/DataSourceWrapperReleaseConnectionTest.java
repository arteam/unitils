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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.database.config.DatabaseConfiguration;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import javax.sql.DataSource;
import java.sql.Connection;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperReleaseConnectionTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapper dataSourceWrapper;

    @TestDataSource
    private DataSource dataSource;
    private DatabaseConfiguration databaseConfiguration;
    private Mock<Connection> connection;


    @Before
    public void initialize() {
        databaseConfiguration = new DatabaseConfiguration("myDatabase", "myDialect", "myDriver", "myUrl", "myUser", "myPass", "schemaA", asList("schemaA", "schemaB"), false, true);
        dataSourceWrapper = new DataSourceWrapper(dataSource, databaseConfiguration);
    }


    @Test
    public void releaseConnection() throws Exception {
        dataSourceWrapper.releaseConnection(connection.getMock());
        connection.assertInvoked().close();
    }

    @Test
    public void ignoreWhenConnectionIsNull() throws Exception {
        dataSourceWrapper.releaseConnection(null);
        connection.assertNotInvoked().close();
    }

    @Test
    public void exceptionsAreIgnoredWhenFailure() throws Exception {
        connection.raises(new NullPointerException("message")).close();

        dataSourceWrapper.releaseConnection(connection.getMock());
        connection.assertInvoked().close();
    }
}
