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

package org.unitils.io.annotation.handler;

import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.filecontent.FileContentReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.setFieldValue;

/**
 * Implements the behavior of the {@link FileContent} annotation.<br/>
 * See annotation javadoc for more info.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileContentAnnotationHandler extends TestListener {

    /* The reader that will get the file content */
    private FileContentReader fileContentReader;

    /**
     * @param fileContentReader The content reader implementation, not null
     */
    public FileContentAnnotationHandler(FileContentReader fileContentReader) {
        this.fileContentReader = fileContentReader;
    }


    /**
     * All fields that have an {@link FileContent} annotation will be handled before the setup of the test.
     * This will convert the content of the requested file to the target type of the field and inject the
     * result into the field.
     *
     * @param testObject The test instance, not null
     * @param testMethod The test method, not null
     */
    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
        Set<Field> fieldsAnnotatedWithFileContent = getFieldsAnnotatedWith(testObject.getClass(), FileContent.class);
        for (Field field : fieldsAnnotatedWithFileContent) {
            readFileContentForField(testObject, field);
        }
    }


    /**
     * Does the actual content reading and injection for the given field.
     *
     * @param testObject The test instance, not null
     * @param field      The field with the FileContent annotation, not null
     */
    protected void readFileContentForField(Object testObject, Field field) {
        FileContent fileContentAnnotation = field.getAnnotation(FileContent.class);

        String encoding = determineEncoding(fileContentAnnotation);
        String fileName = determineFileName(fileContentAnnotation);
        Class<?> targetType = field.getType();
        Class<?> testClass = testObject.getClass();
        try {
            Object result = fileContentReader.readFileContent(fileName, targetType, encoding, testClass);
            setFieldValue(testObject, field, result);

        } catch (Exception e) {
            throw new UnitilsException("Error reading file content for  " + field.getName(), e);
        }
    }

    /**
     * @param fileContentAnnotation The annotation, not null
     * @return the encoding specified in the annotation, null if no encoding was specified
     */
    protected String determineEncoding(FileContent fileContentAnnotation) {
        String encoding = fileContentAnnotation.encoding();
        if (isEmpty(encoding)) {
            return null;
        }
        return encoding;
    }

    /**
     * @param fileContentAnnotation The annotation, not null
     * @return the file name specified in the annotation, null if no file name was specified
     */
    protected String determineFileName(FileContent fileContentAnnotation) {
        String fileName = fileContentAnnotation.value();
        if (isEmpty(fileName)) {
            return null;
        }
        return fileName;
    }
}
