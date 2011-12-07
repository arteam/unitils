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
import org.unitils.io.annotation.TempDir;
import org.unitils.io.temp.TempService;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.unitils.util.ReflectionUtils.getMethod;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TempDirAnnotationHandlerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempDirAnnotationHandler tempDirAnnotationHandler;

    private Mock<TempService> tempServiceMock;
    @Dummy
    private File testDir;


    @Before
    public void initialize() {
        tempDirAnnotationHandler = new TempDirAnnotationHandler(tempServiceMock.getMock(), false);
    }


    @Test
    public void defaultValues() {
        DefaultValuesTestClass testObject = new DefaultValuesTestClass();
        tempServiceMock.returns(testDir).createTempDir(DefaultValuesTestClass.class.getName() + "-defaultValues");

        tempDirAnnotationHandler.beforeTestSetUp(testObject, getMethod(getClass(), "defaultValues", false));
        assertSame(testDir, testObject.tempDir);
    }

    @Test
    public void fileNameSpecified() {
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.returns(testDir).createTempDir("tempDir");

        tempDirAnnotationHandler.beforeTestSetUp(testObject, null);
        assertSame(testDir, testObject.tempDir);
    }

    @Test
    public void noAnnotations() {
        NoAnnotationTestClass testObject = new NoAnnotationTestClass();

        tempDirAnnotationHandler.beforeTestSetUp(testObject, null);
        tempServiceMock.assertNotInvoked().createTempDir(null);
    }

    @Test
    public void exception() {
        NullPointerException exception = new NullPointerException();
        FileNameSpecifiedTestClass testObject = new FileNameSpecifiedTestClass();
        tempServiceMock.raises(exception).createTempDir(null);

        try {
            tempDirAnnotationHandler.beforeTestSetUp(testObject, null);
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

        tempDirAnnotationHandler.beforeTestSetUp(testObject, null);
    }


    private static class DefaultValuesTestClass {

        @TempDir
        protected File tempDir;
    }

    private static class FileNameSpecifiedTestClass {

        @TempDir("tempDir")
        protected File tempDir;
    }

    private static class NoAnnotationTestClass {

        protected File tempDir;
    }

    private static class InvalidTargetTestClass {

        @TempDir("tempDir")
        protected Properties properties;
    }
}
