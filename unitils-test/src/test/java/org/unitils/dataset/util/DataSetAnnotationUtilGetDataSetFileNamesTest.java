/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.dataset.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.ConfigurationLoader;
import org.unitils.dataset.annotation.DataSet;
import org.unitils.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * Test class for getting the file names from the DataSet annotation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DataSetAnnotationUtilGetDataSetFileNamesTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSetAnnotationUtil dataSetAnnotationUtil;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        dataSetAnnotationUtil = new DataSetAnnotationUtil(configuration);
    }


    @Test
    public void annotationOnMethod() throws Exception {
        Class<?> testClass = NoClassLevelAnnotation.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "annotation"), "xml");
        assertDataSetFileName("NoClassLevelAnnotation.xml", result);

    }

    @Test
    public void dataSetFileNameSpecifiedInAnnotation() throws Exception {
        Class<?> testClass = ClassLevelAnnotation.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "annotationWithFileName"), "xml");
        assertLenientEquals(asList("CustomDataSet.xml"), result);
    }

    @Test
    public void classLevelAnnotationOverridden() throws Exception {
        Class<?> testClass = NoClassLevelAnnotation.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "annotationWithFileName"), "xml");
        assertLenientEquals(asList("CustomDataSet.xml"), result);
    }

    @Test
    public void dataSetFileNameSpecifiedInAnnotationOnClassLevel() throws Exception {
        Class<?> testClass = ClassLevelAnnotationWithFileName.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "noAnnotation"), "xml");
        assertLenientEquals(asList("CustomDataSet.xml"), result);
    }

    @Test
    public void noAnnotationFound() throws Exception {
        Class<?> testClass = NoClassLevelAnnotation.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "noAnnotation"), "xml");
        assertNull(result);
    }

    @Test
    public void annotationOnSubclass() throws Exception {
        Class<?> testClass = AnnotationOnSubClass.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "noAnnotation"), "xml");
        assertDataSetFileName("AnnotationOnSubClass.xml", result);

    }

    @Test
    public void annotationOnSuperClass() throws Exception {
        Class<?> testClass = AnnotationOnSuperClass.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "noAnnotation"), "xml");
        assertDataSetFileName("AnnotationOnSuperClass.xml", result);
    }

    @Test
    public void multipleFileNames() throws Exception {
        Class<?> testClass = ClassLevelAnnotation.class;
        List<String> result = dataSetAnnotationUtil.getDataSetFileNames(testClass, getMethod(testClass, "multipleFileNames"), "xml");
        assertLenientEquals(asList("dataSet1.xml", "dataSet2.xml"), result);
    }


    private Method getMethod(Class<?> testClass, String methodName) {
        return ReflectionUtils.getMethod(testClass, methodName, false);
    }

    private void assertDataSetFileName(String expectedDataSetFileName, List<String> actualDataSetFileNames) {
        String className = getClass().getSimpleName() + "$" + expectedDataSetFileName;
        assertLenientEquals(asList(className), actualDataSetFileNames);
    }


    /**
     * Test class with a class level dataset
     */
    @DataSet
    public class ClassLevelAnnotation {

        public void noAnnotation() {
        }

        @DataSet("CustomDataSet.xml")
        public void annotationWithFileName() {
        }

        @DataSet
        public void annotation() {
        }

        @DataSet({"dataSet1.xml", "dataSet2.xml"})
        public void multipleFileNames() {
        }
    }


    /**
     * Test class without a class level dataset
     */
    public class NoClassLevelAnnotation {

        @DataSet
        public void annotation() {
        }

        @DataSet("CustomDataSet.xml")
        public void annotationWithFileName() {
        }

        public void noAnnotation() {
        }
    }


    @DataSet("CustomDataSet.xml")
    public class ClassLevelAnnotationWithFileName {

        public void noAnnotation() {
        }

        @DataSet
        public void annotation() {
        }
    }

    @DataSet
    public class AnnotationOnSubClass extends NoClassLevelAnnotation {
    }

    public class AnnotationOnSuperClass extends ClassLevelAnnotation {
    }

}