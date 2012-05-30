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

package org.unitils.database.dbmaintain;

import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.config.Configuration;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainConfigurationFactory implements Factory<DbMaintainConfiguration> {

    protected Configuration configuration;
    protected DbMaintainConfigurationLoader dbMaintainConfigurationLoader;


    public DbMaintainConfigurationFactory(Configuration configuration, DbMaintainConfigurationLoader dbMaintainConfigurationLoader) {
        this.configuration = configuration;
        this.dbMaintainConfigurationLoader = dbMaintainConfigurationLoader;
    }


    public DbMaintainConfiguration create() {
        Properties unitilsProperties = configuration.getAllProperties();
        Properties dbMaintainProperties = dbMaintainConfigurationLoader.loadConfiguration(unitilsProperties);
        return new DbMaintainConfiguration(dbMaintainProperties);
    }
}
