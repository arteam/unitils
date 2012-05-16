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

package org.unitilsnew.database.dbmaintain;

import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.config.Configuration;

import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainConfigurationFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainConfigurationFactory dbMaintainConfigurationFactory;

    private Mock<Configuration> configurationMock;
    private Mock<DbMaintainConfigurationLoader> dbMaintainConfigurationLoaderMock;

    @Dummy
    private Properties unitilsProperties;
    @Dummy
    private Properties dbMaintainProperties;


    @Before
    public void initialize() {
        dbMaintainConfigurationFactory = new DbMaintainConfigurationFactory(configurationMock.getMock(), dbMaintainConfigurationLoaderMock.getMock());
    }


    @Test
    public void create() {
        configurationMock.returns(unitilsProperties).getAllProperties();
        dbMaintainConfigurationLoaderMock.returns(dbMaintainProperties).loadConfiguration(unitilsProperties);

        DbMaintainConfiguration result = dbMaintainConfigurationFactory.create();

        assertSame(dbMaintainProperties, result.getProperties());
    }
}
