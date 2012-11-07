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
import org.unitils.io.annotation.FileContent;
import org.unitils.io.filecontent.FileContentReader;
import org.unitils.mock.Mock;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.reflect.Annotations;

import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class FileContentFieldAnnotationListenerTest extends UnitilsJUnit4 {

    private FileContentFieldAnnotationListener fileContentFieldAnnotationListener;

    private Mock<FileContentReader> fileContentReaderMock;
    private Mock<TestInstance> testInstanceMock;
    private Mock<TestField> testFieldMock;
    private Mock<Annotations<FileContent>> annotationsMock;

    private Properties testProperties = new Properties();

    private FileContent annotation1;
    private FileContent annotation2;
    private FileContent annotation3;


    @Before
    public void initialize() throws Exception {
        fileContentFieldAnnotationListener = new FileContentFieldAnnotationListener(fileContentReaderMock.getMock());

        annotation1 = MyClass.class.getDeclaredField("field1").getAnnotation(FileContent.class);
        annotation2 = MyClass.class.getDeclaredField("field2").getAnnotation(FileContent.class);
        annotation3 = MyClass.class.getDeclaredField("field3").getAnnotation(FileContent.class);
    }


    @Test
    public void defaultValues() {
        annotationsMock.returns(annotation1).getAnnotationWithDefaults();
        fileContentReaderMock.returns(testProperties).readFileContent("", Properties.class, "", MyClass.class);

        fileContentFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testProperties);
    }

    @Test
    public void fileNameSpecified() {
        annotationsMock.returns(annotation2).getAnnotationWithDefaults();
        fileContentReaderMock.returns(testProperties).readFileContent("fileName", Properties.class, "", MyClass.class);

        fileContentFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testProperties);
    }

    @Test
    public void encodingSpecified() {
        annotationsMock.returns(annotation3).getAnnotationWithDefaults();
        fileContentReaderMock.returns(testProperties).readFileContent("", Properties.class, "encoding", MyClass.class);

        fileContentFieldAnnotationListener.beforeTestSetUp(testInstanceMock.getMock(), testFieldMock.getMock(), annotationsMock.getMock());

        testFieldMock.assertInvoked().setValue(testProperties);
    }


    private static class MyClass {

        @FileContent
        private Properties field1;

        @FileContent("fileName")
        private Properties field2;

        @FileContent(encoding = "encoding")
        private Properties field3;

        private Properties field4;
    }
}
