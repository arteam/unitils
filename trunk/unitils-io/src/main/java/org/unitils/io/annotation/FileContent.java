/*
 * Copyright 2012,  Unitils.org
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

import org.unitils.io.listener.FileContentFieldAnnotationListener;
import org.unitilsnew.core.annotation.FieldAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for loading the contents of a file into a field.
 * <p/>
 * The @FileContent will try to load a file (or other resource depending on the ReadingStrategy), convert it to the
 * type of the annotated field and inject the result into the field.
 * <p/>
 * A file name can be specified as value explicitly. If no file name is specified, a default file name will be used:
 * 'classname'.'extension'. The extension depends on the target type: 'txt' for String and 'properties' for Properties.
 * <p/>
 * By default, the file name is prefixed with the package name (. replaced by /).<br/>
 * E.g. MyFile.xml becomes com/myPackage/MyFile.xml
 * <p/>
 * If a file name starts with a / it will not be prefixed with the package name.<br/>
 * E.g. /MyFile.xml remains /MyFile.xml
 * <p/>
 * Package name prefixing can be disabled using the IOModule.file.prefixWithPackageName property.<br/>
 * IOModule.file.prefixWithPackageName=false => MyFile.xml remains MyFile.xml
 * <p/>
 * If a path prefix is specified using the IOModule.file.pathPrefix property it is added to the file name.<br/>
 * Examples:<br/>
 * <p/>
 * IOModule.file.pathPrefix=myPathPrefix: MyFile.xml becomes myPathPrefix/com/myPackage/MyFile.xml<br/>
 * IOModule.file.pathPrefix=myPathPrefix: /MyFile.xml becomes myPathPrefix/MyFile.xml<br/>
 * <p/>
 * If the path prefix starts with '/', the file name is treated absolute, else it will be treated relative to the classpath.
 * <p/>
 * Examples:
 * <p/>
 * path prefix /c:/testfiles  --> looks for c:/testfiles/MyFile.xml on the file system
 * path prefix testfiles      --> looks for testfiles/MyFile.xml on the classpath
 * <p/><p/>
 * Example usage:
 * <pre><code>
 * public class MyTestClass extends UnitilsJUnit4 {
 * <p/>
 * '    @FileContent
 *      private String field1;
 * '    @FileContent("/someFile.properties")
 *      private Properties field2;
 *      ...
 * }
 * </code></pre>
 * In this example the content of 'org/mypackage/MyTestClass.txt" will be injected into field1 and the properties loaded from
 * 'someFile.properties' will be injected into field2.
 * <p/>
 * The encoding of the file can be passed as an argument. By default ISO-8859-1 is used. The default can be set by
 * overriding the IOModule.encoding.default property.
 * <p/>
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
@Target(FIELD)
@Retention(RUNTIME)
@FieldAnnotation(FileContentFieldAnnotationListener.class)
public @interface FileContent {

    /**
     * @return File location of a file, this can be relative to the workspace, or an absolute path.
     */
    String value() default "";

    /**
     * Encoding to be used when reading the file. If no encoding is specified
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

}
