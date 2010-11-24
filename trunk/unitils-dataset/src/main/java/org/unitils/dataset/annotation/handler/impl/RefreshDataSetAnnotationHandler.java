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
package org.unitils.dataset.annotation.handler.impl;

import org.unitils.dataset.DataSetModule;
import org.unitils.dataset.DataSetStrategyHandlerFactory;
import org.unitils.dataset.annotation.RefreshDataSet;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;

import java.lang.reflect.Method;
import java.util.List;

import static org.unitils.util.CollectionUtils.asList;

/**
 * Handles the execution of the {@link org.unitils.dataset.annotation.RefreshDataSet} annotation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class RefreshDataSetAnnotationHandler implements DataSetAnnotationHandler<RefreshDataSet> {


    public void handle(RefreshDataSet annotation, Method testMethod, Object testInstance, DataSetModule dataSetModule) {
        List<String> fileNames = asList(annotation.value());
        String[] variables = annotation.variables();
        boolean readOnly = annotation.readOnly();
        String databaseName = annotation.databaseName();

        DataSetStrategyHandlerFactory dataSetStrategyHandlerFactory = dataSetModule.getDataSetStrategyHandlerFactory();
        LoadDataSetStrategyHandler loadDataSetStrategyHandler = dataSetStrategyHandlerFactory.getLoadDataSetStrategyHandler(databaseName);
        loadDataSetStrategyHandler.refreshDataSetFiles(testInstance, fileNames, readOnly, variables);
    }

}