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
public class TempFileFieldAnnotationListenerAfterTestMethodTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempFileFieldAnnotationListener tempFileFieldAnnotationListener;

    private Mock<TempService> tempServiceMock;
    private Mock<TestField> testFieldMock;

    @Dummy
    private File testFile;


    @Before
    public void initialize() {
        tempFileFieldAnnotationListener = new TempFileFieldAnnotationListener(tempServiceMock.getMock(), true);

        testFieldMock.returns("fieldName").getName();
        testFieldMock.returns(testFile).getValue();
    }


    @Test
    public void cleanup() {
        tempFileFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);

        tempServiceMock.assertInvoked().deleteTempFileOrDir(testFile);
    }

    @Test
    public void nullFile() {
        testFieldMock.onceReturns(null).getValue();

        tempFileFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);

        tempServiceMock.assertInvoked().deleteTempFileOrDir(isNull(File.class));
    }

    @Test
    public void cleanupDisabled() {
        tempFileFieldAnnotationListener = new TempFileFieldAnnotationListener(tempServiceMock.getMock(), false);

        tempFileFieldAnnotationListener.afterTestMethod(null, null, null, null);

        tempServiceMock.assertNotInvoked().deleteTempFileOrDir(null);
    }

    @Test
    public void exception() {
        tempServiceMock.raises(new UnitilsException("reason")).deleteTempFileOrDir(null);

        try {
            tempFileFieldAnnotationListener.afterTestMethod(null, testFieldMock.getMock(), null, null);
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Error deleting temp file for field 'fieldName'\n" +
                    "Reason: reason", e.getMessage());
        }
    }
}
