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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.FileContent;
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.mock.Mock;

import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileContentAnnotationHandlerTest extends UnitilsJUnit4 {

    /* Tested object */
    private FileContentAnnotationHandler fileContentAnnotationHandler;

    private Mock<FileContentReader> fileContentReaderMock;
    private Properties testProperties = new Properties();


    @Before
    public void initialize() {
        fileContentAnnotationHandler = new FileContentAnnotationHandler(fileContentReaderMock.getMock());
    }


    @Test
    public void defaultValues() {
        DefaultValuesTestClass testObject = new DefaultValuesTestClass();
        fileContentReaderMock.returns(testProperties).readFileContent(isNull(String.class), Properties.class, isNull(String.class), DefaultValuesTestClass.class);

        fileContentAnnotationHandler.beforeTestSetUp(testObject, null);
        assertSame(testProperties, testObject.properties);
    }

    @Test
    public void fileNameSpecified() {
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        fileContentReaderMock.returns(testProperties).readFileContent("fileName", Properties.class, isNull(String.class), FileNameSpecifiedTestClass.class);

        fileContentAnnotationHandler.beforeTestSetUp(testObject, null);
        assertSame(testProperties, testObject.properties);
    }

    @Test
    public void encodingSpecified() {
        EncodingSpecifiedTestClass testObject = new EncodingSpecifiedTestClass();
        fileContentReaderMock.returns(testProperties).readFileContent(isNull(String.class), Properties.class, "encoding", EncodingSpecifiedTestClass.class);

        fileContentAnnotationHandler.beforeTestSetUp(testObject, null);
        assertSame(testProperties, testObject.properties);
    }

    @Test
    public void noAnnotations() {
        NoAnnotationTestClass testObject = new NoAnnotationTestClass();

        fileContentAnnotationHandler.beforeTestSetUp(testObject, null);
        fileContentReaderMock.assertNotInvoked().readFileContent(isNull(String.class), Properties.class, isNull(String.class), NoAnnotationTestClass.class);
    }

    @Test
    public void exception() {
        NullPointerException exception = new NullPointerException();
        DefaultValuesTestClass testObject = new DefaultValuesTestClass();
        fileContentReaderMock.raises(exception).readFileContent(null, Properties.class, null, DefaultValuesTestClass.class);

        try {
            fileContentAnnotationHandler.beforeTestSetUp(testObject, null);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertNull(testObject.properties);
        }
    }


    private static class DefaultValuesTestClass {

        @FileContent
        protected Properties properties;
    }

    private static class FileNameSpecifiedTestClass {

        @FileContent("fileName")
        protected Properties properties;
    }

    private static class EncodingSpecifiedTestClass {

        @FileContent(encoding = "encoding")
        protected Properties properties;
    }

    private static class NoAnnotationTestClass {

        protected Properties properties;
    }
}
