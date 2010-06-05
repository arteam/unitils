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

import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.core.util.ConfigUtils;
import org.unitils.database.DatabaseModule;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation;
import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.assertstrategy.AssertDataSetStrategy;
import org.unitils.dataset.assertstrategy.impl.DefaultAssertDataSetStrategy;
import org.unitils.dataset.database.DatabaseMetaData;
import org.unitils.dataset.loadstrategy.LoadDataSetStrategy;
import org.unitils.dataset.loadstrategy.impl.CleanInsertDataSetStrategy;
import org.unitils.dataset.loadstrategy.impl.InsertDataSetStrategy;
import org.unitils.dataset.loadstrategy.impl.RefreshDataSetStrategy;
import org.unitils.dataset.resolver.DataSetResolver;
import org.unitils.dataset.rowsource.DataSetRowSource;
import org.unitils.dataset.rowsource.impl.InlineDataSetRowSourceFactory;
import org.unitils.dataset.rowsource.impl.XmlDataSetRowSourceFactory;
import org.unitils.dataset.sqltypehandler.SqlTypeHandlerRepository;

import javax.sql.DataSource;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    /* The unitils configuration */
    protected Properties configuration;

    protected DatabaseMetaData databaseMetaData;
    protected DataSetResolver dataSetResolver;
    protected XmlDataSetRowSourceFactory xmlDataSetRowSourceFactory;
    protected InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory;

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
        databaseMetaData = createDatabaseMetaData(configuration);
        dataSetResolver = createDataSetResolver(configuration);
        xmlDataSetRowSourceFactory = createXmlDataSetRowSourceFactory(configuration, databaseMetaData);
        inlineDataSetRowSourceFactory = createInlineDataSetRowSourceFactory(configuration, databaseMetaData);
    }


    /* INSERT OPERATIONS */

    public void insertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy insertDataSetStrategy = new InsertDataSetStrategy();
        insertDataSetStrategy.init(configuration, databaseMetaData);
        performLoadDataSetStrategy(insertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void insertDataSet(String... dataSetRows) {
        LoadDataSetStrategy insertDataSetStrategy = new InsertDataSetStrategy();
        insertDataSetStrategy.init(configuration, databaseMetaData);
        performInlineLoadDataSetStrategy(insertDataSetStrategy, asList(dataSetRows));
    }


    /* CLEAN INSERT OPERATIONS */

    public void cleanInsertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = new CleanInsertDataSetStrategy();
        cleanInsertDataSetStrategy.init(configuration, databaseMetaData);
        performLoadDataSetStrategy(cleanInsertDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void cleanInsertDataSet(String... dataSetRows) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = new CleanInsertDataSetStrategy();
        cleanInsertDataSetStrategy.init(configuration, databaseMetaData);
        performInlineLoadDataSetStrategy(cleanInsertDataSetStrategy, asList(dataSetRows));
    }


    /* REFRESH OPERATIONS */

    public void refreshDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean readOnly, String... variables) {
        LoadDataSetStrategy refreshDataSetStrategy = new RefreshDataSetStrategy();
        refreshDataSetStrategy.init(configuration, databaseMetaData);
        performLoadDataSetStrategy(refreshDataSetStrategy, dataSetFileNames, asList(variables), readOnly, testInstance.getClass());
    }

    public void refreshDataSet(String... dataSetRows) {
        LoadDataSetStrategy refreshDataSetStrategy = new RefreshDataSetStrategy();
        refreshDataSetStrategy.init(configuration, databaseMetaData);
        performInlineLoadDataSetStrategy(refreshDataSetStrategy, asList(dataSetRows));
    }


    /* ASSERT OPERATIONS */

    public void assertDataSetFiles(Object testInstance, List<String> dataSetFileNames, boolean logDatabaseContentOnAssertionError, String... variables) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = new DefaultAssertDataSetStrategy();
        defaultAssertDataSetStrategy.init(configuration, databaseMetaData);
        performAssertDataSetStrategy(defaultAssertDataSetStrategy, dataSetFileNames, asList(variables), logDatabaseContentOnAssertionError, testInstance.getClass());
    }

    public void assertExpectedDataSet(boolean logDatabaseContentOnAssertionError, String... dataSetRows) {
        AssertDataSetStrategy defaultAssertDataSetStrategy = new DefaultAssertDataSetStrategy();
        defaultAssertDataSetStrategy.init(configuration, databaseMetaData);
        performInlineExpectedDataSetStrategy(defaultAssertDataSetStrategy, asList(dataSetRows), logDatabaseContentOnAssertionError);
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
     * @param testClass The test class, not null
     * @return The expected dataset filename, not null
     */
    public String getDefaultExpectedDataSetFileName(Method testMethod, Class<?> testClass) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + testMethod.getName() + "-result.xml";
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


    protected void performLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean readOnly, Class<?> testClass) {
        if (dataSetFileNames.isEmpty()) {
            // empty means, use default file name, which is the name of the class + extension
            dataSetFileNames.add(getDefaultDataSetFileName(testClass));
        }

        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            if (lastLoadedReadOnlyFiles.contains(dataSetFile)) {
                continue;
            }
            DataSetRowSource dataSetRowSource = xmlDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            loadDataSetStrategy.perform(dataSetRowSource, variables);
        }

        if (readOnly) {
            lastLoadedReadOnlyFiles.addAll(dataSetFiles);
        } else {
            lastLoadedReadOnlyFiles.clear();
        }
    }

    protected void performInlineLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetRows) {
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        loadDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>());
    }


    protected void performAssertDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean logDatabaseContentOnAssertionError, Class<?> testClass) {
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            DataSetRowSource dataSetRowSource = xmlDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            assertDataSetStrategy.perform(dataSetRowSource, variables, logDatabaseContentOnAssertionError);
        }
    }

    protected void performInlineExpectedDataSetStrategy(AssertDataSetStrategy assertDataSetStrategy, List<String> dataSetRows, boolean logDatabaseContentOnAssertionError) {
        DataSetRowSource dataSetRowSource = inlineDataSetRowSourceFactory.createDataSetRowSource(dataSetRows);
        assertDataSetStrategy.perform(dataSetRowSource, new ArrayList<String>(), logDatabaseContentOnAssertionError);
    }


    protected List<File> resolveDataSets(Class<?> testClass, List<String> dataSetFileNames) {
        List<File> dataSetFiles = new ArrayList<File>();

        for (String dataSetFileName : dataSetFileNames) {
            File dataSetFile = dataSetResolver.resolve(testClass, dataSetFileName);
            dataSetFiles.add(dataSetFile);
        }
        return dataSetFiles;
    }


    /* FACTORY METHODS */

    protected DatabaseMetaData createDatabaseMetaData(Properties configuration) {
        DbSupport defaultDbSupport = getDefaultDbSupport(configuration);
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        return new DatabaseMetaData(defaultDbSupport, sqlTypeHandlerRepository);
    }

    protected DataSetResolver createDataSetResolver(Properties configuration) {
        DataSetResolver dataSetResolver = ConfigUtils.getInstanceOf(DataSetResolver.class, configuration);
        dataSetResolver.init(configuration);
        return dataSetResolver;
    }

    protected XmlDataSetRowSourceFactory createXmlDataSetRowSourceFactory(Properties configuration, DatabaseMetaData databaseMetaData) {
        XmlDataSetRowSourceFactory xmlDataSetRowSourceFactory = new XmlDataSetRowSourceFactory();
        xmlDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return xmlDataSetRowSourceFactory;
    }

    protected InlineDataSetRowSourceFactory createInlineDataSetRowSourceFactory(Properties configuration, DatabaseMetaData databaseMetaData) {
        InlineDataSetRowSourceFactory inlineDataSetRowSourceFactory = new InlineDataSetRowSourceFactory();
        inlineDataSetRowSourceFactory.init(configuration, databaseMetaData.getSchemaName());
        return inlineDataSetRowSourceFactory;
    }


    /**
     * @return The default DbSupport (the one that connects to the default database schema)
     */
    protected DbSupport getDefaultDbSupport(Properties configuration) {
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