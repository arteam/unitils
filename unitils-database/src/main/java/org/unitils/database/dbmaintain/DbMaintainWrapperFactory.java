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

import org.dbmaintain.MainFactory;
import org.unitilsnew.core.Factory;
import org.unitilsnew.core.annotation.Property;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainWrapperFactory implements Factory<DbMaintainWrapper> {

    protected DbMaintainConfiguration dbMaintainConfiguration;
    protected DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager;
    /* Indicates if the DBMaintain should be invoked to update the database */
    protected boolean dbMaintainEnabled;


    public DbMaintainWrapperFactory(DbMaintainConfiguration dbMaintainConfiguration, DbMaintainDatabaseConnectionManager dbMaintainDatabaseConnectionManager, @Property("database.dbMaintain.enabled") boolean dbMaintainEnabled) {
        this.dbMaintainConfiguration = dbMaintainConfiguration;
        this.dbMaintainDatabaseConnectionManager = dbMaintainDatabaseConnectionManager;
        this.dbMaintainEnabled = dbMaintainEnabled;
    }

    public DbMaintainWrapper create() {
        MainFactory mainFactory = createMainFactory();
        return new DbMaintainWrapper(mainFactory, dbMaintainEnabled);

    }

    protected MainFactory createMainFactory() {
        Properties dbMaintainProperties = dbMaintainConfiguration.getProperties();
        return new MainFactory(dbMaintainProperties, dbMaintainDatabaseConnectionManager);
    }
}
