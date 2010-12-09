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
package org.unitils.dataset;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dataset.annotation.CleanInsertDataSet;
import org.unitils.dataset.annotation.InlineAssertDataSet;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineAssertDataSetAnnotationTest extends DataSetTestBase {

    /* Tested object */
    protected DataSetModule dataSetModule;

    protected TestClass testInstance = new TestClass();
    protected NotAnnotatedTestClass notAnnotatedTestInstance = new NotAnnotatedTestClass();
    protected AnnotationOnClassTestClass annotationOnClassTestInstance = new AnnotationOnClassTestClass();


    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dataSetModule = new DataSetModule();
        dataSetModule.init(configuration);
        dataSetModule.afterInit();
    }


    @Test
    public void successfulAssert() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("successfulAssert");
        dataSetModule.loadDataSet(method, testInstance);

        dataSetModule.assertExpectedDataSet(method, testInstance);
    }

    @Test(expected = AssertionError.class)
    public void failingAssert() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("failingAssert");
        dataSetModule.loadDataSet(method, testInstance);

        dataSetModule.assertExpectedDataSet(method, testInstance);
    }

    @Test
    public void notAnnotated() throws Exception {
        Method method = NotAnnotatedTestClass.class.getDeclaredMethod("notAnnotatedMethod");
        dataSetModule.loadDataSet(method, notAnnotatedTestInstance);

        dataSetModule.assertExpectedDataSet(method, notAnnotatedTestInstance);
    }

    @Test(expected = AssertionError.class)
    public void annotationOnClass() throws Exception {
        Method method = AnnotationOnClassTestClass.class.getDeclaredMethod("notAnnotatedMethod");
        dataSetModule.loadDataSet(method, annotationOnClassTestInstance);

        dataSetModule.assertExpectedDataSet(method, annotationOnClassTestInstance);
    }


    @CleanInsertDataSet("DataSetModuleDataSetTest-simple.xml")
    public class TestClass {

        @InlineAssertDataSet("TEST col1=xxxx")
        public void successfulAssert() {
        }

        @InlineAssertDataSet({"TEST col1=xxxx", "TEST col2=1"})
        public void failingAssert() {
        }

    }

    public class NotAnnotatedTestClass {

        public void notAnnotatedMethod() {
        }
    }

    @CleanInsertDataSet("DataSetModuleDataSetTest-simple.xml")
    @InlineAssertDataSet({"TEST col1=value1", "TEST col2=1"})
    public class AnnotationOnClassTestClass {

        public void notAnnotatedMethod() {
        }
    }

}