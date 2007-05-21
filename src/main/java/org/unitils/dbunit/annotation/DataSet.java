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
package org.unitils.dbunit.annotation;

import org.unitils.dbunit.datasetoperation.DataSetOperation;
import org.unitils.dbunit.datasetoperation.DefaultDataSetOperation;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetfactory.DefaultDataSetFactory;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a data set should be loaded before the test run.
 * <p/>
 * If a class is annotated, a test data set will be loaded before the execution of each of the test methods in
 * the class. A data set file name can explicitly be specified. If no such file name is specified, first a data set
 * named 'classname'.'testmethod'.xml will be tried, if no such file exists, 'classname'.xml will be tried. If that
 * file also doesn't exist, an exception will be thrown. Filenames that start with '/' are treated absolute. Filenames
 * that do not start with '/', are relative to the current class.
 * <p/>
 * A test method can also be annotated with DataSet in which case you specify the dataset that needs to be loaded
 * before this test method is run. Again, a file name can explicitly be specified or if not specified, the default will
 * be used: first 'classname'.'methodname'.xml and if that file does not exist 'classname'.xml.
 * <p/>
 * Examples:
 * <pre><code>
 * '    @DataSet
 *      public class MyTestClass extends UnitilsJUnit3 {
 * '
 *          public void testMethod1(){
 *          }
 * '
 * '        @DataSet("aCustomFileName.xml")
 *          public void testMethod2(){
 *          }
 *      }
 * </code></pre>
 * Will load a data set file named MyTestClass.xml or MyTestClass-testMethod1.xml for testMethod1 in the same directory
 * as the class. And for testMethod2 a data set file named aCustomFileName.xml in the same directory as the class is
 * loaded.
 * <p/>
 * <pre><code>
 *      public class MyTestClass extends UnitilsJUnit3 {
 * '
 *          public void testMethod1(){
 *          }
 * '
 * '        @DataSet
 *          public void testMethod2(){
 *          }
 *      }
 * </code></pre>
 * Will not load any dataset for testMethod1 (there is no class level data set). Will load a data set file named
 * MyTestClass.xml or MyTestClass.testMethod2.xml for testMethod2.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
public @interface DataSet {


    /**
     * The file name of the data set. If left empty, the default filename will
     * be used: first 'classname'.'testMethodname'.xml will be tried, if that file does not exist,
     * 'classname'.xml is tried. If that file also does not exist, an exception is thrown.
     *
     * @return the fileName, empty for default
     */
    String value() default "";

    /**
     * The operation that needs to be executed to get the dbunit dataset into the database
     *
     * @return An implementation class of {@link DataSetOperation}. Use the default value {@link DefaultDataSetOperation}
     *         to make use of the default DataSetOperation configured in the unitils configuration.
     */
    Class<? extends DataSetOperation> operation() default DefaultDataSetOperation.class;

    /**
     * The factory that needs to be used to read the dataset file and create a {@link org.unitils.dbunit.util.MultiSchemaDataSet}
     * object
     * 
     * @return An implementation class of {@link DataSetFactory}. Use the default value {@link DefaultDataSetFactory}
     *         to make use of the default DataSetOperation configured in the unitils configuration.
     */
    Class<? extends DataSetFactory> factory() default DefaultDataSetFactory.class;
}
