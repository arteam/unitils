/*
 * Copyright 2008,  Unitils.org
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
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.dataset.annotation.ExpectedDataSet;
import org.unitils.dataset.comparison.DataSetComparator;
import org.unitils.dataset.comparison.DatabaseContentLogger;
import org.unitils.dataset.comparison.ExpectedDataSetAssert;
import org.unitils.dataset.core.DataSet;
import org.unitils.dataset.core.DataSetRowProcessor;
import org.unitils.dataset.core.DataSetSettings;
import org.unitils.dataset.factory.DataSetResolver;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.loader.DataSetLoader;
import org.unitils.dataset.loader.impl.Database;
import org.unitils.dataset.loader.impl.IdentifierNameProcessor;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;
import org.unitils.dataset.util.DataSetAnnotationUtil;
import org.unitils.dataset.util.DatabaseAccessor;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;
import org.unitils.util.PropertyUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getConfiguredInstanceOf;
import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Module that provides support for loading test data sets into the database.
 * <p/>
 * Loading of data sets can be done by annotating a class or method with the {@link org.unitils.dataset.annotation.DataSet} annotation. The name
 * of the data set files can be specified explicitly as an argument of the annotation. If no file name is specified, it looks
 * for a file in the same directory as the test class named: 'classname without packagename'.xml.
 * <p/>
 * By annotating a method with the {@link ExpectedDataSet} annotation or by calling the {@link #assertExpectedDataSet}
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

    public static String DEFAULT_CASE_SENSITIVE_PROPERTY = "dataset.casesensitive.default";
    public static String DEFAULT_LITERAL_TOKEN_PROPERTY = "dataset.literaltoken.default";
    public static String DEFAULT_VARIABLE_TOKEN_PROPERTY = "dataset.variabletoken.default";

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetModule.class);

    /* Map holding the default configuration of the dbunit module annotations */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    /* The unitils configuration */
    protected Properties configuration;

    /* A utility class for getting the values from the annotations. */
    protected DataSetAnnotationUtil dataSetAnnotationUtil;


    /**
     * Initializes the DbUnitModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    @SuppressWarnings("unchecked")
    public void init(Properties configuration) {
        this.configuration = configuration;
        this.dataSetAnnotationUtil = new DataSetAnnotationUtil(configuration);
        this.defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DataSetModule.class, configuration, org.unitils.dataset.annotation.DataSet.class, ExpectedDataSet.class);
    }


    /**
     * No after initialization needed for this module
     */
    public void afterInit() {
    }

    public void loadDataSet(List<String> dataSetFileNames, List<String> variables, Class<?> testClass, Class<? extends DataSetLoader> dataSetLoaderClass) {
        Class<? extends DataSetRowSource> dataSetRowSourceClass = dataSetAnnotationUtil.getDefaultDataSetRowSourceClass();
        loadDataSet(dataSetFileNames, variables, testClass, dataSetRowSourceClass, dataSetLoaderClass);
    }

    public void assertExpectedDataSet(List<String> dataSetFileNames, List<String> variables, Class<?> testClass, boolean logDatabaseContentOnAssertionError) {
//        Class<? extends DataSetRowSource> dataSetRowSourceClass = dataSetAnnotationUtil.getDefaultDataSetRowSourceClass();
//        assertExpectedDataSet(dataSetFileNames, variables, testClass, dataSetRowSourceClass, logDatabaseContentOnAssertionError);
    }

    protected void loadDataSet(Method testMethod, Object testObject) {
//        try {
//            Class<?> testClass = testObject.getClass();
//            Class<? extends DataSetFactory> dataSetFactoryClass = dataSetAnnotationUtil.getDataSetFactoryClass(testClass, testMethod);
//            DataSetFactory dataSetFactory = createDataSetRowSource(dataSetFactoryClass);
//            List<String> dataSetFileNames = dataSetAnnotationUtil.getDataSetFileNames(testClass, testMethod, dataSetFactory.getDataSetFileExtension());
//            List<String> variables = dataSetAnnotationUtil.getDataSetVariables(testClass, testMethod);
//            Class<? extends DataSetLoader> dataSetLoaderClass = dataSetAnnotationUtil.getDataSetLoaderClass(testClass, testMethod);
//
//            loadDataSet(dataSetFileNames, variables, testClass, dataSetFactory, dataSetLoaderClass);
//
//        } catch (Exception e) {
//            throw new UnitilsException("Error inserting data set for method " + testMethod, e);
//        }
    }

    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     */
    protected void assertExpectedDataSet(Method testMethod, Object testObject) {
//        try {
//            Class<?> testClass = testObject.getClass();
//            Class<? extends DataSetFactory> dataSetFactoryClass = dataSetAnnotationUtil.getDataSetFactoryClass(testClass, testMethod);
//            DataSetFactory dataSetFactory = createDataSetRowSource(dataSetFactoryClass);
//            List<String> dataSetFileNames = dataSetAnnotationUtil.getExpectedDataSetFileNames(testClass, testMethod, dataSetFactory.getDataSetFileExtension());
//            List<String> variables = dataSetAnnotationUtil.getExpectedDataSetVariables(testClass, testMethod);
//            boolean logDatabaseContentOnAssertionError = dataSetAnnotationUtil.getLogDatabaseContentOnAssertionError(testClass, testMethod);
//
//            assertExpectedDataSet(dataSetFileNames, variables, testClass, dataSetFactory, logDatabaseContentOnAssertionError);
//
//        } catch (Exception e) {
//            throw new UnitilsException("Error comparing data set for method " + testMethod, e);
//        }
    }

    protected void loadDataSet(List<String> dataSetFileNames, List<String> variables, Class<?> testClass, Class<? extends DataSetRowSource> dataSetRowSourceClass, Class<? extends DataSetLoader> dataSetLoaderClass) {
        Database database = createDatabase();
        IdentifierNameProcessor identifierNameProcessor = createIdentifierNameProcessor(database);
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        DataSetRowProcessor dataSetRowProcessor = createDataSetRowProcessor(identifierNameProcessor, sqlTypeHandlerRepository, database);
        DatabaseAccessor databaseAccessor = createDatabaseAccessor(database);

        DataSetLoader dataSetLoader = createDataSetLoader(dataSetLoaderClass, dataSetRowProcessor, databaseAccessor);

        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            loadDataSet(dataSetFile, variables, dataSetRowSourceClass, dataSetLoader, database);
        }

    }

    protected IdentifierNameProcessor createIdentifierNameProcessor(Database database) {
        IdentifierNameProcessor identifierNameProcessor = new IdentifierNameProcessor();
        identifierNameProcessor.init(database);
        return identifierNameProcessor;
    }

    protected DatabaseAccessor createDatabaseAccessor(Database database) {
        return new DatabaseAccessor(database);
    }

    protected DataSetRowProcessor createDataSetRowProcessor(IdentifierNameProcessor identifierNameProcessor, SqlTypeHandlerRepository sqlTypeHandlerRepository, Database database){
        DataSetRowProcessor dataSetRowProcessor = new DataSetRowProcessor();
        dataSetRowProcessor.init(identifierNameProcessor, sqlTypeHandlerRepository, database);
        return dataSetRowProcessor;
    }

    protected void assertExpectedDataSet(List<String> dataSetFileNames, List<String> variables, Class<?> testClass, DataSetRowSource dataSetRowSource, boolean logDatabaseContentOnAssertionError) {
//        Database database = createDatabase();
//        DatabaseAccessor databaseAccessor = createDatabaseAccessor(database);
//
//        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
//        for (File dataSetFile : dataSetFiles) {
//            assertExpectedDataSet(dataSetFile, variables, dataSetRowSource, logDatabaseContentOnAssertionError, database, databaseAccessor);
//        }
    }


    protected void loadDataSet(File dataSetFile, List<String> variables, Class<? extends DataSetRowSource> dataSetRowSourceClass, DataSetLoader dataSetLoader, Database database) {
        logger.info("Loading data sets file: " + dataSetFile);
        InputStream dataSetInputStream = null;
        try {
            dataSetInputStream = new FileInputStream(dataSetFile);
            String defaultSchemaName = database.getSchemaName();
            DataSetRowSource dataSetRowSource = createDataSetRowSource(dataSetInputStream, defaultSchemaName, dataSetRowSourceClass);
            dataSetLoader.load(dataSetRowSource, variables);

        } catch (Exception e) {
            throw new UnitilsException("Unable to load data set file: " + dataSetFile, e);
        } finally {
            closeQuietly(dataSetInputStream);
        }
    }

    protected void assertExpectedDataSet(File dataSetFile, List<String> variables, Class<? extends DataSetRowSource> dataSetRowSourceClass, boolean logDatabaseContentOnAssertionError, Database database, DatabaseAccessor databaseAccessor) {
//        DataSetRowSource dataSetRowSource = createDataSetRowSource(dataSetRowSourceClass);
//        DataSet dataSet = dataSetRowSource.createDataSet(dataSetFile, database.getSchemaName());
//        if (dataSet == null) {
//            // no data set specified
//            return;
//        }
//
//        logger.info("Comparing data sets file: " + dataSetFile);
//        DatabaseContentRetriever databaseContentLogger = null;
//        if (logDatabaseContentOnAssertionError) {
//            databaseContentLogger = createDatabaseContentLogger(database, databaseAccessor);
//        }
//        DataSetComparator dataSetComparator = createDataSetComparator(dataSet, database, databaseAccessor);
//        ExpectedDataSetAssert expectedDataSetAssert = createExpectedDataSetAssert(dataSetComparator, databaseContentLogger);
//        expectedDataSetAssert.assertEqual(dataSet, variables);
    }


    protected List<File> resolveDataSets(Class<?> testClass, List<String> dataSetFileNames) {
        List<File> dataSetFiles = new ArrayList<File>();

        DataSetResolver dataSetResolver = createDataSetResolver();
        for (String dataSetFileName : dataSetFileNames) {
            File dataSetFile = dataSetResolver.resolve(testClass, dataSetFileName);
            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }


    /* FACTORY METHODS */

    /**
     * @param dataSetInputStream    The input stream that contains the data set, not null
     * @param defaultSchemaName     The schema name to use when none is specified, not null
     * @param dataSetRowSourceClass The type, not null
     * @return An initialized data set factory of the given type, not null
     */
    protected DataSetRowSource createDataSetRowSource(InputStream dataSetInputStream, String defaultSchemaName, Class<? extends DataSetRowSource> dataSetRowSourceClass) {
        char defaultLiteralToken = PropertyUtils.getString(DEFAULT_LITERAL_TOKEN_PROPERTY, configuration).charAt(0);
        char defaultVariableToken = PropertyUtils.getString(DEFAULT_VARIABLE_TOKEN_PROPERTY, configuration).charAt(0);
        boolean defaultCaseSensitive = PropertyUtils.getBoolean(DEFAULT_CASE_SENSITIVE_PROPERTY, configuration);
        DataSetSettings dataSetSettings = new DataSetSettings(defaultLiteralToken, defaultVariableToken, defaultCaseSensitive);

        DataSetRowSource dataSetRowSource = createInstanceOfType(dataSetRowSourceClass, false);
        dataSetRowSource.init(defaultSchemaName, dataSetSettings);
        return dataSetRowSource;
    }

    /**
     * @param dataSetLoaderClass The type, not null
     * @param dataSetRowProcessor
     * @param databaseAccessor
     * @return An initialized data set loader of the given type, not null
     */
    protected DataSetLoader createDataSetLoader(Class<? extends DataSetLoader> dataSetLoaderClass, DataSetRowProcessor dataSetRowProcessor, DatabaseAccessor databaseAccessor) {
        DataSetLoader dataSetLoader = createInstanceOfType(dataSetLoaderClass, false);
        dataSetLoader.init(dataSetRowProcessor, databaseAccessor);
        return dataSetLoader;
    }


    /**
     * @param dataSet  The data set that will be compared, not null
     * @param database The access to the database, not null
     * @return An initialized data set comparator, as configured in the Unitils configuration, not null
     */
    protected DataSetComparator createDataSetComparator(DataSet dataSet, Database database, DatabaseAccessor databaseAccessor) {
        //DataSetComparator dataSetComparator = getInstanceOf(DataSetComparator.class, configuration);
        //dataSetComparator.init(database, databaseAccessor);
        // todo implement
        return null;//dataSetComparator;
    }

    protected Database createDatabase() {
        DbSupport defaultDbSupport = getDefaultDbSupport();
        Database database = new Database();
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        database.init(defaultDbSupport, sqlTypeHandlerRepository);
        return database;
    }

    /**
     * @param database The access to the database, not null
     * @return An initialized database logger, as configured in the Unitils configuration, not null
     */
    protected Object createDatabaseContentLogger(Database database, DatabaseAccessor databaseAccessor) {
//        DatabaseContentRetriever databaseContentLogger = getInstanceOf(DatabaseContentRetriever.class, configuration);
//        databaseContentLogger.init(database, databaseAccessor);
//        return databaseContentLogger;
        return null;
    }

    protected ExpectedDataSetAssert createExpectedDataSetAssert(DataSetComparator dataSetComparator, DatabaseContentLogger databaseContentLogger) {
        ExpectedDataSetAssert expectedDataSetAssert = getInstanceOf(ExpectedDataSetAssert.class, configuration);
        expectedDataSetAssert.init(dataSetComparator, databaseContentLogger);
        return expectedDataSetAssert;
    }

    /**
     * @return The data set resolver, as configured in the Unitils configuration, not null
     */
    protected DataSetResolver createDataSetResolver() {
        return getConfiguredInstanceOf(DataSetResolver.class, configuration);
    }

    /**
     * @return The default DbSupport (the one that connects to the default database schema)
     */
    protected DbSupport getDefaultDbSupport() {
        DataSource dataSource = getDatabaseModule().getDataSourceAndActivateTransactionIfNeeded();
        SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);
        return DbSupportFactory.getDefaultDbSupport(configuration, sqlHandler);
    }


    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    /**
     * @return The TestListener object that implements Unitils' data set support
     */
    public TestListener getTestListener() {
        return new DataSetListener();
    }


    /**
     * Test listener that is called while the test framework is running tests
     */
    protected class DataSetListener extends TestListener {

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            loadDataSet(testMethod, testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
            if (throwable == null) {
                assertExpectedDataSet(testMethod, testObject);
            }
        }
    }

}