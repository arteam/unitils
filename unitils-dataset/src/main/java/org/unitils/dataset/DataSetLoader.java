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

import org.unitils.core.Unitils;
import org.unitils.dataset.factory.DataSetStrategyHandlerFactory;
import org.unitils.dataset.loadstrategy.InlineLoadDataSetStrategyHandler;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategyHandler;

import java.util.ArrayList;
import java.util.List;

import static org.unitils.util.CollectionUtils.asList;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetLoader {

    public static void insertDefaultDataSetFile(Object testInstance, String... variables) {
        insertDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void insertDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        insertDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doInsertDataSetFile(testInstance, dataSetFileNames, variables);
    }


    public static void insertDataSet(String... dataSetRows) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doInsertDataSet(dataSetRows);
    }


    public static void cleanInsertDefaultDataSetFile(Object testInstance, String... variables) {
        cleanInsertDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void cleanInsertDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        cleanInsertDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doCleanInsertDataSetFiles(testInstance, dataSetFileNames, variables);
    }


    public static void cleanInsertDataSet(String... dataSetRows) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doCleanInsertDataSet(dataSetRows);
    }


    public static void refreshDefaultDataSetFile(Object testInstance, String... variables) {
        refreshDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void refreshDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        refreshDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doRefreshDataSetFiles(testInstance, dataSetFileNames, variables);
    }


    public static void refreshDataSet(String... dataSetRows) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doRefreshDataSet(dataSetRows);
    }


    public static void updateDefaultDataSetFile(Object testInstance, String... variables) {
        updateDataSetFiles(testInstance, new ArrayList<String>(), variables);
    }

    public static void updateDataSetFile(Object testInstance, String dataSetFileName, String... variables) {
        updateDataSetFiles(testInstance, asList(dataSetFileName), variables);
    }

    public static void updateDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doUpdateDataSetFiles(testInstance, dataSetFileNames, variables);
    }


    public static void updateDataSet(String... dataSetRows) {
        DataSetLoader dataSetLoader = new DataSetLoader();
        dataSetLoader.doUpdateDataSet(dataSetRows);
    }


    private String databaseName;
    private boolean readOnly;


    public DataSetLoader() {
        this(null, false);
    }

    public DataSetLoader(String databaseName, boolean readOnly) {
        this.databaseName = databaseName;
        this.readOnly = readOnly;
    }


    public void doInsertDataSetFile(Object testInstance, List<String> dataSetFileNames, String... variables) {
        getLoadDataSetStrategyHandler(databaseName).insertDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public void doInsertDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler(databaseName).insertDataSet(dataSetRows);
    }

    public void doCleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        getLoadDataSetStrategyHandler(databaseName).cleanInsertDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public void doCleanInsertDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler(databaseName).cleanInsertDataSet(dataSetRows);
    }

    public void doRefreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        getLoadDataSetStrategyHandler(databaseName).refreshDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public void doRefreshDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler(databaseName).refreshDataSet(dataSetRows);
    }

    public void doUpdateDataSetFiles(Object testInstance, List<String> dataSetFileNames, String... variables) {
        getLoadDataSetStrategyHandler(databaseName).updateDataSetFiles(testInstance, dataSetFileNames, readOnly, variables);
    }

    public void doUpdateDataSet(String... dataSetRows) {
        getInlineLoadDataSetStrategyHandler(databaseName).updateDataSet(dataSetRows);
    }


    private LoadDataSetStrategyHandler getLoadDataSetStrategyHandler(String databaseName) {
        return getDataSetStrategyHandlerFactory().createLoadDataSetStrategyHandler(databaseName);
    }

    private InlineLoadDataSetStrategyHandler getInlineLoadDataSetStrategyHandler(String databaseName) {
        return getDataSetStrategyHandlerFactory().createInlineLoadDataSetStrategyHandler(databaseName);
    }


    private DataSetStrategyHandlerFactory getDataSetStrategyHandlerFactory() {
        DataSetModule dataSetModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DataSetModule.class);
        return dataSetModule.getDataSetStrategyHandlerFactory();
    }
}