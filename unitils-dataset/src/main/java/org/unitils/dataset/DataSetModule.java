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
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import org.unitils.core.dbsupport.DefaultSQLHandler;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.dataset.annotation.DataSetAnnotation;
import org.unitils.dataset.annotation.ExpectedDataSet;
import org.unitils.dataset.annotation.ExpectedDataSetAnnotation;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.comparison.ExpectedDataSetStrategy;
import org.unitils.dataset.comparison.impl.DefaultExpectedDataSetStrategy;
import org.unitils.dataset.core.CleanInsertDataSetStrategy;
import org.unitils.dataset.core.InsertDataSetStrategy;
import org.unitils.dataset.core.LoadDataSetStrategy;
import org.unitils.dataset.core.RefreshDataSetStrategy;
import org.unitils.dataset.factory.DataSetResolver;
import org.unitils.dataset.factory.DataSetRowSource;
import org.unitils.dataset.factory.impl.DefaultDataSetResolver;
import org.unitils.dataset.factory.impl.XmlDataSetRowSourceFactory;
import org.unitils.dataset.loader.impl.Database;
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

    /* The unitils configuration */
    protected Properties configuration;

    protected Database database;
    protected XmlDataSetRowSourceFactory xmlDataSetRowSourceFactory = new XmlDataSetRowSourceFactory();
    protected DataSetResolver dataSetResolver = new DefaultDataSetResolver();


    /**
     * Initializes the DataSetModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;
    }


    public void afterInit() {
        database = createDatabase();
        xmlDataSetRowSourceFactory.init(configuration, database.getSchemaName());
        dataSetResolver.init(configuration);
    }


    public void dataSetInsert(Object testInstance, List<String> dataSetFileNames, String... variables) {
        LoadDataSetStrategy insertDataSetStrategy = new InsertDataSetStrategy();
        insertDataSetStrategy.init(configuration, createDatabase());
        performLoadDataSetStrategy(insertDataSetStrategy, dataSetFileNames, asList(variables), testInstance.getClass());
    }

    public void dataSetCleanInsert(Object testInstance, List<String> dataSetFileNames, String... variables) {
        LoadDataSetStrategy cleanInsertDataSetStrategy = new CleanInsertDataSetStrategy();
        cleanInsertDataSetStrategy.init(configuration, createDatabase());
        performLoadDataSetStrategy(cleanInsertDataSetStrategy, dataSetFileNames, asList(variables), testInstance.getClass());
    }

    public void dataSetRefresh(Object testInstance, List<String> dataSetFileNames, String... variables) {
        LoadDataSetStrategy refreshDataSetStrategy = new RefreshDataSetStrategy();
        refreshDataSetStrategy.init(configuration, createDatabase());
        performLoadDataSetStrategy(refreshDataSetStrategy, dataSetFileNames, asList(variables), testInstance.getClass());
    }

    public void assertExpectedDataSet(Object testInstance, List<String> dataSetFileNames, boolean logDatabaseContentOnAssertionError, String... variables) {
        ExpectedDataSetStrategy defaultExpectedDataSetStrategy = new DefaultExpectedDataSetStrategy();
        defaultExpectedDataSetStrategy.init(configuration, database);
        performExpectedDataSetStrategy(defaultExpectedDataSetStrategy, dataSetFileNames, asList(variables), logDatabaseContentOnAssertionError, testInstance.getClass());
    }


    @SuppressWarnings({"unchecked"})
    protected void loadDataSet(Method testMethod, Object testObject) {
        try {
            Class<?> testClass = testObject.getClass();
            Annotation dataSetAnnotation = getMethodOrClassLevelAnnotationAnnotatedWith(DataSetAnnotation.class, testMethod, testClass);
            if (dataSetAnnotation == null) {
                return;
            }

            Database database = createDatabase();

            DataSetAnnotationHandler dataSetAnnotationHandler = getDataSetAnnotationHandler(dataSetAnnotation);
            dataSetAnnotationHandler.init(configuration, database, this);
            dataSetAnnotationHandler.handle(dataSetAnnotation, testClass);

        } catch (Exception e) {
            throw new UnitilsException("Error inserting data set for method " + testMethod, e);
        }
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
        try {
            Class<?> testClass = testObject.getClass();
            Annotation dataSetAnnotation = getMethodOrClassLevelAnnotationAnnotatedWith(ExpectedDataSetAnnotation.class, testMethod, testClass);
            if (dataSetAnnotation == null) {
                return;
            }

            Database database = createDatabase();

            DataSetAnnotationHandler dataSetAnnotationHandler = getDataSetAnnotationHandler(dataSetAnnotation);
            dataSetAnnotationHandler.init(configuration, database, this);
            dataSetAnnotationHandler.handle(dataSetAnnotation, testClass);

        } catch (Exception e) {
            throw new UnitilsException("Error comparing data set for method " + testMethod, e);
        }
    }

    protected DataSetAnnotationHandler getDataSetAnnotationHandler(Annotation dataSetAnnotation) {
        DataSetAnnotation annotation = dataSetAnnotation.annotationType().getAnnotation(DataSetAnnotation.class);
        Class<? extends DataSetAnnotationHandler> dataSetAnnotationHandlerClass = annotation.value();
        return createInstanceOfType(dataSetAnnotationHandlerClass, false);
    }


    public void performLoadDataSetStrategy(LoadDataSetStrategy loadDataSetStrategy, List<String> dataSetFileNames, List<String> variables, Class<?> testClass) {
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            DataSetRowSource dataSetRowSource = xmlDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            loadDataSetStrategy.perform(dataSetRowSource, variables);
        }
    }

    public void performExpectedDataSetStrategy(ExpectedDataSetStrategy expectedDataSetStrategy, List<String> dataSetFileNames, List<String> variables, boolean logDatabaseContentOnAssertionError, Class<?> testClass) {
        List<File> dataSetFiles = resolveDataSets(testClass, dataSetFileNames);
        for (File dataSetFile : dataSetFiles) {
            DataSetRowSource dataSetRowSource = xmlDataSetRowSourceFactory.createDataSetRowSource(dataSetFile);
            expectedDataSetStrategy.assertExpectedDataSet(dataSetRowSource, variables, logDatabaseContentOnAssertionError);
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


    /* FACTORY METHODS */

    protected Database createDatabase() {
        DbSupport defaultDbSupport = getDefaultDbSupport();
        Database database = new Database();
        SqlTypeHandlerRepository sqlTypeHandlerRepository = new SqlTypeHandlerRepository();
        database.init(defaultDbSupport, sqlTypeHandlerRepository);
        return database;
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