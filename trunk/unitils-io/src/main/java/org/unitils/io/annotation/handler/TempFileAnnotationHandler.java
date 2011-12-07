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
import org.unitils.io.annotation.TempFile;
import org.unitils.io.temp.TempService;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.unitils.util.AnnotationUtils.getFieldsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.getFieldValue;
import static org.unitils.util.ReflectionUtils.setFieldValue;

/**
 * Implements the behavior of the {@link TempFile} annotation.<br/>
 * See annotation javadoc for more info.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TempFileAnnotationHandler extends TestListener {

    /* True if the temp files should be deleted after the test */
    protected Boolean cleanupAfterTest;
    /* The file service that will create and delete the temp dirs */
    protected TempService tempService;


    public TempFileAnnotationHandler(TempService tempService, boolean cleanupAfterTest) {
        this.tempService = tempService;
        this.cleanupAfterTest = cleanupAfterTest;
    }


    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
        Set<Field> tmpFileFields = getFieldsAnnotatedWith(testObject.getClass(), TempFile.class);
        for (Field field : tmpFileFields) {
            createTempFileForField(testObject, testMethod, field);
        }
    }

    @Override
    public void afterTestMethod(Object testObject, Method testMethod, Throwable t) {
        if (!cleanupAfterTest) {
            return;
        }
        Set<Field> tmpFileFields = getFieldsAnnotatedWith(testObject.getClass(), TempFile.class);
        for (Field field : tmpFileFields) {
            deleteTempFileForField(testObject, field);
        }
    }

    protected void createTempFileForField(Object testObject, Method testMethod, Field field) {
        TempFile annotation = field.getAnnotation(TempFile.class);
        String fileName = annotation.value();
        if (fileName.isEmpty()) {
            fileName = testObject.getClass().getName() + "-" + testMethod.getName() + ".tmp";
        }
        try {
            File tempFile = tempService.createTempFile(fileName);
            setFieldValue(testObject, field, tempFile);
        } catch (Exception e) {
            throw new UnitilsException("Error creating temp file for field " + field.getName(), e);
        }
    }

    protected void deleteTempFileForField(Object testObject, Field field) {
        try {
            File tempFile = getFieldValue(testObject, field);
            tempService.deleteTempFileOrDir(tempFile);
        } catch (Exception e) {
            throw new UnitilsException("Error deleting temp file for field " + field.getName(), e);
        }
    }

}
