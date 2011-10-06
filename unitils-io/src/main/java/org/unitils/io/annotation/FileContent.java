/*
 * Copyright 2008,  Unitils.org
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * 
 * @since 3.3
 * 
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface FileContent {

	/**
	 * Classpath location of a file
	 * 
	 * @return
	 */
	String location() default "";

	/**
	 * Enconding to be used when reading the file. If no encoding is specified
	 * the default JVM encoding will be used
	 * 
	 * Possible values:
	 * 
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
