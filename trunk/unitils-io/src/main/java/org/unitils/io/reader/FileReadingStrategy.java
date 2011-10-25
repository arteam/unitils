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

package org.unitils.io.reader;

import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileReadingStrategy implements ReadingStrategy {

    public InputStream handleFile(Field field, Object testObject, String extension) throws IOException {
        FileContent annotation = field.getAnnotation(FileContent.class);

        String fileName;
        if (annotation.location().isEmpty()) {
            fileName = prefixPackageNameFilePath(testObject.getClass(), resolveFileName(testObject, extension));

        } else {
            fileName = annotation.location();
        }

        InputStream result = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if (result == null) {
            throw new UnitilsException(fileName + " not found.");
        }
        return result;
    }

    protected String resolveFileName(Object testObject, String extension) {
        // TODO code is almost the same as in the DbunitModule at line
        // 448, so this should be refactored.
        String className = testObject.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1, className.length()) + '.' + extension;
    }

    protected String prefixPackageNameFilePath(Class<?> testClass, String fileName) {
        String className = testClass.getName();
        int indexOfLastDot = className.lastIndexOf('.');
        if (indexOfLastDot == -1) {
            return fileName;
        }

        String packageName = className.substring(0, indexOfLastDot).replace('.', '/');
        return packageName + '/' + fileName;
    }
}