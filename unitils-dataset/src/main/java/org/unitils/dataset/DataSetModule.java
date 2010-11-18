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
import org.dbmaintain.database.IdentifierProcessor;
import org.dbmaintain.database.IdentifierProcessorFactory;
import org.springframework.test.context.TestContext;
import org.unitils.core.Module;
import org.unitils.core.TestExecutionListenerAdapter;
import org.unitils.database.DatabaseUnitils;
import org.unitils.database.DatabaseUpdateListener;
import org.unitils.database.UnitilsDataSource;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation;
import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.database.DataSourceWrapper;
import org.unitils.dataset.structure.DataSetStructureGenerator;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.unitils.database.DatabaseUnitils.getDbMaintainManager;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationAnnotatedWith;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * Module that provides support for loading test data sets into the database.
 * <p/>
 * Loading of data sets can be done by annotating a class or method with the *DataSet annotations. The name
 * of the data set files can be specified explicitly as an argument of the annotation. If no file name is specified, it looks
 * for a file in the same directory as the test class named: 'classname without packagename'.xml.
 * <p/>
 * By annotating a method with the {@link org.unitils.dataset.annotation.AssertDataSet} annotation or by calling the assertDataSetFiles
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

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DataSetModule.class);

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSetModuleFactory dataSetModuleFactory;


    /**
     * Initializes the DataSetModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;

    }

    public void afterInit() {
        DatabaseUpdateListener databaseUpdateListener = new DataSetXSDsGeneratingDatabaseUpdateListener();
        getDbMaintainManager().registerDatabaseUpdateListener(databaseUpdateListener);
    }


    public DataSetModuleFactory getDataSetModuleFactory() {
        if (dataSetModuleFactory == null) {
            dataSetModuleFactory = createDataSetModuleFactory();
        }
        return dataSetModuleFactory;
    }

    protected DataSetModuleFactory createDataSetModuleFactory() {
        // todo database name
        UnitilsDataSource unitilsDataSource = DatabaseUnitils.getUnitilsDataSource(null);

        IdentifierProcessor identifierProcessor = createIdentifierProcessor(unitilsDataSource);
        DataSourceWrapper dataSourceWrapper = createDataSourceWrapper(identifierProcessor, unitilsDataSource);
        return new DataSetModuleFactory(configuration, dataSourceWrapper, identifierProcessor);
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


    protected DataSourceWrapper createDataSourceWrapper(IdentifierProcessor identifierProcessor, UnitilsDataSource unitilsDataSource) {
        return new DataSourceWrapper(unitilsDataSource, identifierProcessor);
    }

    protected IdentifierProcessor createIdentifierProcessor(UnitilsDataSource unitilsDataSource) {
        String databaseDialect = unitilsDataSource.getDialect();
        String defaultSchemaName = unitilsDataSource.getDefaultSchemaName();
        DataSource dataSource = unitilsDataSource.getDataSource();
        IdentifierProcessorFactory identifierProcessorFactory = new IdentifierProcessorFactory(configuration);
        return identifierProcessorFactory.createIdentifierProcessor(databaseDialect, defaultSchemaName, dataSource);
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
            DataSetStructureGenerator dataSetStructureGenerator = getDataSetModuleFactory().getDataSetStructureGenerator();
            dataSetStructureGenerator.generateDataSetStructureAndTemplate();
        }
    }

}