/*
 * Copyright 2006 the original author or authors.
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
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Test for {@link ModuleUtils}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ModuleUtilsTest extends TestCase {


    /* Test configuration, containing test enum default values */
    private Configuration configuration = new PropertiesConfiguration();


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation1.ModuleUtilsTest.TestEnum.default", "VALUE1");
        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.ModuleUtilsTest.TestEnum.default", "VALUE2");
    }


    /**
     * Test the loading of the default values.
     */
    public void testGetAnnotationEnumDefaults() {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = ModuleUtils.getValueReplaceDefault(TestAnnotation1.class, TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        TestEnum enumValue2 = ModuleUtils.getValueReplaceDefault(TestAnnotation2.class, TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE2, enumValue2);
    }


    /**
     * Test the loading of the default values. TestAnnotation2 has no default value configured.
     * Default for test enum in annotation 1 should still be loaded correctly.
     */
    public void testGetAnnotationEnumDefaults_defaultNotFound() {

        configuration.clearProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.ModuleUtilsTest.TestEnum.default");

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = ModuleUtils.getValueReplaceDefault(TestAnnotation1.class, TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        try {
            ModuleUtils.getValueReplaceDefault(TestAnnotation2.class, TestEnum.DEFAULT, result);
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

        configuration.clearProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.ModuleUtilsTest.TestEnum.default");
        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.ModuleUtilsTest.OtherTestEnum.default", "VALUE2");

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class, TestAnnotation2.class);

        TestEnum enumValue1 = ModuleUtils.getValueReplaceDefault(TestAnnotation1.class, TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
        try {
            ModuleUtils.getValueReplaceDefault(TestAnnotation2.class, TestEnum.DEFAULT, result);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test the loading of the default values. TestAnnotation2 has a default value configured that does not exist.
     * An exception should be raised.
     */
    public void testGetAnnotationEnumDefaults_defaultValueNotFound() {

        configuration.setProperty("ModuleUtilsTest.TestModule.ModuleUtilsTest.TestAnnotation2.ModuleUtilsTest.TestEnum.default", "xxxxxxx");

        try {
            ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class, TestAnnotation2.class);
            fail("Expected UnitilsException");

        } catch (UnitilsException e) {
            // expected
        }
    }


    /**
     * Test the loading of the default values for no annotations. An empty map should have been returned.
     */
    public void testGetAnnotationEnumDefaults_noAnnotations() {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration);

        assertTrue(result.isEmpty());
    }


    /**
     * Test get default enum replaced by default value.
     */
    public void testGetValueReplaceDefault() {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class);

        TestEnum enumValue1 = ModuleUtils.getValueReplaceDefault(TestAnnotation1.class, TestEnum.DEFAULT, result);
        assertSame(TestEnum.VALUE1, enumValue1);
    }


    /**
     * Test get enum value not replaced by default value.
     */
    public void testGetValueReplaceDefault_normalValue() {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class);

        TestEnum enumValue1 = ModuleUtils.getValueReplaceDefault(TestAnnotation1.class, TestEnum.VALUE2, result);
        assertSame(TestEnum.VALUE2, enumValue1);
    }


    /**
     * Test get enum value not replaced by default value. TestAnnotation2 has no default loaded.
     */
    public void testGetValueReplaceDefault_noDefaultValueFound() {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = ModuleUtils.getAnnotationEnumDefaults(TestModule.class, configuration, TestAnnotation1.class);

        try {
            ModuleUtils.getValueReplaceDefault(TestAnnotation2.class, TestEnum.DEFAULT, result);
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

        public void init(Configuration configuration) {
        }

        public TestListener createTestListener() {
            return null;
        }
    }
}
