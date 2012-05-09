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
import org.unitils.core.UnitilsException;
import org.unitils.io.FieldAnnotationListenerTestableAdapter;
import org.unitils.io.annotation.TempDir;
import org.unitils.io.annotation.TempFile;
import org.unitils.io.temp.TempService;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TempFileAnnotationHandlerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private FieldAnnotationListenerTestableAdapter<TempDir> tempFileAnnotationHandler;

    private Mock<TempService> tempServiceMock;
    @Dummy
    private File testFile;


    @Before
    public void initialize() {
        TempFileAnnotationHandler sut = new TempFileAnnotationHandler(tempServiceMock.getMock(), false);
        tempFileAnnotationHandler = new FieldAnnotationListenerTestableAdapter<TempDir>(sut);
    }


    @Test
    public void defaultValues() {
        DefaultValuesTestClass testObject = new DefaultValuesTestClass();
        tempServiceMock.returns(testFile).createTempFile(DefaultValuesTestClass.class.getName() + "-defaultValues.tmp");

        tempFileAnnotationHandler.beforeTestSetUp(testObject, "defaultValues", "tempFile", null);
        assertSame(testFile, testObject.tempFile);
    }

    @Test
    public void fileNameSpecified() {
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.returns(testFile).createTempFile("tempFile.tmp");

        tempFileAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "tempFile", null);
        assertSame(testFile, testObject.tempFile);
    }


    @Test
    public void exception() {
        NullPointerException exception = new NullPointerException();
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.raises(exception).createTempFile(null);

        try {
            tempFileAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "tempFile", null);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertNull(testObject.tempFile);
        }
    }


    @Test(expected = UnitilsException.class)
    public void targetFieldIsNotAFile() {
        InvalidTargetTestClass testObject = new InvalidTargetTestClass();
        tempServiceMock.returns(testFile).createTempFile("tempFile.tmp");

        tempFileAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "properties", null);
    }


    protected static class DefaultValuesTestClass {

        @TempFile
        protected File tempFile;

        public void doNothing() {
            //Do Nothing
        }

        public void defaultValues() {
            //Do Nothing
        }
    }

    protected static class FileNameSpecifiedTestClass {

        @TempFile("tempFile.tmp")
        protected File tempFile;

        public void doNothing() {
            //Do Nothing
        }
    }

    protected static class InvalidTargetTestClass {

        @TempFile("tempFile.tmp")
        protected Properties properties;

        public void doNothing() {
            //Do Nothing
        }
    }
}
