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
package org.unitils.database.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.Mock;

import java.util.Properties;

/**
 * todo javadoc
 */
public class PropertiesDataSourceFactoryTest extends UnitilsJUnit3 {

    private PropertiesDataSourceFactory propertiesFileDataSourceConfig;

    @Mock
    private BasicDataSource mockBasicDataSource;


    public void setUp() throws Exception {
        Properties configuration = new Properties();
        configuration.setProperty("dataSource.driverClassName", "testdriver");
        configuration.setProperty("dataSource.url", "testurl");
        configuration.setProperty("dataSource.userName", "testusername");
        configuration.setProperty("dataSource.password", "testpassword");

        propertiesFileDataSourceConfig = new PropertiesDataSourceFactory() {
            protected BasicDataSource getNewDataSource() {
                return mockBasicDataSource;
            }
        };
        propertiesFileDataSourceConfig.init(configuration);
    }


    public void testCreateDataSource() {
        // expectations
        mockBasicDataSource.setDriverClassName("testdriver");
        mockBasicDataSource.setUrl("testurl");
        mockBasicDataSource.setUsername("testusername");
        mockBasicDataSource.setPassword("testpassword");
        replay();

        propertiesFileDataSourceConfig.createDataSource();
    }

}
