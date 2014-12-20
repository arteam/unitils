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

package org.unitils.io.listener;

import org.unitils.core.FieldAnnotationListener;
import org.unitils.core.TestField;
import org.unitils.core.TestInstance;
import org.unitils.core.reflect.Annotations;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.filecontent.FileContentReader;

/**
 * Implements the behavior of the {@link FileContent} annotation.<br/>
 * See annotation javadoc for more info.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class FileContentFieldAnnotationListener extends FieldAnnotationListener<FileContent> {

    /* The reader that will get the file content */
    protected FileContentReader fileContentReader;


    /**
     * @param fileContentReader The content reader implementation, not null
     */
    public FileContentFieldAnnotationListener(FileContentReader fileContentReader) {
        this.fileContentReader = fileContentReader;
    }


    /**
     * All fields that have an {@link FileContent} annotation will be handled before the setup of the test. This will convert the content of
     * the requested file to the target type of the field and inject the result into the field.
     *
     * @param testInstance The test instance, not null
     * @param testField    The test method, not null
     * @param annotations  The file content annotations, not null
     */
    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<FileContent> annotations) {
        FileContent annotation = annotations.getAnnotationWithDefaults();

        String encoding = annotation.encoding();
        String fileName = annotation.value();
        Class<?> targetType = testField.getType();
        Class<?> testClass = testInstance.getClassWrapper().getWrappedClass();

        Object result = fileContentReader.readFileContent(fileName, targetType, encoding, testClass);
        testField.setValue(result);
    }
}
