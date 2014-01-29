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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TempDirAnnotationHandlerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempDirAnnotationHandler tempDirAnnotationHandler;

    private Mock<TempService> tempServiceMock;
    @Dummy
    private File testDir;


    @Before
    public void initialize() {
        tempDirAnnotationHandler = new TempDirAnnotationHandler(tempServiceMock.getMock(), true);
    }


    @Test
    public void cleanup() {
        TestClass testObject = new TestClass();
        testObject.tempDir = testDir;

        tempDirAnnotationHandler.afterTestMethod(testObject, null, null);

        tempServiceMock.assertInvoked().deleteTempFileOrDir(testDir);
    }

    @Test
    public void cleanupDisabled() {
        TestClass testObject = new TestClass();
        testObject.tempDir = testDir;

        tempDirAnnotationHandler = new TempDirAnnotationHandler(tempServiceMock.getMock(), false);
        tempDirAnnotationHandler.afterTestMethod(testObject, null, null);

        tempServiceMock.assertNotInvoked().deleteTempFileOrDir(null);
    }

    @Test
    public void noAnnotations() {
        NoAnnotationTestClass testObject = new NoAnnotationTestClass();

        tempDirAnnotationHandler.afterTestMethod(testObject, null, null);
        tempServiceMock.assertNotInvoked().deleteTempFileOrDir(null);
    }

    @Test
    public void exception() {
        NullPointerException exception = new NullPointerException();
        TestClass testObject = new TestClass();
        tempServiceMock.raises(exception).deleteTempFileOrDir(null);

        try {
            tempDirAnnotationHandler.afterTestMethod(testObject, null, null);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertSame(exception, e.getCause());
        }
    }


    private static class TestClass {

        @TempDir("tempDir")
        protected File tempDir;
    }

    private static class NoAnnotationTestClass {

        protected File tempDir;
    }

}
