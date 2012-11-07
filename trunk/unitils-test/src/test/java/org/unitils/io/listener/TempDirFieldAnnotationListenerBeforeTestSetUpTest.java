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
import org.unitils.io.annotation.TempDir;
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
public class TempDirFieldAnnotationListenerBeforeTestSetUpTest extends UnitilsJUnit4 {

    /* Tested object */
    private TempDirFieldAnnotationListener tempDirFieldAnnotationListener;

    private Mock<TempService> tempServiceMock;
    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<TempDir>> annotationsMock;

    @Dummy
    private File testDir;

    private TempDir annotation1;
    private TempDir annotation2;
    private TempDir annotation3;


    @Before
    public void initialize() throws Exception {
        tempDirFieldAnnotationListener = new TempDirFieldAnnotationListener(tempServiceMock.getMock(), true);

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(TempDir.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(TempDir.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(TempDir.class);
    }


    @Test
    public void defaultValues() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        tempServiceMock.returns(testDir).createTempDir("");

        tempDirFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testDir);
    }


    @Test
    public void fileNameSpecified() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        tempServiceMock.returns(testDir).createTempDir("fileName");

        tempDirFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testDir);
    }

    @Test
    public void targetFieldIsNotAFile() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        tempServiceMock.returns(testDir).createTempDir("");

        tempDirFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        fail("todo");
    }


    private static class MyClass {

        @TempDir
        private File field1;

        @TempDir("fileName")
        private File field2;

        @TempDir
        private Properties field3;

        private File field4;
    }
}
