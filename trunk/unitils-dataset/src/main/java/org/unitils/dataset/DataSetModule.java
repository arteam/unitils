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

import org.unitils.core.CurrentTestInstance;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.database.DatabaseUpdateListener;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.annotation.handler.MarkerForAssertDataSetAnnotation;
import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.database.DataSourceWrapperFactory;
import org.unitils.dataset.factory.DataSetStrategyHandlerFactory;
import org.unitils.dataset.structure.DataSetStructureGenerator;
import org.unitils.dataset.structure.DataSetStructureGeneratorFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.unitils.database.DatabaseUnitils.getDatabaseNames;
import static org.unitils.database.DatabaseUnitils.registerDatabaseUpdateListener;
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

    /* The unitils configuration */
    protected Properties configuration;
    protected DataSetStructureGeneratorFactory dataSetStructureGeneratorFactory;
    protected DataSetStrategyHandlerFactory dataSetStrategyHandlerFactory;
    protected DataSourceWrapperFactory dataSourceWrapperFactory;


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
        registerDatabaseUpdateListener(databaseUpdateListener);
    }


    public DataSetStrategyHandlerFactory getDataSetStrategyHandlerFactory() {
        if (dataSetStrategyHandlerFactory == null) {
            DataSourceWrapperFactory dataSourceWrapperFactory = getDataSourceWrapperFactory();
            dataSetStrategyHandlerFactory = new DataSetStrategyHandlerFactory(configuration, dataSourceWrapperFactory);
        }
        return dataSetStrategyHandlerFactory;
    }

    public DataSourceWrapperFactory getDataSourceWrapperFactory() {
        if (dataSourceWrapperFactory == null) {
            dataSourceWrapperFactory = new DataSourceWrapperFactory(configuration);
        }
        return dataSourceWrapperFactory;
    }

    public DataSetStructureGeneratorFactory getDataSetStructureGeneratorFactory() {
        if (dataSetStructureGeneratorFactory == null) {
            DataSourceWrapperFactory dataSourceWrapperFactory = getDataSourceWrapperFactory();
            dataSetStructureGeneratorFactory = new DataSetStructureGeneratorFactory(configuration, dataSourceWrapperFactory);
        }
        return dataSetStructureGeneratorFactory;
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
        public void beforeTest(CurrentTestInstance currentTestInstance) throws Exception {
            Object testObject = currentTestInstance.getTestObject();
            Method testMethod = currentTestInstance.getTestMethod();

            loadDataSet(testMethod, testObject);
        }

        @Override
        public void afterTest(CurrentTestInstance currentTestInstance) throws Exception {
            Object testObject = currentTestInstance.getTestObject();
            Method testMethod = currentTestInstance.getTestMethod();
            Throwable testThrowable = currentTestInstance.getTestThrowable();

            if (testThrowable != null) {
                return;
            }
            assertExpectedDataSet(testMethod, testObject);
        }
    }


    protected class DataSetXSDsGeneratingDatabaseUpdateListener implements DatabaseUpdateListener {

        public void databaseWasUpdated() {
            DataSetStructureGenerator dataSetStructureGenerator = getDataSetStructureGeneratorFactory().getDataSetStructureGenerator();
            for (String databaseName : getDatabaseNames()) {
                dataSetStructureGenerator.generateDataSetStructureAndTemplate(databaseName);
            }
        }
    }

}