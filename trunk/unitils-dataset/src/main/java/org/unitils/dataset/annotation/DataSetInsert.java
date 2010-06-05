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
package org.unitils.dataset.annotation;

import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.annotation.handler.impl.DataSetInsertAnnotationHandler;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for triggering the loading of a data set before the execution of a test.
 * <br/><br/>
 * Insert means the data set will just be loaded in the database. If the record is already in the database or there
 * are conflicting records in the table, an exception will be raised. See {@link org.unitils.dataset.annotation.DataSetCleanInsert} and
 * {@link org.unitils.dataset.annotation.DataSetRefresh} for other possible ways of loading.
 * <br/><br/>
 * This annotation can be put on method or on class level. If the annotation is found on class and method level, the
 * one on method level is taken. If a method is annotated, a data set will be loaded when that test method is run.
 * If a class (or super-class) is annotated, a data set will be loaded for all test methods in that class.
 * <br/><br/>
 * You can explicitly specify 1 or more file names of data sets that need to be loaded. If no file name is specified,
 * a default file name, 'classname'.xml, will be used. File names that start with '/' are treated absolute. File names
 * that do not start with '/', are treated relative to the current class. See {@link org.unitils.dataset.resolver.impl.DefaultDataSetResolver}
 * for more info and more options.
 * <br/><br/>
 * Variables can be provided. The first variable will replace all $1 values in the data set, the second variable all $2 values
 * and so on. If no variable is provided for a place holder, it will not be replaced.
 * <br/><br/>
 * When you know that a test will not modify any data in the database (eg only queries data), you can mark a data set as
 * read-only. If a next test needs to load the same data set (and there was no other non-read-only test) it will not
 * be loaded again. This will improve the performance of the tests, avoiding unnecessary data set loading.
 * <br/><br/>
 * Examples:
 * <pre><code>
 * '    @DataSetInsert
 * '    public class MyTestClass extends UnitilsJUnit4 {
 * '
 * '        @Test
 * '        public void method1(){
 * '        }
 * '
 * '        @Test
 * '        @DataSetInsert("aCustomFileName.xml")
 * '        public void method2(){
 * '        }
 * '    }
 * </code></pre>
 * Will load a data set file named MyTestClass.xml in the same directory as the class for method1 and
 * a data set file named aCustomFileName.xml in the same directory as the class for method2.
 * loaded.
 * <br/><br/>
 * <pre><code>
 * '    public class MyTestClass extends UnitilsJUnit4 {
 * '
 * '        @Test
 * '        public void method1(){
 * '        }
 * '
 * '        @Test
 * '        @DataSetInsert
 * '        public void method2(){
 * '        }
 * '    }
 * </code></pre>
 * Will not load any data set for method1 (there is no class level data set) and will load a data set file named
 * MyTestClass.xml for method2.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
@MarkerForLoadDataSetAnnotation(DataSetInsertAnnotationHandler.class)
public @interface DataSetInsert {

    /**
     * The file names of the data sets. If left empty, the default filename will
     * be used: 'classname'.xml. An UnitilsException will be raised when a data set file cannot be found.
     *
     * @return the fileName, empty for default
     */
    String[] value() default {};

    /**
     * Variables that will be used to fill in possible variable declarations in a data set.
     * <p/>
     * A data set can contain variable declarations: $0, $1 ...
     * When the data set is loaded, these declarations are replaced with the given variable values.
     * The first variable will replace $0, the second $1 ...
     * When no value is found, the $0 variable declaration will not be replaced and will remain in the data set.
     * <p/>
     * The character that defines a variable token, e.g. $0, can be defined using the variableToken parameter on the
     * dataset root xml element. By default $ is used, but you can override this default by setting
     * the dataset.variabletoken.default unitils property.
     * <p/>
     * Variable declarations can be escaped by using a double variable token, e.g. $$0 will be replaced by $0
     * instead of the variable value
     *
     * @return The values to use when replacing the variable declarations (e.g. $0) in the data set, empty by default
     */
    String[] variables() default {};

    /**
     * You can mark a data set as read-only if you're sure that the tests will not modify the data. If a next test needs
     * the same data set to be loaded, it will not be loaded again avoiding unnecessary data set loading.
     *
     * @return True if the data of the data sets will not be modified by the tests, false by default
     */
    boolean readOnly() default false;

}