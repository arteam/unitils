/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.util;

import junit.framework.TestCase;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import static org.unitils.util.ModuleUtils.getAnnotationPropertyDefaults;
import static org.unitils.util.ModuleUtils.getEnumValueReplaceDefault;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

/**
 * Test for {@link ModuleUtils}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ModuleUtilsTest extends TestCase {


    /* Test configuration, containing test enum default values */
    private Properties configuration = new Properties();


    /**
     * Initializes the test fixture.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation1.testEnum.default", "VALUE1");
        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.testEnum.default", "VALUE2");
    }


    /**
     * Test the loading of the default values.
     */
    public void testGetAnnotationEnumDefaults() {
        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class,
                configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = getEnumValueReplaceDefault(TestAnnotation1.class, "testEnum", TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        TestEnum enumValue2 = getEnumValueReplaceDefault(TestAnnotation2.class, "testEnum", TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE2, enumValue2);
    }


    /**
     * Test the loading of the default values. TestAnnotation2 has no default value configured.
     * Default for test enum in annotation 1 should still be loaded correctly.
     */
    public void testGetAnnotationEnumDefaults_defaultNotFound() {
        configuration.remove("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.testEnum.default");

        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class,
                configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = getEnumValueReplaceDefault(TestAnnotation1.class, "testEnum", TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        try {
            getEnumValueReplaceDefault(TestAnnotation2.class, "testEnum", TestEnum.DEFAULT, result);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test the loading of the default values. TestAnnotation2 has a default value configured but for different enum.
     * Default for test enum in annotation 1 should still be loaded correctly.
     */
    public void testGetAnnotationEnumDefaults_defaultWrongKey() {
        configuration.remove("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.testEnum.default");
        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.otherTestEnum.default", "VALUE2");

        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class,
                configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = getEnumValueReplaceDefault(TestAnnotation1.class, "testEnum", TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        try {
            getEnumValueReplaceDefault(TestAnnotation2.class, "testEnum", TestEnum.DEFAULT, result);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test the loading of the default values for no annotations. An empty map should have been returned.
     */
    public void testGetAnnotationEnumDefaults_noAnnotations() {
        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class, configuration);
        assertTrue(result.isEmpty());
    }


    /**
     * Test get default enum replaced by default value.
     */
    public void testGetEnumValueReplaceDefault() {
        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class, configuration, TestAnnotation1.class);

        TestEnum enumValue1 = getEnumValueReplaceDefault(TestAnnotation1.class, "testEnum", TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
    }


    /**
     * Test get enum value not replaced by default value.
     */
    public void testGetEnumValueReplaceDefault_normalValue() {
        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class, configuration, TestAnnotation1.class);

        TestEnum enumValue1 = getEnumValueReplaceDefault(TestAnnotation1.class, "testEnum", TestEnum.VALUE2, result);
        assertSame(TestEnum.VALUE2, enumValue1);
    }


    /**
     * Test get enum value not replaced by default value. TestAnnotation2 has no default loaded.
     */
    public void testGetEnumValueReplaceDefault_noDefaultValueFound() {
        Map<Class<? extends Annotation>, Map<String, String>> result = getAnnotationPropertyDefaults(TestModule.class, configuration, TestAnnotation1.class);
        try {
            getEnumValueReplaceDefault(TestAnnotation2.class, "testEnum", TestEnum.DEFAULT, result);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test annotation containing an enum value and a primitive value.
     */
    private static @interface TestAnnotation1 {

        public int testPrimitive() default 0;

        public TestEnum testEnum() default TestEnum.DEFAULT;

    }


    /**
     * Test annotation containing an enum value.
     */
    private static @interface TestAnnotation2 {

        public TestEnum testEnum() default TestEnum.DEFAULT;

    }


    /**
     * Enumeration with a default value.
     */
    private static enum TestEnum {

        DEFAULT, VALUE1, VALUE2
    }


    /**
     * Dummy test module.
     */
    private static class TestModule implements Module {

        public void init(Properties configuration) {
        }

        public TestListener createTestListener() {
            return null;
        }
    }
}
