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
import org.unitils.dataset.annotation.InlineInsertDataSet;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineDataSetInsertAnnotationTest extends DataSetTestBase {

    /* Tested object */
    protected DataSetModule dataSetModule;

    @Before
    public void initialize() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dataSetModule = new DataSetModule();
        dataSetModule.init(configuration);
        dataSetModule.afterInit();
    }


    @Test
    public void loadDataSet() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("annotatedMethod");
        dataSetModule.loadDataSet(method, new TestClass());

        assertValueInTable("TEST", "col1", "method");
    }

    @Test
    public void annotationOnClass() throws Exception {
        Method method = TestClass.class.getDeclaredMethod("notAnnotatedMethod");
        dataSetModule.loadDataSet(method, new TestClass());

        assertValueInTable("TEST", "col1", "class");
    }

    @Test
    public void notAnnotated() throws Exception {
        Method method = NotAnnotatedTestClass.class.getDeclaredMethod("notAnnotatedMethod");
        dataSetModule.loadDataSet(method, new NotAnnotatedTestClass());

        assertValueNotInTable("TEST", "col1", "class");
    }


    @InlineInsertDataSet("TEST col1=class")
    public class TestClass {

        @InlineInsertDataSet("TEST col1=method")
        public void annotatedMethod() {
        }

        public void notAnnotatedMethod() {
        }
    }

    public class NotAnnotatedTestClass {

        public void notAnnotatedMethod() {
        }
    }

}