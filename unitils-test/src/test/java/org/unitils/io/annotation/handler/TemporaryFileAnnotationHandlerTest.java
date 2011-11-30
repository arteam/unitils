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


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.io.TemporaryFile.TemporaryFileUtil;
import org.unitils.io.annotation.TemporaryFile;
import org.unitils.io.annotation.TemporaryFolder;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public class TemporaryFileAnnotationHandlerTest extends UnitilsJUnit4 {

    @TestedObject
    private TemporaryFileAnnotationHandler annotationHandler;

    private Mock<TemporaryFileUtil> fileUtil;

    @Dummy
    private File returnDummy;

    @Before
    public void setUp() {
        annotationHandler = new TemporaryFileAnnotationHandler(fileUtil.getMock(), new File("target/test-classes"), true);
    }

    @Test
    public void beforeTestSetupDefaultFile() {

        Method method = ReflectionUtils.getMethod(this.getClass(), "beforeTestSetupDefaultFile", false, null);
        fileUtil.returns(returnDummy).createTemporaryFile("org.unitils.io.annotation.handler.TemporaryFileAnnotationHandlerTest$DefaultTemporaryFilebeforeTestSetupDefaultFile.tmp");
        DefaultTemporaryFile testClassFake = new DefaultTemporaryFile();

        annotationHandler.beforeTestSetUp(testClassFake, method);

        fileUtil.assertInvoked().createTemporaryFile("org.unitils.io.annotation.handler.TemporaryFileAnnotationHandlerTest$DefaultTemporaryFilebeforeTestSetupDefaultFile.tmp");
        Assert.assertSame(returnDummy, testClassFake.content);
    }

    @Test
    public void beforeTestSetupCustomFile() {

        Method method = ReflectionUtils.getMethod(this.getClass(), "beforeTestSetupCustomFile", false, null);
        fileUtil.returns(returnDummy).createTemporaryFile("a-custom-file-name.tmp");
        CustomTemporaryFile testClassFake = new CustomTemporaryFile();

        annotationHandler.beforeTestSetUp(testClassFake, method);

        fileUtil.assertInvoked().createTemporaryFile("a-custom-file-name.tmp");
        Assert.assertSame(returnDummy, testClassFake.content);
    }


    @Test
    public void beforeTestSetupDefaultFolder() {

        Method method = ReflectionUtils.getMethod(this.getClass(), "beforeTestSetupDefaultFolder", false, null);
        fileUtil.returns(returnDummy).createTemporaryFolder("org.unitils.io.annotation.handler.TemporaryFileAnnotationHandlerTest$DefaultTemporaryFolderbeforeTestSetupDefaultFolder");
        DefaultTemporaryFolder testClassFake = new DefaultTemporaryFolder();

        annotationHandler.beforeTestSetUp(testClassFake, method);

        fileUtil.assertInvoked().createTemporaryFolder("org.unitils.io.annotation.handler.TemporaryFileAnnotationHandlerTest$DefaultTemporaryFolderbeforeTestSetupDefaultFolder");
        Assert.assertSame(returnDummy, testClassFake.content);
    }

    @Test
    public void beforeTestSetupCustomFolder() {

        Method method = ReflectionUtils.getMethod(this.getClass(), "beforeTestSetupCustomFolder", false, null);
        fileUtil.returns(returnDummy).createTemporaryFolder("a-custom-folder-name");
        CustomTemporaryFolder testClassFake = new CustomTemporaryFolder();

        annotationHandler.beforeTestSetUp(testClassFake, method);

        fileUtil.assertInvoked().createTemporaryFolder("a-custom-folder-name");
        Assert.assertSame(returnDummy, testClassFake.content);
    }

    private static class DefaultTemporaryFile {
        @TemporaryFile
        File content;
    }


    private static class DefaultTemporaryFolder {
        @TemporaryFolder
        File content;
    }

    private static class CustomTemporaryFile {
        @TemporaryFile("a-custom-file-name.tmp")
        File content;
    }

    private static class CustomTemporaryFolder {
        @TemporaryFolder("a-custom-folder-name")
        File content;
    }


}
