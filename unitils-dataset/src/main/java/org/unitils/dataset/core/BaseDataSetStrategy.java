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
package org.unitils.dataset.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.dataset.factory.DataSetResolver;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.util.PropertyUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getConfiguredInstanceOf;
import static org.unitils.core.util.ConfigUtils.getInstanceOf;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class BaseDataSetStrategy {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(BaseDataSetStrategy.class);

    public static String DEFAULT_CASE_SENSITIVE_PROPERTY = "dataset.casesensitive.default";
    public static String DEFAULT_LITERAL_TOKEN_PROPERTY = "dataset.literaltoken.default";
    public static String DEFAULT_VARIABLE_TOKEN_PROPERTY = "dataset.variabletoken.default";

    protected Database database;
    protected DataSetLoader dataSetLoader;
    protected DataSetResolver dataSetResolver;
    protected DatabaseAccessor databaseAccessor;
    protected DataSetRowSource dataSetRowSource;

    protected IdentifierNameProcessor identifierNameProcessor;
    protected DataSetRowProcessor dataSetRowProcessor;


    public void init(Properties configuration, Database database) {
        this.database = database;

        this.dataSetResolver = createDataSetResolver(configuration);
        this.databaseAccessor = createDatabaseAccessor(database);
        this.dataSetRowSource = createDataSetRowSource(configuration, database);
        this.identifierNameProcessor = createIdentifierNameProcessor(database);
        this.dataSetRowProcessor = createDataSetRowProcessor(identifierNameProcessor, database);
        this.dataSetLoader = createDataSetLoader(dataSetRowProcessor, databaseAccessor);
    }


    public void perform(List<String> dataSetFileNames, List<String> variables, Class<?> testClass) {
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        loadDataSets(dataSetFiles, variables);
    }

    protected void loadDataSets(List<File> dataSetFiles, List<String> variables) {
        for (File dataSetFile : dataSetFiles) {
            loadDataSet(dataSetFile, variables);
        }
    }


    protected void loadDataSet(File dataSetFile, List<String> variables) {
        logger.info("Loading data sets file: " + dataSetFile);
        try {
            dataSetRowSource.open(dataSetFile);
            dataSetLoader.load(dataSetRowSource, variables);

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set file: " + dataSetFile, e);
        } finally {
            dataSetRowSource.close();
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

    protected abstract DataSetLoader createDataSetLoader(DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor);

    /**
     * @param configuration The unitils configuration, not null
     * @return An initialized data set factory of the given type, not null
     */
    protected DataSetRowSource createDataSetRowSource(Properties configuration, Database database) {
        char defaultLiteralToken = PropertyUtils.getString(DEFAULT_LITERAL_TOKEN_PROPERTY, configuration).charAt(0);
        char defaultVariableToken = PropertyUtils.getString(DEFAULT_VARIABLE_TOKEN_PROPERTY, configuration).charAt(0);
        boolean defaultCaseSensitive = PropertyUtils.getBoolean(DEFAULT_CASE_SENSITIVE_PROPERTY, configuration);
        DataSetSettings dataSetSettings = new DataSetSettings(defaultLiteralToken, defaultVariableToken, defaultCaseSensitive);

        String defaultSchemaName = database.getSchemaName();
        DataSetRowSource dataSetRowSource = getInstanceOf(DataSetRowSource.class, configuration);
        dataSetRowSource.init(defaultSchemaName, dataSetSettings);
        return dataSetRowSource;
    }

    /**
     * @param configuration The unitils configuration, not null
     * @return The data set resolver, as configured in the Unitils configuration, not null
     */
    protected DataSetResolver createDataSetResolver(Properties configuration) {
        return getConfiguredInstanceOf(DataSetResolver.class, configuration);
    }

    protected DatabaseAccessor createDatabaseAccessor(Database database) {
        return new DatabaseAccessor(database);
    }

    protected IdentifierNameProcessor createIdentifierNameProcessor(Database database) {
        // todo refactor initialization
        IdentifierNameProcessor identifierNameProcessor = new IdentifierNameProcessor();
        identifierNameProcessor.init(database);
        return identifierNameProcessor;
    }

    protected DataSetRowProcessor createDataSetRowProcessor(IdentifierNameProcessor identifierNameProcessor, Database database) {
        // todo refactor initialization
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        // todo refactor initialization
        DataSetRowProcessor dataSetRowProcessor = new DataSetRowProcessor();
        dataSetRowProcessor.init(identifierNameProcessor, sqlTypeHandlerRepository, database);
        return dataSetRowProcessor;
    }


}
