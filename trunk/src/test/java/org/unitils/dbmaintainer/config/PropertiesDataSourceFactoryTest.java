/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.unitils.UnitilsJUnit3;
import static org.unitils.easymock.EasyMockUnitils.replay;
import org.unitils.easymock.annotation.LenientMock;

/**
 * todo javadoc
 */
public class PropertiesDataSourceFactoryTest extends UnitilsJUnit3 {

    private PropertiesDataSourceFactory propertiesFileDataSourceConfig;

    @LenientMock
    private BasicDataSource mockBasicDataSource;


    public void setUp() throws Exception {

        Configuration configuration = new PropertiesConfiguration();
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
