/*
 * Copyright 2011,  Unitils.org
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

package org.unitils.io.annotation;

import org.unitils.io.conversion.ConversionStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation indication a file should be loaded into the annotated object.
 * <p/>
 * The @FileContent will try to load a file (or other resource depending on the ReadingStrategy) into the annotated object.
 * When the location is left blank, the module will search for the file having the same filename and path as the test.
 * <p/>
 * so for example
 * <pre><code>
 * '
 *      public class MyTestClass extends UnitilsJUnit4 {
 *      @FileContent
 *      String someContent;
 * <p/>
 *      ...
 *      }
 *    </code></pre>
 * In this case the io module wil fill up the string someContent by the value in the file foudn in the path :
 * org/unitils/io/MyTestClass.txt
 * <p/>
 * When filling in the location parameter in the FileContent will override the default and that file will be loaded
 * into the variable.
 * <p/>
 * //TODO add an example for properties and check the syntax of the sentences
 *
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface FileContent {

    /**
     * @return File location of a file, this can be relative to the workspace, or an absolute path.
     */
    String location() default "";

    /**
     * Enconding to be used when reading the file. If no encoding is specified
     * the default JVM encoding will be used
     * <p/>
     * Possible values:
     * <p/>
     * <pre>
     *     Charset
     *      Description
     *         US-ASCII Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
     *         ISO-8859-1   ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
     *         UTF-8 Eight-bit UCS Transformation Format
     *         UTF-16BE Sixteen-bit UCS Transformation Format, big-endian byte order
     *         UTF-16LE Sixteen-bit UCS Transformation Format, little-endian byte order
     *         UTF-16 Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
     * </pre>
     *
     * @return encoding
     */
    String encoding() default "";

    Class<? extends ConversionStrategy> conversionStrategy() default ConversionStrategy.class;

}
