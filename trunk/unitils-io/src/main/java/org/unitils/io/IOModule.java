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

import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.io.annotation.handler.FileContentAnnotationHandler;
import org.unitils.io.annotation.handler.TempDirAnnotationHandler;
import org.unitils.io.annotation.handler.TempFileAnnotationHandler;
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.io.filecontent.FileContentReaderFactory;
import org.unitils.io.temp.TempService;
import org.unitils.io.temp.TempServiceFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;
import static org.unitils.util.PropertyUtils.getBoolean;

/**
 * Will listen for the @FileContent annotation in tests. The content of the file
 * specified in the annotation will be loaded in the property. A property
 * annotation with {@link @FileContent} should always be a String
 * <br>
 * Example:
 * <p/>
 * <pre>
 * &#064;FileContent(location = &quot;be/smals/file.txt&quot;)
 * private String fileContent;
 * </pre>
 *
 * @author Jeroen Horema
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */
public class IOModule implements Module {

    protected static final String CLEANUP_AFTER_TEST = "IOModule.temp.cleanupAfterTest";

    /* The configuration of Unitils */
    protected Properties configuration;
    protected FileContentReader fileContentReader;
    protected TempService tempService;


    public void init(Properties configuration) {
        this.configuration = configuration;
        fileContentReader = createFileContentReader();
        tempService = createTempService();
    }

    public void afterInit() {
    }

    public TestListener getTestListener() {
        boolean cleanUpAfterTest = getBoolean(CLEANUP_AFTER_TEST, configuration);

        FileContentAnnotationHandler fileContentAnnotationHandler = new FileContentAnnotationHandler(fileContentReader);
        TempFileAnnotationHandler tempFileAnnotationHandler = new TempFileAnnotationHandler(tempService, cleanUpAfterTest);
        TempDirAnnotationHandler tempDirAnnotationHandler = new TempDirAnnotationHandler(tempService, cleanUpAfterTest);
        return new IOTestListener(fileContentAnnotationHandler, tempFileAnnotationHandler, tempDirAnnotationHandler);
    }


    public <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Class<?> testClass) {
        return fileContentReader.readFileContent(fileName, targetType, encoding, testClass);
    }

    public File createTempFile(String fileName) {
        return tempService.createTempFile(fileName);
    }

    public File createTempDir(String dirName) {
        return tempService.createTempDir(dirName);
    }

    public void deleteTempFileOrDir(File fileOrDir) {
        tempService.deleteTempFileOrDir(fileOrDir);
    }


    protected FileContentReader createFileContentReader() {
        FileContentReaderFactory fileContentReaderFactory = getInstanceOf(FileContentReaderFactory.class, configuration);
        return fileContentReaderFactory.createFileContentReader(configuration);
    }

    protected TempService createTempService() {
        TempServiceFactory tempServiceFactory = getInstanceOf(TempServiceFactory.class, configuration);
        return tempServiceFactory.createTempService(configuration);
    }


    protected class IOTestListener extends TestListener {

        protected FileContentAnnotationHandler fileContentAnnotationHandler;
        protected TempFileAnnotationHandler tempFileAnnotationHandler;
        protected TempDirAnnotationHandler tempDirAnnotationHandler;

        public IOTestListener(FileContentAnnotationHandler fileContentAnnotationHandler, TempFileAnnotationHandler tempFileAnnotationHandler, TempDirAnnotationHandler tempDirAnnotationHandler) {
            this.fileContentAnnotationHandler = fileContentAnnotationHandler;
            this.tempFileAnnotationHandler = tempFileAnnotationHandler;
            this.tempDirAnnotationHandler = tempDirAnnotationHandler;
        }

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            tempDirAnnotationHandler.beforeTestSetUp(testObject, testMethod);
            tempFileAnnotationHandler.beforeTestSetUp(testObject, testMethod);
            fileContentAnnotationHandler.beforeTestSetUp(testObject, testMethod);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
            tempFileAnnotationHandler.afterTestMethod(testObject, testMethod, testThrowable);
            tempDirAnnotationHandler.afterTestMethod(testObject, testMethod, testThrowable);
        }
    }
}
