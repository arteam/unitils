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
package org.unitils.dataset.assertstrategy;

import org.unitils.dataset.factory.DataSetStrategyFactory;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.unitils.util.CollectionUtils.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertDataSetStrategyHandler {

    protected FileDataSetRowSourceFactory fileDataSetRowSourceFactory;
    protected DataSetResolver dataSetResolver;
    protected DataSetStrategyFactory dataSetStrategyFactory;

    public AssertDataSetStrategyHandler(FileDataSetRowSourceFactory fileDataSetRowSourceFactory, DataSetResolver dataSetResolver, DataSetStrategyFactory dataSetStrategyFactory) {
        this.fileDataSetRowSourceFactory = fileDataSetRowSourceFactory;
        this.dataSetResolver = dataSetResolver;
        this.dataSetStrategyFactory = dataSetStrategyFactory;
    }


    public void assertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean logDatabaseContentOnAssertionError, String... variables) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = dataSetStrategyFactory.createAssertDataSetStrategy();
        performAssertDataSetStrategy(defaultAssertDataSetStrategy, dataSetFileNames, asList(variables), logDatabaseContentOnAssertionError, testInstance.getClass());
    }


    public void performAssertDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean logDatabaseContentOnAssertionError, Class<?> testClass) {
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            DataSetRowSource dataSetRowSource = fileDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            assertDataSetStrategy.perform(dataSetRowSource, variables, logDatabaseContentOnAssertionError);
        }
    }


    protected List<File> resolveDataSets(Class<?> testClass, List<String> dataSetFileNames) {
        List<File> dataSetFiles = new ArrayList<File>();

        for (String dataSetFileName : dataSetFileNames) {
            File dataSetFile = dataSetResolver.resolve(testClass, dataSetFileName);
            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }
}