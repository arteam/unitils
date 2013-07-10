/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.dbunit.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class DataSetTestAnnotationListenerBeforeTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetTestAnnotationListener dataSetTestAnnotationListener;

    private Mock<DataSetService> dataSetServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<Annotations<DataSet>> annotationsMock;

    private DataSet annotation1;
    private DataSet annotation2;
    private DataSet annotation3;


    @Before
    public void initialize() throws Exception {
        dataSetTestAnnotationListener = new DataSetTestAnnotationListener(dataSetServiceMock.getMock());

        annotation1 = MyClass.class.getMethod("test1").getAnnotation(DataSet.class);
        annotation2 = MyClass.class.getMethod("test2").getAnnotation(DataSet.class);
        annotation3 = MyClass.class.getMethod("test3").getAnnotation(DataSet.class);

        testInstanceMock.returns(MyClass.class).getClassWrapper().getWrappedClass();
    }


    @Test
    public void fileNamesSpecified() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        dataSetTestAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), annotationsMock.getMock());

        dataSetServiceMock.assertInvoked().loadDataSets(asList("test1.xml", "test2.xml"), MyClass.class, null, null);
    }

    @Test
    public void dataSetFactoryAndLoaderSpecified() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();

        dataSetTestAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), annotationsMock.getMock());

        dataSetServiceMock.assertInvoked().loadDataSets(asList("test.xml"), MyClass.class, MyDataSetLoadStrategy.class, MyDataSetFactory.class);
    }

    @Test
    public void noValuesSpecified() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();

        dataSetTestAnnotationListener.beforeTestMethod(testInstanceMock.getMock(), annotationsMock.getMock());

        dataSetServiceMock.assertInvoked().loadDataSets(null, MyClass.class, null, null);
    }


    private static class MyClass {

        @DataSet({"test1.xml", "test2.xml"})
        public void test1() {
        }

        @DataSet(value = "test.xml", factory = MyDataSetFactory.class, loadStrategy = MyDataSetLoadStrategy.class)
        public void test2() {
        }

        @DataSet
        public void test3() {
        }
    }

    private static interface MyDataSetFactory extends DataSetFactory {
    }

    private static interface MyDataSetLoadStrategy extends DataSetLoadStrategy {
    }
}
