/*
 * Copyright 2008,  Unitils.org
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

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;

import java.util.Properties;

/**
 * Tests for the properties data source factory.
 */
public class PropertiesDataSourceFactoryTest extends UnitilsJUnit4 {

    /* Tested object */
    private PropertiesDataSourceFactory propertiesFileDataSource;

    private Mock<BasicDataSource> mockBasicDataSource;


    @Before
    public void setUp() throws Exception {
        Properties configuration = new Properties();
        configuration.setProperty("database.driverClassName", "testdriver");
        configuration.setProperty("database.url", "testurl");
        configuration.setProperty("database.userName", "testusername");
        configuration.setProperty("database.password", "testpassword");

        propertiesFileDataSource = new PropertiesDataSourceFactory() {
            @Override
            protected BasicDataSource getNewDataSource() {
                return mockBasicDataSource.getMock();
            }
        };
        propertiesFileDataSource.init(configuration);
    }


    @Test
    public void testCreateDataSource() {
        propertiesFileDataSource.createDataSource();

        mockBasicDataSource.assertInvoked().setDriverClassName("testdriver");
        mockBasicDataSource.assertInvoked().setUrl("testurl");
        mockBasicDataSource.assertInvoked().setUsername("testusername");
        mockBasicDataSource.assertInvoked().setPassword("testpassword");
    }

}
