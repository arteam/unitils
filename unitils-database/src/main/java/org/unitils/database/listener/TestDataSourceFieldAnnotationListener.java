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

package org.unitils.database.listener;

import org.unitils.database.annotation.TestDataSource;
import org.unitils.database.core.DataSourceService;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.reflect.Annotations;

import javax.sql.DataSource;

import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class TestDataSourceFieldAnnotationListener extends FieldAnnotationListener<TestDataSource> {

    protected boolean wrapDataSourceInTransactionalProxy;
    protected DataSourceService dataSourceService;


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }


    public TestDataSourceFieldAnnotationListener(@Property("database.wrapDataSourceInTransactionalProxy") boolean wrapDataSourceInTransactionalProxy, DataSourceService dataSourceService) {
        this.wrapDataSourceInTransactionalProxy = wrapDataSourceInTransactionalProxy;
        this.dataSourceService = dataSourceService;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<TestDataSource> annotations) {
        TestDataSource annotation = annotations.getAnnotationWithDefaults();
        String databaseName = annotation.value();

        DataSource dataSource = dataSourceService.getDataSource(databaseName, wrapDataSourceInTransactionalProxy);
        testField.setValue(dataSource);
    }
}
