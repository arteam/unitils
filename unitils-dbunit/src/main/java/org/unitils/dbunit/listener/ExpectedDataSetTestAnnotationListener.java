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

import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitilsnew.core.TestAnnotationListener;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class ExpectedDataSetTestAnnotationListener extends TestAnnotationListener<ExpectedDataSet> {

    protected DataSetService dataSetService;


    public ExpectedDataSetTestAnnotationListener(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }


    @Override
    public void afterTestMethod(TestInstance testInstance, Annotations<ExpectedDataSet> annotations, Throwable testThrowable) {
        if (testThrowable != null) {
            // do not do an assert when there is already an exception raised
            return;
        }
        ExpectedDataSet annotation = annotations.getAnnotationWithDefaults();

        String[] fileNamesArray = annotation.value();
        List<String> fileNames = fileNamesArray == null ? null : asList(fileNamesArray);
        Method testMethod = testInstance.getTestMethod();
        Class<?> testClass = testInstance.getClassWrapper().getWrappedClass();
        Class<? extends DataSetFactory> dataSetFactoryClass = annotation.factory();

        dataSetService.assertExpectedDataSets(fileNames, testMethod, testClass, dataSetFactoryClass);
    }
}
