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
import org.unitils.io.annotation.TempDir;
import org.unitils.io.temp.TempService;
import org.unitils.io.FieldAnnotationListenerTestableAdapter;
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
public class TempDirAnnotationHandlerBeforeTestSetUpTest extends UnitilsJUnit4 {


    private FieldAnnotationListenerTestableAdapter<TempDir> tempDirAnnotationHandler;

    private Mock<TempService> tempServiceMock;

    @Dummy
    private File testDir;


    @Before
    public void initialize() {
        TempDirAnnotationHandler handler = new TempDirAnnotationHandler(tempServiceMock.getMock(), false);
        tempDirAnnotationHandler = new FieldAnnotationListenerTestableAdapter<TempDir>(handler);
    }


    @Test
    public void defaultValues() {
        DefaultValuesTestClass testObject = new DefaultValuesTestClass();
        tempServiceMock.returns(testDir).createTempDir(DefaultValuesTestClass.class.getName() + "-doNothing");

        tempDirAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "tempDir", null);

        tempServiceMock.assertInvoked();
        assertSame(testDir, testObject.tempDir);
    }


    @Test
    public void fileNameSpecified() {
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.returns(testDir).createTempDir("tempDir");

        tempDirAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "tempDir", null);

        assertSame(testDir, testObject.tempDir);
    }


    @Test
    public void exception() {
        NullPointerException exception = new NullPointerException();
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.raises(exception).createTempDir(null);

        try {
            tempDirAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "tempDir", null);

            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
            assertNull(testObject.tempDir);
        }
    }


    @Test(expected = UnitilsException.class)
    public void targetFieldIsNotAFile() {
        InvalidTargetTestClass testObject = new InvalidTargetTestClass();
        tempServiceMock.returns(testDir).createTempDir("tempDir");

        tempDirAnnotationHandler.beforeTestSetUp(testObject, "doNothing", "properties", null);

    }


    protected static class DefaultValuesTestClass {

        @TempDir
        protected File tempDir;

        public void doNothing() {
            // Do Nothing
        }
    }

    protected static class FileNameSpecifiedTestClass {

        @TempDir("tempDir")
        protected File tempDir;

        public void doNothing() {
            // Do Nothing
        }
    }

    protected static class InvalidTargetTestClass {

        @TempDir("tempDir")
        protected Properties properties;

        public void doNothing() {
            // Do Nothing
        }

    }
}
