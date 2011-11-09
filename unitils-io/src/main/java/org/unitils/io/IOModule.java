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
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.io.filecontent.FileContentReaderFactory;

import java.lang.reflect.Method;
import java.util.Properties;

import static org.unitils.core.util.ConfigUtils.getInstanceOf;

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

    /* The configuration of Unitils */
    protected Properties configuration;
    protected FileContentReader fileContentReader;


    public void init(Properties configuration) {
        this.configuration = configuration;
        fileContentReader = createFileContentReader();
    }

    public void afterInit() {
    }

    public TestListener getTestListener() {
        FileContentAnnotationHandler fileContentAnnotationHandler = new FileContentAnnotationHandler(fileContentReader);
        return new IOTestListener(fileContentAnnotationHandler);
    }


    public <T> T readFileContent(String fileName, Class<T> targetType, String encoding, Class<?> testClass) {
        return fileContentReader.readFileContent(fileName, targetType, encoding, testClass);
    }


    protected FileContentReader createFileContentReader() {
        FileContentReaderFactory fileContentReaderFactory = getInstanceOf(FileContentReaderFactory.class, configuration);
        return fileContentReaderFactory.createFileContentReader(configuration);
    }


    protected class IOTestListener extends TestListener {

        protected FileContentAnnotationHandler fileContentAnnotationHandler;

        public IOTestListener(FileContentAnnotationHandler fileContentAnnotationHandler) {
            this.fileContentAnnotationHandler = fileContentAnnotationHandler;
        }

        @Override
        public void beforeTestSetUp(Object testObject, Method testMethod) {
            fileContentAnnotationHandler.beforeTestSetUp(testObject, testMethod);
        }
    }
}
