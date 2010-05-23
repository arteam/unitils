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
import org.unitils.dataset.annotation.DataSetCleanInsert;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.core.CleanInsertDataSetStrategy;
import org.unitils.dataset.loader.impl.Database;

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class DataSetCleanInsertAnnotationHandler implements DataSetAnnotationHandler<DataSetCleanInsert> {

    protected CleanInsertDataSetStrategy cleanInsertDataSetStrategy = new CleanInsertDataSetStrategy();
    protected DataSetModule dataSetModule;


    public void init(Properties configuration, Database database, DataSetModule dataSetModule) {
        cleanInsertDataSetStrategy.init(configuration, database);
        this.dataSetModule = dataSetModule;
    }


    public void handle(DataSetCleanInsert annotation, Class<?> testClass) {
        List<String> fileNames = asList(annotation.value());
        List<String> variables = asList(annotation.variables());

        dataSetModule.performLoadDataSetStrategy(cleanInsertDataSetStrategy, fileNames, variables, testClass);
    }

}
