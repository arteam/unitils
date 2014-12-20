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

import org.unitils.core.TestAnnotationListener;
import org.unitils.core.TestInstance;
import org.unitils.core.TestPhase;
import org.unitils.core.reflect.Annotations;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.core.DataSetService;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;

import java.util.List;

import static java.util.Arrays.asList;
import static org.unitils.core.TestPhase.SETUP;

/**
 * @author Tim Ducheyne
 */
public class DataSetTestAnnotationListener extends TestAnnotationListener<DataSet> {

    protected DataSetService dataSetService;


    public DataSetTestAnnotationListener(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }


    @Override
    public TestPhase getTestPhase() {
        return SETUP;
    }

    @Override
    public void beforeTestMethod(TestInstance testInstance, Annotations<DataSet> annotations) {
        DataSet annotation = annotations.getAnnotationWithDefaults();

        String[] fileNamesArray = annotation.value();
        List<String> fileNames = fileNamesArray == null ? null : asList(fileNamesArray);
        Class<?> testClass = testInstance.getClassWrapper().getWrappedClass();
        Class<? extends DataSetFactory> dataSetFactoryClass = annotation.factory();
        if (DataSetFactory.class.equals(dataSetFactoryClass)) {
            dataSetFactoryClass = null;
        }
        Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass = annotation.loadStrategy();
        if (DataSetLoadStrategy.class.equals(dataSetLoadStrategyClass)) {
            dataSetLoadStrategyClass = null;
        }

        dataSetService.loadDataSets(fileNames, testClass, dataSetLoadStrategyClass, dataSetFactoryClass);
    }


}
