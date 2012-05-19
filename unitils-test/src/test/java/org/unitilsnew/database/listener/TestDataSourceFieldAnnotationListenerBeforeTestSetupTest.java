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

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;
import org.unitilsnew.database.annotations.TestDataSource;
import org.unitilsnew.database.core.DataSourceService;

import javax.sql.DataSource;

/**
 * @author Tim Ducheyne
 */
public class TestDataSourceFieldAnnotationListenerBeforeTestSetupTest extends UnitilsJUnit4 {

    /* Tested object */
    private TestDataSourceFieldAnnotationListener testDataSourceFieldAnnotationListener;

    private Mock<DataSourceService> dataSourceServiceMock;
    @Dummy
    private DataSource dataSource;

    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<TestDataSource>> annotationsMock;


    private TestDataSource annotation1;
    private TestDataSource annotation2;


    @Before
    public void initialize() throws Exception {
        testDataSourceFieldAnnotationListener = new TestDataSourceFieldAnnotationListener(false, dataSourceServiceMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(TestDataSource.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(TestDataSource.class);
    }


    @Test
    public void defaultDatabase() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        dataSourceServiceMock.returns(dataSource).getDataSource("", false);

        testDataSourceFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(dataSource);
    }

    @Test
    public void namedDatabase() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        dataSourceServiceMock.returns(dataSource).getDataSource("myDatabase", false);

        testDataSourceFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(dataSource);
    }

    @Test
    public void wrappedInTransactionalProxy() {
        testDataSourceFieldAnnotationListener = new TestDataSourceFieldAnnotationListener(true, dataSourceServiceMock.getMock());

        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        dataSourceServiceMock.returns(dataSource).getDataSource(null, true);

        testDataSourceFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(dataSource);
    }


    private static class MyClass {

        @TestDataSource
        private DataSource field1;

        @TestDataSource("myDatabase")
        private DataSource field2;

    }
}
