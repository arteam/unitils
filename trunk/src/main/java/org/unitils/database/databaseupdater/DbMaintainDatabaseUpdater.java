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
package org.unitils.database.databaseupdater;

import org.dbmaintain.launch.DbMaintain;
import org.dbmaintain.config.PropertiesDbMaintainConfigurer;
import org.dbmaintain.dbsupport.SQLHandler;
import org.dbmaintain.dbsupport.impl.DefaultSQLHandler;
import org.unitils.database.databaseupdater.dbmaintain.DbMaintainFacade;
import org.unitils.database.databaseupdater.dbmaintain.DefaultDbMaintainFacade;
import org.unitils.core.util.ConfigUtils;

import java.util.Properties;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @since 11-feb-2009
 */
public class DbMaintainDatabaseUpdater implements DatabaseUpdater {

    private DbMaintainFacade dbMaintainFacade;

    public DbMaintainDatabaseUpdater() {}

    public void init(Properties configuration) {
        dbMaintainFacade = new DefaultDbMaintainFacade();
        dbMaintainFacade.init(configuration);
    }

    public boolean updateDatabase() {
        return dbMaintainFacade.updateDatabase();
    }

    protected SQLHandler getSqlHandler() {
        return new DefaultSQLHandler();
    }
}
