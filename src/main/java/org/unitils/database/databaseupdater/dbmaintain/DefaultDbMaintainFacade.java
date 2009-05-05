/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.database.databaseupdater.dbmaintain;

import org.dbmaintain.launch.DbMaintain;
import org.dbmaintain.config.PropertiesDbMaintainConfigurer;
import org.dbmaintain.config.DbMaintainConfigurationLoader;
import org.dbmaintain.dbsupport.impl.DefaultSQLHandler;
import org.dbmaintain.dbsupport.DbSupport;

import java.util.Properties;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 24-feb-2009
 */
public class DefaultDbMaintainFacade implements DbMaintainFacade {

    private PropertiesDbMaintainConfigurer dbMaintainConfigurer;

    private DbMaintain dbMaintain;

    private DbSupport defaultDbSupport;

    public void init(Properties configuration) {
        Properties dbMaintainProperties = new DbMaintainConfigurationLoader().loadDefaultConfiguration();
        dbMaintainProperties.putAll(configuration);
        dbMaintainConfigurer = new PropertiesDbMaintainConfigurer(dbMaintainProperties,
                new DefaultSQLHandler());
    }

    protected void initDbMaintain() {
        defaultDbSupport = dbMaintainConfigurer.getDefaultDbSupport();
        dbMaintain = new DbMaintain(dbMaintainConfigurer);
    }

    protected DbMaintain getDbMaintain() {
        if (dbMaintain == null) {
            initDbMaintain();
        }
        return dbMaintain;
    }

    public boolean updateDatabase() {
        return getDbMaintain().updateDatabase();
    }

    public void markDatabaseAsUpToDate() {
        getDbMaintain().markDatabaseAsUpToDate();
    }

    public void clearDatabase() {
        getDbMaintain().clearDatabase();
    }

    public void cleanDatabase() {
        getDbMaintain().cleanDatabase();
    }

    public void disableConstraints() {
        getDbMaintain().disableConstraints();
    }

    public void updateSequences() {
        getDbMaintain().updateSequences();
    }

    public DbSupport getDefaultDbSupport() {
        if (dbMaintain == null) {
            initDbMaintain();
        }
        return defaultDbSupport;
    }
}
