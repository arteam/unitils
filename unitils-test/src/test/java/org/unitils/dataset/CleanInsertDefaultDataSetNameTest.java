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
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.annotation.DataSetCleanInsert;
import org.unitils.inject.annotation.TestedObject;

import java.util.Properties;

import static org.unitils.dataset.DataSetLoader.cleanInsertDefaultDataSetFile;

/**
 * Test class for loading of data sets using the clean insert data set strategy.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CleanInsertDefaultDataSetNameTest extends DataSetTestBase {

    @TestedObject
    private DataSetModule dataSetModule = new DataSetModule();

    private TestClass testInstance = new TestClass();
    private NoDataSetFileTestClass noDataSetTestInstance = new NoDataSetFileTestClass();


    @Before
    public void initialize() {
        Properties configuration = Unitils.getInstance().getConfiguration();
        dataSetModule.init(configuration);
        dataSetModule.afterInit();
    }


    @Test
    public void annotatedMethod() throws Exception {
        dataSetModule.loadDataSet(TestClass.class.getDeclaredMethod("annotatedMethod"), testInstance);
        assertValueInTable("test", "col1", "default data set name test");
    }

    @Test
    public void classLevelAnnotation() throws Exception {
        dataSetModule.loadDataSet(TestClass.class.getDeclaredMethod("notAnnotatedMethod"), testInstance);
        assertValueInTable("test", "col1", "default data set name test");
    }

    @Test
    public void dataSetFileNotFound() throws Exception {
        try {
            dataSetModule.loadDataSet(NoDataSetFileTestClass.class.getDeclaredMethod("method"), noDataSetTestInstance);
        } catch (UnitilsException e) {
            assertMessageContains("DataSet file with name org/unitils/dataset/CleanInsertDefaultDataSetNameTest$NoDataSetFileTestClass.xml cannot be found", e);
        }
    }

    @Test
    public void programmatic() throws Exception {
        testInstance.programmatic();
        assertValueInTable("test", "col1", "default data set name test");
    }

    @Test
    public void programmatic_dataSetFileNotFound() throws Exception {
        try {
            noDataSetTestInstance.programmatic_dataSetFileNotFound();
        } catch (UnitilsException e) {
            assertMessageContains("DataSet file with name org/unitils/dataset/CleanInsertDefaultDataSetNameTest$NoDataSetFileTestClass.xml cannot be found", e);
        }
    }


    @DataSetCleanInsert
    private static class TestClass {

        @DataSetCleanInsert
        public void annotatedMethod() {
        }

        public void notAnnotatedMethod() {
        }

        public void programmatic() {
            cleanInsertDefaultDataSetFile(this);
        }


    }

    @DataSetCleanInsert
    private static class NoDataSetFileTestClass {

        public void method() {
        }

        public void programmatic_dataSetFileNotFound() {
            cleanInsertDefaultDataSetFile(this);
        }
    }

}