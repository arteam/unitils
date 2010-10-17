/*
 * Copyright Unitils.org
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
package org.unitils.database.datasource.impl;

import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.DatabaseInfo;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;

import javax.sql.DataSource;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_DRIVERCLASSNAME;
import static org.dbmaintain.config.DbMaintainProperties.PROPERTY_URL;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DefaultDataSourceFactoryCreateDataSourceTest {

    /* Tested object */
    private DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory();

    private Properties configuration;
    private DatabaseInfo databaseInfo;


    @Before
    public void initialize() {
        configuration = new ConfigurationLoader().loadConfiguration();
        databaseInfo = new DatabaseInfo("test", "hsqldb", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:unitils", "sa", "", asList("public"), false);
    }


    @Test
    public void createDataSource() throws Exception {
        defaultDataSourceFactory.init(configuration);
        DataSource dataSource = defaultDataSourceFactory.createDataSource(databaseInfo);

        assertNotNull(dataSource.getConnection());
    }

    @Test
    public void verifyThatAConnectionCanBeCreated() throws Exception {
        configuration.setProperty(PROPERTY_DRIVERCLASSNAME, "xxxxx");
        configuration.setProperty(PROPERTY_URL, "xxxxx");
        defaultDataSourceFactory.init(configuration);

        try {
            defaultDataSourceFactory.createDataSource(databaseInfo);
        } catch (DatabaseException e) {
            assertTrue(e.getMessage().contains("Unable to connect to database. Driver: xxxxx, url: xxxxx, user: sa, password: <not shown>"));
        }
    }
}
