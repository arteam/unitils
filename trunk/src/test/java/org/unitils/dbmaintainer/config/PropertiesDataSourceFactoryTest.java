/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.dbmaintainer.config;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import static org.easymock.classextension.EasyMock.*;
import org.unitils.core.UnitilsConfigurationLoader;

/**
 * todo
 */
public class PropertiesDataSourceFactoryTest extends TestCase {

    private PropertiesDataSourceFactory propertiesFileDataSourceConfig;

    private BasicDataSource mockBasicDataSource;

    public void setUp() throws Exception {

        Configuration configuration = new UnitilsConfigurationLoader().loadConfiguration();

        mockBasicDataSource = createMock(BasicDataSource.class);
        propertiesFileDataSourceConfig = new PropertiesDataSourceFactory() {
            protected BasicDataSource getNewDataSource() {
                return mockBasicDataSource;
            }
        };
        propertiesFileDataSourceConfig.init(configuration);
    }

    public void testCreateDataSource() {
        // expectations
        mockBasicDataSource.setUsername("testusername");
        mockBasicDataSource.setPassword("testpassword");
        mockBasicDataSource.setUrl("testurl");
        mockBasicDataSource.setDriverClassName("testdriver");

        replay(mockBasicDataSource);

        propertiesFileDataSourceConfig.createDataSource();

        // check expectations
        verify(mockBasicDataSource);
    }

}
