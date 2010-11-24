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
package org.unitils.dataset.loadstrategy;

import org.unitils.dataset.DataSetStrategyFactory;
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
public class LoadDataSetStrategyHandler {

    protected FileDataSetRowSourceFactory fileDataSetRowSourceFactory;
    protected DataSetResolver dataSetResolver;
    protected DataSetStrategyFactory dataSetStrategyFactory;

    protected List<File> lastLoadedReadOnlyFiles = new ArrayList<File>();


    public LoadDataSetStrategyHandler(FileDataSetRowSourceFactory fileDataSetRowSourceFactory, DataSetResolver dataSetResolver, DataSetStrategyFactory dataSetStrategyFactory) {
        this.fileDataSetRowSourceFactory = fileDataSetRowSourceFactory;
        this.dataSetResolver = dataSetResolver;
        this.dataSetStrategyFactory = dataSetStrategyFactory;
    }


    public void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy insertDataSetStrategy = dataSetStrategyFactory.createInsertDataSetStrategy();
        performLoadDataSetStrategy(insertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = dataSetStrategyFactory.createCleanInsertDataSetStrategy();
        performLoadDataSetStrategy(cleanInsertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy refreshDataSetStrategy = dataSetStrategyFactory.createRefreshDataSetStrategy();
        performLoadDataSetStrategy(refreshDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void updateDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy updateDataSetStrategy = dataSetStrategyFactory.createUpdateDataSetStrategy();
        performLoadDataSetStrategy(updateDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }


    public void performLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean readOnly, Class<?> testClass) {
        if (dataSetFileNames.isEmpty()) {
            // empty means, use default file name, which is the name of the class + extension
            dataSetFileNames.add(getDefaultDataSetFileName(testClass));
        }
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            if (lastLoadedReadOnlyFiles.contains(dataSetFile)) {
                continue;
            }
            DataSetRowSource dataSetRowSource = fileDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            loadDataSetStrategy.perform(dataSetRowSource, variables);
        }

        if (readOnly) {
            lastLoadedReadOnlyFiles.addAll(dataSetFiles);
        } else {
            lastLoadedReadOnlyFiles.clear();
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

    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @return The default filename, not null
     */
    protected String getDefaultDataSetFileName(Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
    }
}