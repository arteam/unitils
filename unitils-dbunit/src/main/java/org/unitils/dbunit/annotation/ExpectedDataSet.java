/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.core.annotation.AnnotationDefault;
import org.unitils.core.annotation.TestAnnotation;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.listener.ExpectedDataSetTestAnnotationListener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation indicating that after having executed a test method, the contents of the unit test database should be
 * equal to the contents of a data set.
 * <p/>
 * If a class is annotated, the content of the unit test database will be compared with a dataset after the execution
 * of each of the test methods in the class. A data set file name can explicitly be specified. If no such file name is
 * specified, the default 'classname'.'testmethod'-result.xml will be tried, if no such file, an exception will be
 * thrown. Filenames that start with '/' are treated absolute. Filenames that do not start with '/', are relative to
 * the current class.
 * <p/>
 * A test method can also be annotated with ExpectedDataSet in which case you specify the dataset that needs to be used
 * for this test method. Again, a file name can explicitly be specified or if not specified, the default will
 * be used: 'classname'.'methodname'-result.xml. Example:
 * <p/>
 * Examples:
 * <p/>
 * <pre><code>
 * '    @ExpectedDataSet
 *      public class MyTestClass extends UnitilsJUnit3 {
 * '
 *          public void testMethod1(){
 *          }
 * '
 * '        @ExpectedDataSet("aCustomFileName.xml")
 *          public void testMethod2(){
 *          }
 *      }
 * </code></pre>
 * Will check the resulting contents of the unit test database using a data set file named MyTestClass.testMethod1-result.xml
 * in the same directory as the class for testMethod1 and aCustomFileName.xml for testMethod2.
 * <p/>
 * <pre><code>
 *      public class MyTestClass extends UnitilsJUnit3 {
 * '
 *          public void testMethod1(){
 *          }
 * '
 * '        @ExpectedDataSet
 *          public void testMethod2(){
 *          }
 *      }
 * </code></pre>
 * Will not perform any dataset check for testMethod1 (there is no class level expected data set). And will use a data set
 * file named MyTestClass.testMethod2-result.xml for testMethod2.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@TestAnnotation(ExpectedDataSetTestAnnotationListener.class)
public @interface ExpectedDataSet {

    /**
     * The file name of the data set. If left empty, the default filename will be
     * used: 'classname'.'methodname'-result.xml. If that file also does not exist, an exception is thrown.
     *
     * @return the fileName, empty for default
     */
    String[] value() default {};

    @AnnotationDefault Class<? extends DataSetFactory> factory() default DataSetFactory.class;

}
