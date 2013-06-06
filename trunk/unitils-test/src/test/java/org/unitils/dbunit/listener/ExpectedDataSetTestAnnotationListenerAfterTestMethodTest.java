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
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import java.lang.reflect.Method;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class ExpectedDataSetTestAnnotationListenerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private ExpectedDataSetTestAnnotationListener expectedDataSetTestAnnotationListener;

    private Mock<DataSetService> dataSetServiceMock;

    private Mock<TestInstance> testInstanceMock;
    private Mock<Annotations<ExpectedDataSet>> annotationsMock;

    private Method testMethod1;
    private Method testMethod2;
    private Method testMethod3;

    private ExpectedDataSet annotation1;
    private ExpectedDataSet annotation2;
    private ExpectedDataSet annotation3;


    @Before
    public void initialize() throws Exception {
        expectedDataSetTestAnnotationListener = new ExpectedDataSetTestAnnotationListener(dataSetServiceMock.getMock());

        testMethod1 = MyClass.class.getMethod("test1");
        testMethod2 = MyClass.class.getMethod("test2");
        testMethod3 = MyClass.class.getMethod("test3");
        annotation1 = testMethod1.getAnnotation(ExpectedDataSet.class);
        annotation2 = testMethod2.getAnnotation(ExpectedDataSet.class);
        annotation3 = testMethod3.getAnnotation(ExpectedDataSet.class);

        testInstanceMock.returns(MyClass.class).getClassWrapper().getWrappedClass();
    }


    @Test
    public void fileNamesSpecified() {
        testInstanceMock.returns(testMethod1).getTestMethod();
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        expectedDataSetTestAnnotationListener.afterTestMethod(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        dataSetServiceMock.assertInvoked().assertExpectedDataSets(asList("test1.xml", "test2.xml"), testMethod1, MyClass.class, null);
    }

    @Test
    public void dataSetFactorySpecified() {
        testInstanceMock.returns(testMethod2).getTestMethod();
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();

        expectedDataSetTestAnnotationListener.afterTestMethod(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        dataSetServiceMock.assertInvoked().assertExpectedDataSets(asList("test.xml"), testMethod2, MyClass.class, MyDataSetFactory.class);
    }

    @Test
    public void noValuesSpecified() {
        testInstanceMock.returns(testMethod3).getTestMethod();
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();

        expectedDataSetTestAnnotationListener.afterTestMethod(testInstanceMock.getMock(), annotationsMock.getMock(), null);

        dataSetServiceMock.assertInvoked().assertExpectedDataSets(null, testMethod3, MyClass.class, null);
    }

    @Test
    public void ignoreWhenTestRaisedAnException() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();

        expectedDataSetTestAnnotationListener.afterTestMethod(testInstanceMock.getMock(), annotationsMock.getMock(), new NullPointerException());

        dataSetServiceMock.assertNotInvoked().assertExpectedDataSets(asList("test.xml"), testMethod1, MyClass.class, null);
    }


    private static class MyClass {

        @ExpectedDataSet({"test1.xml", "test2.xml"})
        public void test1() {
        }

        @ExpectedDataSet(value = "test.xml", factory = MyDataSetFactory.class)
        public void test2() {
        }

        @ExpectedDataSet
        public void test3() {
        }
    }

    private static interface MyDataSetFactory extends DataSetFactory {
    }
}
