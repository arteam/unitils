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

import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.TempFile;
import org.unitils.io.temp.TempService;
import org.unitilsnew.core.FieldAnnotationListener;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.annotation.Property;
import org.unitilsnew.core.reflect.Annotations;

import java.io.File;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implements the behavior of the {@link TempFile} annotation.<br/>
 * See annotation javadoc for more info.
 *
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @since 3.3
 */
public class TempFileFieldAnnotationListener extends FieldAnnotationListener<TempFile> {

    public static final String CLEANUP_AFTER_TEST_PROPERTY = "io.temp.cleanupAfterTest";

    /* True if the temp files should be deleted after the test */
    protected Boolean cleanupAfterTest;
    /* The file service that will create and delete the temp dirs */
    protected TempService tempService;


    public TempFileFieldAnnotationListener(TempService tempService, @Property(CLEANUP_AFTER_TEST_PROPERTY) Boolean cleanupAfterTest) {
        this.tempService = tempService;
        this.cleanupAfterTest = cleanupAfterTest;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance, TestField testField, Annotations<TempFile> annotations) {
        String fileName = annotations.getAnnotationWithDefaults().value();
        createTempFileForField(testInstance, testField, fileName);
    }

    @Override
    public void afterTestMethod(TestInstance testInstance, TestField testField, Annotations<TempFile> annotations, Throwable testThrowable) {
        if (!cleanupAfterTest) {
            return;
        }
        deleteTempFileForField(testField);
    }


    protected void createTempFileForField(TestInstance testInstance, TestField testField, String fileName) {
        if (isBlank(fileName)) {
            fileName = testInstance.getClassWrapper().getName() + "-" + testInstance.getTestMethod().getName() + ".tmp";
        }
        try {
            File tempFile = tempService.createTempFile(fileName);
            testField.setValue(tempFile);

        } catch (Exception e) {
            throw new UnitilsException("Error creating temp file for field '" + testField.getName() + "'", e);
        }
    }

    protected void deleteTempFileForField(TestField testField) {
        try {
            File file = testField.getValue();
            tempService.deleteTempFileOrDir(file);

        } catch (Exception e) {
            throw new UnitilsException("Error deleting temp file for field '" + testField.getName() + "'", e);
        }
    }
}
