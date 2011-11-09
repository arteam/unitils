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

package org.unitils.io;

import org.unitils.core.Unitils;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOUnitils {

    /**
     * Loads the content of 'test-class'.'target-type-default-extension' and converts it to the given target type using the default encoding.
     * e.g.
     * <pre><code>
     * String result1 = readFileContent(String.class, this);
     * Properties result2 = readFileContent(Properties.class, this);
     * </code></pre>
     * If this is an instance of MyTest, this will read-in file name  org/myPackage/MyTest.txt  (txt is the default extension for the String converter) and return the result as a string.<p/>
     * The second line will read-in file name org/myPackage/MyTest.properties  (properties is the default extension of the Properties converter) and return the result as a Properties instance.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param targetType   The target type to convert the content to, not null
     * @param testInstance The test instance that will be used to construct the file name and locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(Class<T> targetType, Object testInstance) {
        return readFileContent(null, targetType, null, testInstance);
    }

    /**
     * Loads the content of 'test-class'.'target-type-default-extension' and converts it to the given target type.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param targetType   The target type to convert the content to, not null
     * @param encoding     The encoding to use when reading the file, null for the default encoding
     * @param testInstance The test instance that will be used to construct the file name and locate the file, not null
     * @return The file content converted to the target type, not null
     * @see org.unitils.io.annotation.FileContent
     */
    public static <T> T readFileContent(Class<T> targetType, String encoding, Object testInstance) {
        return readFileContent(null, targetType, encoding, testInstance);
    }


    /**
     * Loads the content of the file with the given name and converts it to the given target type using the default encoding.
     * e.g.
     * <pre><code>
     * String result1 = readFileContent("myFile.csv", String.class, this);
     * Properties result2 = readFileContent("/myFile.map", Properties.class, this);
     * </code></pre>
     * If this is an instance of MyTest, this will read-in file name  org/myPackage/myFile.csv  and return the result as a string.<p/>
     * The second line will read-in file name myFile.properties  and return the result as a Properties instance.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param fileName     The name of the file, not null
     * @param targetType   The target type to convert the content to, not null
     * @param testInstance The test instance that will be used to locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(String fileName, Class<T> targetType, Object testInstance) {
        return readFileContent(fileName, targetType, null, testInstance);
    }

    /**
     * Loads the content of the file with the given name and converts it to the given target type.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param fileName     The name of the file, not null
     * @param targetType   The target type to convert the content to, not null
     * @param encoding     The encoding to use when reading the file, null for the default encoding
     * @param testInstance The test instance that will be used to locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Object testInstance) {
        Class<?> testClass = testInstance.getClass();
        return getIOModule().readFileContent(fileName, targetType, encoding, testClass);
    }


    private static IOModule getIOModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(IOModule.class);
    }
}
