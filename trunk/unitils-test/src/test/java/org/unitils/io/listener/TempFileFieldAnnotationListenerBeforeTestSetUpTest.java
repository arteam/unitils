/*
 * Copyright 2012,  Unitils.org
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
import org.unitils.io.annotation.TempFile;
import org.unitils.io.temp.TempService;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TempFileFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempFileFieldAnnotationListener tempFileFieldAnnotationListener;

    private Mock<TempService> tempServiceMock;
    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<TempFile>> annotationsMock;
    @Dummy
    private File testFile;

    private TempFile annotation1;
    private TempFile annotation2;
    private TempFile annotation3;


    @Before
    public void initialize() throws Exception {
        tempFileFieldAnnotationListener = new TempFileFieldAnnotationListener(tempServiceMock.getMock(), true);

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(TempFile.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(TempFile.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(TempFile.class);
    }


    @Test
    public void defaultValues() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        tempServiceMock.returns(testFile).createTempFile("");

        tempFileFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testFile);
    }

    @Test
    public void fileNameSpecified() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        tempServiceMock.returns(testFile).createTempFile("tempFile.tmp");

        tempFileFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testFile);
    }

    @Test
    public void targetFieldIsNotAFile() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        tempServiceMock.returns(testFile).createTempFile("");

        tempFileFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        fail("todo");
    }


    private static class MyClass {

        @TempFile
        private File field1;

        @TempFile("tempFile.tmp")
        private File field2;

        @TempFile
        private Properties field3;

        private File field4;
    }
}
