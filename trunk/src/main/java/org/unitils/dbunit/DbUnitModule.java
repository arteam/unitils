/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbunit;

import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupport;
import static org.unitils.core.dbsupport.DbSupportFactory.getDefaultDbSupport;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotation;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getClassValueReplaceDefault;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.database.transaction.TransactionalDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.dbunit.util.MultiSchemaXmlDataSetReader;
import org.unitils.dbunit.util.DbUnitAssert;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.unitils.dbunit.util.MultiSchemaDataSet;

/**
 * Module that provides support for managing database test data using DBUnit.
 * <p/>
 * Loading of DbUnit data sets can be done by annotating a class or method with the {@link DataSet} annotation. The name
 * data set file can be specified explicitly as an argument of the annotation. If no file name is specified, it looks
 * for following files in the same directory as the test class:
 * <ol>
 * <li>'classname without packagename'.'test method name'.xml</li>
 * <li>'classname without packagename'.xml</li>
 * <p/>
 * If the method specific data set file is found, this will be used, otherwise it will look for the class-level data set
 * file. See the {@link DataSet} javadoc for more info.
 * <p/>
 * By annotating a method with the {@link ExpectedDataSet} annotation or by calling the {@link #assertDbContentAsExpected}
 * method, the contents of the database can be compared with the contents of a dataset. The expected dataset can be
 * passed as an argument of the annotation. If no file name is specified it looks for a file in the same directory
 * as the test class that has following name: 'classname without packagename'.'test method name'-result.xml.
 * <p/>
 * This module depends on the {@link DatabaseModule} for database connection management.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModule implements Module {

    /* Map holding the default configuration of the dbunit module annotations */
    private Map<Class<? extends Annotation>, Map<Method, String>> defaultAnnotationPropertyValues;

    /*
     * Objects that DbUnit uses to connect to the database and to cache some database metadata. Since DBUnit's data
     * caching is time-consuming, this object is created only once and used throughout the entire test run. The
     * underlying JDBC Connection however is 'closed' (returned to the pool) after every database operation.
     *
     * A different DbUnit connection is used for every database schema. Since DbUnit can only work with a single schema,
     * this is the simplest way to obtain multi-schema support. 
     */
    private Map<String, DbUnitDatabaseConnection> dbUnitDatabaseConnections = new HashMap<String, DbUnitDatabaseConnection>();

    /* The unitils configuration */
    private Properties configuration;


    /**
     * Initializes the DbUnitModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    public void init(Properties configuration) {
        this.configuration = configuration;

        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DbUnitModule.class, configuration, DataSet.class, ExpectedDataSet.class);
    }


    /**
     * Gets the DbUnit connection or creates one if it does not exist yet.
     *
     * @param schemaName The schema name, not null
     * @return The DbUnit connection, not null
     */
    public DbUnitDatabaseConnection getDbUnitDatabaseConnection(String schemaName) {
        DbUnitDatabaseConnection dbUnitDatabaseConnection = dbUnitDatabaseConnections.get(schemaName);
        if (dbUnitDatabaseConnection == null) {
            dbUnitDatabaseConnection = createDbUnitConnection(schemaName);
            dbUnitDatabaseConnections.put(schemaName, dbUnitDatabaseConnection);
        }
        return dbUnitDatabaseConnection;
    }


    /**
     * This method will first try to load a method level defined dataset. If no such file exists, a class level defined
     * dataset will be loaded. If neither of these files exist, nothing is done.
     * The name of the test data file at both method level and class level can be overridden using the
     * {@link DataSet} annotation. If specified using this annotation but not found, a {@link UnitilsException} is
     * thrown.
     *
     * @param testMethod The method, not null
     */
    public void insertTestData(Method testMethod, Object testObject) {
        try {
            MultiSchemaDataSet multiSchemaDataSet = getTestDataSets(testMethod, testObject);
            if (multiSchemaDataSet == null) {
                // no dataset specified
                return;
            }
            DataSetLoadStrategy dataSetLoadStrategy = getDataSetOperation(testMethod);

            for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
                IDataSet dataSet = multiSchemaDataSet.getDataSetForSchema(schemaName);
                dataSetLoadStrategy.execute(getDbUnitDatabaseConnection(schemaName), dataSet);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error inserting test data from DbUnit dataset for method " + testMethod, e);
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>
     *
     * @param inputStream       The stream containing the test data set, not null
     * @param databaseOperation The dbunit DatabaseOperation that must be executed on this DataSet
     */
    public void insertTestData(InputStream inputStream, DataSetLoadStrategy dataSetLoadStrategy) {
        try {
            MultiSchemaDataSet multiSchemaDataSet = getDataSet(inputStream);
            for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
                IDataSet dataSet = multiSchemaDataSet.getDataSetForSchema(schemaName);
                dataSetLoadStrategy.execute(getDbUnitDatabaseConnection(schemaName), dataSet);
            }
        } catch (Exception e) {
            throw new UnitilsException("Error inserting test data from DbUnit dataset.", e);
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param testMethod The test method, not null
     */
    public void assertDbContentAsExpected(Method testMethod, Object testObject) {
        try {
            // get the expected dataset
            MultiSchemaDataSet multiSchemaExpectedDataSet = getExpectedTestDataSet(testMethod, testObject);
            if (multiSchemaExpectedDataSet == null) {
                // no data set should be compared
                return;
            }
            // first make sure every database update is flushed to the database
            getDatabaseModule().flushDatabaseUpdates();
            
            for (String schemaName : multiSchemaExpectedDataSet.getSchemaNames()) {
                IDataSet expectedDataSet = multiSchemaExpectedDataSet.getDataSetForSchema(schemaName);

                DbUnitAssert.assertDbContentAsExpected(expectedDataSet, getDbUnitDatabaseConnection(schemaName));
            }
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Using the values of the method-level or class-level {@link DataSet} annotations, returns the data set for the
     * given test method. If no method-level or class-level {@link DataSet} annotation is found, null is returned.
     * If a method-level {@link DataSet} annotation is found this will be used, else the class-level will be used.
     * <p/>
     * The value of the found annotation determines which file needs to be used for the dataset. If a filename is
     * explicitly specified, this name will be used. Filenames that start with '/' are treated absolute. Filenames
     * that do not start with '/', are relative to the current class.
     * If an empty filename ("") is specified, it will first look for a file named 'classname'.'testmethod'.xml (as defined
     * in {@link #getMethodLevelDefaultTestDataSetFileName}). If that file does not exist it will look for a file
     * named 'classname'.xml {@link #getClassLevelDefaultTestDataSetFileName}).
     * <p/>
     * If a file is not found or could not be loaded (but was requested, because there is an annotation), an exception
     * is raised.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     * @return The dataset, null if no {@link DataSet} annotation is found.
     */
    public MultiSchemaDataSet getTestDataSets(Method testMethod, Object testObject) {
        DataSet dataSetAnnotation = getMethodOrClassLevelAnnotation(DataSet.class, testMethod, testObject.getClass());
        if (dataSetAnnotation == null) {
            // No @DataSet annotation found
            return null;
        }

        String dataSetFileName = dataSetAnnotation.value();
        Class<?> testClass = testMethod.getDeclaringClass();

        // Create configured factory for data sets
        DataSetFactory dataSetFactory = getDataSetFactory(DataSet.class, testMethod);

        // empty means, use default file name
        if ("".equals(dataSetFileName)) {
            // first try method specific default file name
            dataSetFileName = getMethodLevelDefaultTestDataSetFileName(testMethod, dataSetFactory.getDataSetFileExtension());
            MultiSchemaDataSet dataSets = getDataSet(testClass, dataSetFileName, dataSetFactory);
            if (dataSets != null) {
                // found file, so return
                return dataSets;
            }
            // not found, try class default file name
            dataSetFileName = getClassLevelDefaultTestDataSetFileName(testClass, dataSetFactory.getDataSetFileExtension());
        }

        MultiSchemaDataSet dataSets = getDataSet(testClass, dataSetFileName, dataSetFactory);
        if (dataSets == null) {
            throw new UnitilsException("Could not find DbUnit dataset with name " + dataSetFileName);
        }
        return dataSets;
    }


    /**
     * Returns the {@link MultiSchemaDataSet} that represents the state of a number of database tables after the given
     * <code>Method</code> has been executed.
     *
     * @param testMethod The test method, not null
     * @return The dataset, null if there is no data set
     */
    public MultiSchemaDataSet getExpectedTestDataSet(Method testMethod, Object testObject) {
        ExpectedDataSet expectedDataSetAnnotation = getMethodOrClassLevelAnnotation(ExpectedDataSet.class, testMethod, testObject.getClass());
        if (expectedDataSetAnnotation == null) {
            // No @ExpectedDataSet annotation found
            return null;
        }

        // Create configured factory for data sets
        DataSetFactory dataSetFactory = getDataSetFactory(ExpectedDataSet.class, testMethod);

        String dataSetFileName = expectedDataSetAnnotation.value();
        // empty means use default file name
        if ("".equals(dataSetFileName)) {
            // first try method specific default file name
            dataSetFileName = getDefaultExpectedDataSetFileName(testMethod, dataSetFactory.getDataSetFileExtension());
        }

        MultiSchemaDataSet dataSets = getDataSet(testMethod.getDeclaringClass(), dataSetFileName, dataSetFactory);
        if (dataSets == null) {
            throw new UnitilsException("Could not find expected DbUnit dataset with name " + dataSetFileName);
        }
        return dataSets;
    }


    /**
     * Creates the dataset for the given file. Filenames that start with '/' are treated absolute. Filenames that
     * do not start with '/', are relative to the current class.
     *
     * @param testClass       The test class, not null
     * @param dataSetFilename The name, (start with '/' for absolute names), not null
     * @param dataSetFactory  DataSetFactory responsible for creating the dataset file
     * @return The data set, null if the file does not exist
     */
    protected MultiSchemaDataSet getDataSet(Class testClass, String dataSetFilename, DataSetFactory dataSetFactory) {
        try {
            InputStream in = testClass.getResourceAsStream(dataSetFilename);
            if (in == null) {
                // file does not exist
                return null;
            }
            return getDataSet(in);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for file " + dataSetFilename, e);
        }
    }


    /**
     * todo javadoc
     * <p/>
     * Create a {@link MultiSchemaDataSet}, in which the file <code>InputStream</code> is loaded.
     *
     * @param in the InputStream, not null
     * @return The DbUnit <code>IDataSet</code>
     */
    public MultiSchemaDataSet getDataSet(InputStream in) {
        try {
            // A db support instance is created to get the default schema name in correct casing
            DataSource dataSource = getDatabaseModule().getDataSource();
            SQLHandler sqlHandler = new SQLHandler(dataSource);
            DbSupport defaultDbSupport = getDefaultDbSupport(configuration, sqlHandler);

            MultiSchemaXmlDataSetReader multiSchemaXmlDataSetReader = new MultiSchemaXmlDataSetReader(defaultDbSupport.getSchemaName());
            return multiSchemaXmlDataSetReader.readDataSetXml(in);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create DbUnit dataset for input stream.", e);
        } finally {
            closeQuietly(in);
        }
    }


    @SuppressWarnings({"unchecked"})
    protected DataSetLoadStrategy getDataSetOperation(Method testMethod) {
        Class<? extends DataSetLoadStrategy> dataSetOperationClass = getMethodOrClassLevelAnnotationProperty(DataSet.class, "loadStrategy", DataSetLoadStrategy.class, testMethod);
        dataSetOperationClass = (Class<? extends DataSetLoadStrategy>) getClassValueReplaceDefault(DataSet.class, "loadStrategy", dataSetOperationClass, defaultAnnotationPropertyValues, DataSetLoadStrategy.class);

        return createInstanceOfType(dataSetOperationClass, false);
    }


    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @param schemaName The schema name, not null
     * @return A new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected DbUnitDatabaseConnection createDbUnitConnection(String schemaName) {
        // A db support instance is created to get the schema name in correct casing
        TransactionalDataSource dataSource = getDatabaseModule().getDataSource();
        SQLHandler sqlHandler = new SQLHandler(dataSource);
        DbSupport dbSupport = getDbSupport(configuration, sqlHandler, schemaName);

        // Create connection
        DbUnitDatabaseConnection connection = new DbUnitDatabaseConnection(dataSource, dbSupport.getSchemaName());

        /* Create DbUnits IDataTypeFactory, that handles dbms specific data type issues */
        IDataTypeFactory dataTypeFactory = (IDataTypeFactory) getConfiguredInstance(IDataTypeFactory.class, configuration, dbSupport.getDatabaseDialect());

        // Make sure correct dbms specific data types are used
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        return connection;
    }


    /**
     * Closes (i.e. return to the pool) the JDBC Connection that is currently in use by the DbUnitDatabaseConnection
     */
    protected void closeJdbcConnection() {
        try {
            for (DbUnitDatabaseConnection dbUnitDatabaseConnection : dbUnitDatabaseConnections.values()) {
                dbUnitDatabaseConnection.closeJdbcConnection();
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while closing connection.", e);
        }
    }


    /**
     * Gets the name of the default testdata file at method level. The default name is constructed as
     * follows: classname + '.' + methodName + '.xml'
     *
     * @param method    The test method, not null
     * @param extension The configured extension of dataset files
     * @return The default filename, not null
     */
    protected String getMethodLevelDefaultTestDataSetFileName(Method method, String extension) {
        String className = method.getDeclaringClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + '.' + extension;
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files
     * @return The default filename, not null
     */
    protected String getClassLevelDefaultTestDataSetFileName(Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + '.' + extension;
    }


    /**
     * Gets the name of the expected dataset file. The default name of this file is constructed as
     * follows: 'classname without packagename'.'testname'-result.xml.
     *
     * @param method    The test method, not null
     * @param extension The configured extension of dataset files
     * @return The expected dataset filename, not null
     */
    protected static String getDefaultExpectedDataSetFileName(Method method, String extension) {
        String className = method.getDeclaringClass().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + "-result." + extension;
    }


    /**
     * Get the configured DataSetFactory for the given method
     *
     * @param annotationClass The class of the annotation, i.e. DataSet.class or ExpectedDataSet.class
     * @param testMethod      The method for which we need the configured DataSetFactory
     * @return The configured DataSetFactory
     */
    @SuppressWarnings("unchecked")
    protected DataSetFactory getDataSetFactory(Class<? extends Annotation> annotationClass, Method testMethod) {
        Class<? extends DataSetFactory> dataSetFactoryClass = getMethodOrClassLevelAnnotationProperty(annotationClass, "factory", DataSetFactory.class, testMethod);
        dataSetFactoryClass = (Class<? extends DataSetFactory>) getClassValueReplaceDefault(annotationClass, "factory", dataSetFactoryClass, defaultAnnotationPropertyValues, DataSetFactory.class);
        return createInstanceOfType(dataSetFactoryClass, false);
    }


    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    /**
     * @return The TestListener object that implements Unitils' DbUnit support
     */
    public TestListener createTestListener() {
        return new DbUnitListener();
    }


    /**
     * Test listener that is called while the test framework is running tests
     */
    protected class DbUnitListener extends TestListener {

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            insertTestData(testMethod, testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
            if (throwable == null) {
                assertDbContentAsExpected(testMethod, testObject);
            }
        }

    }

}
