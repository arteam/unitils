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
package org.unitils.database.util;

import org.dbmaintain.config.DbSupportsFactory;
import org.dbmaintain.config.PropertiesDatabaseInfoLoader;
import org.dbmaintain.config.PropertiesDbMaintainConfigurer;
import org.dbmaintain.dbsupport.DatabaseInfo;
import org.dbmaintain.dbsupport.DbSupports;
import org.dbmaintain.dbsupport.impl.DefaultSQLHandler;
import org.dbmaintain.launch.DbMaintain;

import java.util.List;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbMaintainFactory {

    private Properties configuration;


    public DbMaintainFactory(Properties configuration) {
        this.configuration = configuration;
    }


    public DbSupports createDbSupports() {
        PropertiesDatabaseInfoLoader propertiesDatabaseInfoLoader = new PropertiesDatabaseInfoLoader(configuration);
        List<DatabaseInfo> databaseInfos = propertiesDatabaseInfoLoader.getDatabaseInfos();

        DbSupportsFactory dbSupportFactory = new DbSupportsFactory(configuration, new DefaultSQLHandler());
        return dbSupportFactory.createDbSupports(databaseInfos);
    }

    public DbMaintain createDbMaintain(DbSupports dbSupports) {
        PropertiesDbMaintainConfigurer propertiesDbMaintainConfigurer = new PropertiesDbMaintainConfigurer(configuration, dbSupports, dbSupports.getDefaultDbSupport().getSQLHandler());
        return new DbMaintain(propertiesDbMaintainConfigurer);
    }


}
