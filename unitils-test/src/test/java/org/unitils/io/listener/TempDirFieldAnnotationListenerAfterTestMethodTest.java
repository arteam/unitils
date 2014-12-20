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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.TestField;
import org.unitils.core.UnitilsException;
import org.unitils.io.annotation.TempDir;
import org.unitils.io.temp.TempService;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TempDirFieldAnnotationListenerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempDirFieldAnnotationListener tempDirFieldAnnotationListener;

    private Mock<TempService> tempServiceMock;
    private Mock<TestField> testFieldMock;

    @Dummy
    private File testDir;


    @Before
    public void initialize() {
        tempDirFieldAnnotationListener = new TempDirFieldAnnotationListener(tempServiceMock.getMock(), true);

        testFieldMock.returns("fieldName").getName();
        testFieldMock.returns(testDir).getValue();
    }


    @Test
    public void cleanup() {
        tempDirFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);

        tempServiceMock.assertInvoked().deleteTempFileOrDir(testDir);
    }

    @Test
    public void nullDirectory() {
        testFieldMock.onceReturns(null).getValue();

        tempDirFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);

        tempServiceMock.assertInvoked().deleteTempFileOrDir(isNull(File.class));
    }

    @Test
    public void cleanupDisabled() {
        tempDirFieldAnnotationListener = new TempDirFieldAnnotationListener(tempServiceMock.getMock(), false);

        tempDirFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);

        tempServiceMock.assertNotInvoked().deleteTempFileOrDir(null);
    }

    @Test
    public void exception() {
        tempServiceMock.raises(new UnitilsException("reason")).deleteTempFileOrDir(null);

        try {
            tempDirFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Error deleting temp dir for field 'fieldName'\n" +
                    "Reason: reason", e.getMessage());
        }
    }


    private static class TestableClass {

        @TempDir("tempDir")
        protected File tempDir;
    }

}
