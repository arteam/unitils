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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.database.Database;
import org.springframework.test.context.TestContext;
import org.unitils.core.Module;
import org.unitils.core.TestExecutionListenerAdapter;
import org.unitils.core.Unitils;
import org.unitils.database.DatabaseModule;
import org.unitils.database.DatabaseUpdateListener;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation;
import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.assertstrategy.AssertDataSetStrategy;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.rowsource.FileDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.InlineDataSetRowSourceFactory;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.dataset.structure.DataSetStructureGenerator;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationAnnotatedWith;
import static org.unitils.util.CollectionUtils.asList;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Module that provides support for loading test data sets into the database.
 * <p/>
 * Loading of data sets can be done by annotating a class or method with the *DataSet annotations. The name
 * of the data set files can be specified explicitly as an argument of the annotation. If no file name is specified, it looks
 * for a file in the same directory as the test class named: 'classname without packagename'.xml.
 * <p/>
 * By annotating a method with the {@link org.unitils.dataset.annotation.AssertDataSet} annotation or by calling the {@link #assertDataSetFiles}
 * method, the contents of the database can be compared with the contents of a data set. The expected data set can be
 * passed as an argument of the annotation. If no file name is specified it looks for a file in the same directory
 * as the test class that has following name: 'class name without packagename'.'test method name'-result.xml.
 * <p/>
 * This module depends on the {@link org.unitils.database.DatabaseModule} for database connection management.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetModule implements Module {

    /**
     * Property key for the xsd target directory
     */
    public static final String PROPKEY_XSD_TARGETDIRNAME = "dataset.xsd.targetDirName";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetModule.class);

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSetModuleFactoryHelper dataSetModuleFactoryHelper;

    protected List<File> lastLoadedReadOnlyFiles = new ArrayList<File>();


    /**
     * Initializes the DataSetModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;
    }

    public void afterInit() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        databaseModule.registerDatabaseUpdateListener(new DataSetXSDsGeneratingDatabaseUpdateListener());
    }


    public DataSetModuleFactoryHelper getDataSetModuleFactoryHelper() {
        if (dataSetModuleFactoryHelper == null) {
            DatabaseMetaData databaseMetaData = createDatabaseMetaData();
            dataSetModuleFactoryHelper = new DataSetModuleFactoryHelper(configuration, databaseMetaData);
        }
        return dataSetModuleFactoryHelper;
    }


    public void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy insertDataSetStrategy = getDataSetModuleFactoryHelper().createInsertDataSetStrategy();
        performLoadDataSetStrategy(insertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void insertDataSet(String... dataSetRows) {
        LoadDataSetStrategy insertDataSetStrategy = getDataSetModuleFactoryHelper().createInsertDataSetStrategy();
        performInlineLoadDataSetStrategy(insertDataSetStrategy, asList(dataSetRows));
    }


    public void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = getDataSetModuleFactoryHelper().createCleanInsertDataSetStrategy();
        performLoadDataSetStrategy(cleanInsertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void cleanInsertDataSet(String... dataSetRows) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = getDataSetModuleFactoryHelper().createCleanInsertDataSetStrategy();
        performInlineLoadDataSetStrategy(cleanInsertDataSetStrategy, asList(dataSetRows));
    }


    public void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy refreshDataSetStrategy = getDataSetModuleFactoryHelper().createRefreshDataSetStrategy();
        performLoadDataSetStrategy(refreshDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void refreshDataSet(String... dataSetRows) {
        LoadDataSetStrategy refreshDataSetStrategy = getDataSetModuleFactoryHelper().createRefreshDataSetStrategy();
        performInlineLoadDataSetStrategy(refreshDataSetStrategy, asList(dataSetRows));
    }


    public void assertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean logDatabaseContentOnAssertionError, String... variables) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = getDataSetModuleFactoryHelper().createAssertDataSetStrategy();
        performAssertDataSetStrategy(defaultAssertDataSetStrategy, dataSetFileNames, asList(variables), logDatabaseContentOnAssertionError, testInstance.getClass());
    }

    public void assertExpectedDataSet(boolean logDatabaseContentOnAssertionError, String... dataSetRows) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = getDataSetModuleFactoryHelper().createAssertDataSetStrategy();
        performInlineAssertDataSetStrategy(defaultAssertDataSetStrategy, asList(dataSetRows), logDatabaseContentOnAssertionError);
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @return The default filename, not null
     */
    public String getDefaultDataSetFileName(Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + ".xml";
    }

    /**
     * Gets the name of the expected dataset file. The default name of this file is constructed as
     * follows: 'classname without packagename'.'testname'-result.xml.
     *
     * @param testMethod The current test method, not null
     * @param testClass  The test class, not null
     * @return The expected dataset filename, not null
     */
    public String getDefaultExpectedDataSetFileName(Method testMethod, Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + testMethod.getName() + "-result.xml";
    }


    public void performLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean readOnly, Class<?> testClass) {
        if (dataSetFileNames.isEmpty()) {
            // empty means, use default file name, which is the name of the class + extension
            dataSetFileNames.add(getDefaultDataSetFileName(testClass));
        }
        FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getDataSetModuleFactoryHelper().createFileDataSetRowSourceFactory();

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

    public void performInlineLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetRows) {
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getDataSetModuleFactoryHelper().createInlineDataSetRowSourceFactory();
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        loadDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>());
    }


    public void performAssertDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean logDatabaseContentOnAssertionError, Class<?> testClass) {
        FileDataSetRowSourceFactory fileDataSetRowSourceFactory = getDataSetModuleFactoryHelper().createFileDataSetRowSourceFactory();

        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            DataSetRowSource dataSetRowSource = fileDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            assertDataSetStrategy.perform(dataSetRowSource, variables, logDatabaseContentOnAssertionError);
        }
    }

    public void performInlineAssertDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetRows, boolean logDatabaseContentOnAssertionError) {
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = getDataSetModuleFactoryHelper().createInlineDataSetRowSourceFactory();
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        assertDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>(), logDatabaseContentOnAssertionError);
    }

    public void generateDataSetXSDs() {
        String targetDirectoryName = configuration.getProperty(PROPKEY_XSD_TARGETDIRNAME);
        if (isBlank(targetDirectoryName)) {
            logger.info("No target XSD path was defined (" + PROPKEY_XSD_TARGETDIRNAME + ") in properties. Skipping data set XSD generation.");
            return;
        }
        generateDataSetXSDs(new File(targetDirectoryName));
    }

    public void generateDataSetXSDs(File targetDirectory) {
        DataSetStructureGenerator dataSetStructureGenerator = getDataSetModuleFactoryHelper().createDataSetStructureGenerator();
        dataSetStructureGenerator.generateDataSetStructure(targetDirectory);
        dataSetStructureGenerator.generateDataSetTemplateXmlFile(targetDirectory);
    }


    protected List<File> resolveDataSets(Class<?> testClass, List<String> dataSetFileNames) {
        List<File> dataSetFiles = new ArrayList<File>();

        DataSetResolver dataSetResolver = getDataSetModuleFactoryHelper().createDataSetResolver();
        for (String dataSetFileName : dataSetFileNames) {
            File dataSetFile = dataSetResolver.resolve(testClass, dataSetFileName);
            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }


    @SuppressWarnings({"unchecked"})
    protected void loadDataSet(Method testMethod, Object testObject) {
        Annotation dataSetAnnotation = getMethodOrClassLevelAnnotationAnnotatedWith(MarkerForLoadDataSetAnnotation.class, testMethod, testObject.getClass());
        if (dataSetAnnotation == null) {
            return;
        }

        MarkerForLoadDataSetAnnotation annotation = dataSetAnnotation.annotationType().getAnnotation(MarkerForLoadDataSetAnnotation.class);
        Class<? extends DataSetAnnotationHandler> dataSetAnnotationHandlerClass = annotation.value();
        DataSetAnnotationHandler dataSetAnnotationHandler = createInstanceOfType(dataSetAnnotationHandlerClass, false);

        dataSetAnnotationHandler.handle(dataSetAnnotation, testMethod, testObject, this);
    }

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     */
    @SuppressWarnings({"unchecked"})
    protected void assertExpectedDataSet(Method testMethod, Object testObject) {
        Annotation dataSetAnnotation = getMethodOrClassLevelAnnotationAnnotatedWith(MarkerForAssertDataSetAnnotation.class, testMethod, testObject.getClass());
        if (dataSetAnnotation == null) {
            return;
        }

        MarkerForAssertDataSetAnnotation annotation = dataSetAnnotation.annotationType().getAnnotation(MarkerForAssertDataSetAnnotation.class);
        Class<? extends DataSetAnnotationHandler> dataSetAnnotationHandlerClass = annotation.value();
        DataSetAnnotationHandler dataSetAnnotationHandler = createInstanceOfType(dataSetAnnotationHandlerClass, false);

        dataSetAnnotationHandler.handle(dataSetAnnotation, testMethod, testObject, this);
    }


    protected DatabaseMetaData createDatabaseMetaData() {
        DatabaseModule databaseModule = Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
        Database defaultDatabase = databaseModule.getDatabases().getDefaultDatabase();

        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        return new DatabaseMetaData(defaultDatabase, sqlTypeHandlerRepository);
    }


    /**
     * @return The TestListener object that implements Unitils' data set support
     */
    public TestExecutionListenerAdapter getTestListener() {
        return new DataSetListener();
    }


    /**
     * Test listener that is called while the test framework is running tests
     */
    protected class DataSetListener extends TestExecutionListenerAdapter {

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod, TestContext testContext) throws Exception {
            loadDataSet(testMethod, testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable, TestContext testContext) throws Exception {
            if (testThrowable != null) {
                return;
            }
            assertExpectedDataSet(testMethod, testObject);
        }
    }


    protected class DataSetXSDsGeneratingDatabaseUpdateListener implements DatabaseUpdateListener {

        public void databaseWasUpdated() {
            generateDataSetXSDs();
        }
    }

}