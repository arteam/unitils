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

package org.unitilsnew.database.listener;

import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.database.annotations.TestDataSource;
import org.unitilsnew.database.core.DataSourceWrapper;
import org.unitilsnew.database.core.DataSourceWrapperManager;
import org.unitilsnew.database.dbmaintain.DbMaintainWrapper;

import javax.sql.DataSource;

import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class TestDataSourceFieldAnnotationListener extends FieldAnnotationListener<TestDataSource> {

    protected boolean wrapDataSourceInTransactionalProxy;
    protected DataSourceWrapperManager dataSourceWrapperManager;
    protected DbMaintainWrapper dbMaintainWrapper;


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }


    public TestDataSourceFieldAnnotationListener(@Property("database.wrapDataSourceInTransactionalProxy") boolean wrapDataSourceInTransactionalProxy, DataSourceWrapperManager dataSourceWrapperManager, DbMaintainWrapper dbMaintainWrapper) {
        this.wrapDataSourceInTransactionalProxy = wrapDataSourceInTransactionalProxy;
        this.dataSourceWrapperManager = dataSourceWrapperManager;
        this.dbMaintainWrapper = dbMaintainWrapper;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<TestDataSource> annotations) {
        TestDataSource annotation = annotations.getAnnotationWithDefaults();
        String databaseName = annotation.value();

        dbMaintainWrapper.updateDatabaseIfNeeded();
        DataSource dataSource = getDataSource(databaseName);
        testField.setValue(dataSource);
    }


    protected DataSource getDataSource(String databaseName) {
        DataSourceWrapper dataSourceWrapper = dataSourceWrapperManager.getDataSourceWrapper(databaseName);
        return dataSourceWrapper.getDataSource(wrapDataSourceInTransactionalProxy);
    }
}
