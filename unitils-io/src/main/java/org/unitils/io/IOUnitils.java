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

package org.unitils.io;

import org.unitils.io.filecontent.FileContentReader;
import org.unitils.io.temp.TempService;

import java.io.File;

import static org.unitils.core.Unitils.getInstanceOfType;
import static org.unitils.util.ReflectionUtils.getTestClass;

/**
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOUnitils {

    protected static FileContentReader fileContentReader = getInstanceOfType(FileContentReader.class);
    protected static TempService tempService = getInstanceOfType(TempService.class);


    /**
     * Loads the content of 'test-class'.'target-type-default-extension' and converts it to the given target type using the default
     * encoding. e.g.
     * <p/>
     * <pre>
     * <code>
     * String result1 = readFileContent(String.class, this);
     * Properties result2 = readFileContent(Properties.class, this);
     * </code>
     * </pre>
     * <p/>
     * If this is an instance of MyTest, this will read-in file name org/myPackage/MyTest.txt (txt is the default extension for the String
     * converter) and return the result as a string.
     * <p/>
     * The second line will read-in file name org/myPackage/MyTest.properties (properties is the default extension of the Properties
     * converter) and return the result as a Properties instance.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param targetType          The target type to convert the content to, not null
     * @param testInstanceOrClass The test instance or class that will be used to construct the file name and locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(Class<T> targetType, Object testInstanceOrClass) {
        return readFileContent(null, targetType, null, testInstanceOrClass);
    }

    /**
     * Loads the content of 'test-class'.'target-type-default-extension' and converts it to the given target type.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param targetType          The target type to convert the content to, not null
     * @param encoding            The encoding to use when reading the file, null for the default encoding
     * @param testInstanceOrClass The test instance or class that will be used to construct the file name and locate the file, not null
     * @return The file content converted to the target type, not null
     * @see org.unitils.io.annotation.FileContent
     */
    public static <T> T readFileContent(Class<T> targetType, String encoding, Object testInstanceOrClass) {
        return readFileContent(null, targetType, encoding, testInstanceOrClass);
    }


    /**
     * Loads the content of the file with the given name and converts it to the given target type using the default encoding. e.g.
     * <p/>
     * <pre>
     * <code>
     * String result1 = readFileContent("myFile.csv", String.class, this);
     * Properties result2 = readFileContent("/myFile.map", Properties.class, this);
     * </code>
     * </pre>
     * <p/>
     * If this is an instance of MyTest, this will read-in file name org/myPackage/myFile.csv and return the result as a string.
     * <p/>
     * The second line will read-in file name myFile.properties and return the result as a Properties instance.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param fileName            The name of the file, not null
     * @param targetType          The target type to convert the content to, not null
     * @param testInstanceOrClass The test instance or class that will be used to locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(String fileName, Class<T> targetType, Object testInstanceOrClass) {
        return readFileContent(fileName, targetType, null, testInstanceOrClass);
    }

    /**
     * Loads the content of the file with the given name and converts it to the given target type.
     * <p/>
     * See {@link org.unitils.io.annotation.FileContent} for more information on how the file is resolved.
     *
     * @param fileName            The name of the file, not null
     * @param targetType          The target type to convert the content to, not null
     * @param encoding            The encoding to use when reading the file, null for the default encoding
     * @param testInstanceOrClass The test instance or class that will be used to locate the file, not null
     * @return The file content converted to the target type, not null
     */
    public static <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Object testInstanceOrClass) {
        Class<?> testClass = getTestClass(testInstanceOrClass);
        return fileContentReader.readFileContent(fileName, targetType, encoding, testClass);
    }

    /**
     * Creates a temporary file with the given name. The parent directory for this file can be specified by setting the
     * {@link org.unitils.io.temp.TempService#ROOT_TEMP_DIR_PROPERTY} property. If no root temp dir is specified the default user
     * temp dir will be used.
     * <p/>
     * Watch out: if the file already exists, it will first be deleted.
     * <p/>
     * The file will not be removed after the test. You can use {@link #deleteTempFileOrDir(java.io.File)}, if you want to perform cleanup
     * after the test.
     *
     * @param fileName The name of the temp file, not null
     * @return The temp file, not null
     */
    public static File createTempFile(String fileName) {
        return tempService.createTempFile(fileName);
    }

    /**
     * Creates a temporary directory with the given name. The parent directory for this directory can be specified by setting the
     * {@link org.unitils.io.temp.TempService#ROOT_TEMP_DIR_PROPERTY} property. If no root temp dir is specified the default user
     * temp dir will be used.
     * <p/>
     * Watch out: if the directory already exists, it will first be deleted. If the directory was not empty, all files in the directory will
     * be deleted.
     * <p/>
     * The directory will not be removed after the test. You can use {@link #deleteTempFileOrDir(java.io.File)}, if you want to perform
     * cleanup after the test.
     *
     * @param dirName The name of the temp dir, not null
     * @return The temp dir, not null
     */
    public static File createTempDir(String dirName) {
        return tempService.createTempDir(dirName);
    }

    /**
     * Deletes the given file or directory.
     * <p/>
     * Watch out: if the directory is not empty, all files in the directory will be deleted.
     * <p/>
     * Nothing will be done if the file or directory is null or does not exist.
     *
     * @param fileOrDir The file or directory to delete, can be null
     */
    public static void deleteTempFileOrDir(File fileOrDir) {
        tempService.deleteTempFileOrDir(fileOrDir);
    }
}
