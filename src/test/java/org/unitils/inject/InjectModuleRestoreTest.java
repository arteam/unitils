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
package org.unitils.inject;

import junit.framework.TestCase;
import org.apache.commons.configuration.Configuration;
import org.unitils.core.ConfigurationLoader;
import org.unitils.inject.annotation.InjectIntoStatic;
import static org.unitils.inject.util.Restore.*;

/**
 * Test for restoring values that where replaced during the static injection of the {@link InjectModule} after
 * a test was performed.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@SuppressWarnings({"UnusedDeclaration"})
public class InjectModuleRestoreTest extends TestCase {

    /* Tested object */
    private InjectModule injectModule;


    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();
        injectModule = new InjectModule();
        injectModule.init(configuration);

        SetterInjectionTarget.stringProperty = "original value";
        FieldInjectionTarget.stringProperty = "original value";
        SetterInjectionTarget.primitiveProperty = 111;
        FieldInjectionTarget.primitiveProperty = 111;
    }


    /**
     * Tests the default restore (=old value).
     */
    public void testRestore_defaultRestore() {
        injectModule.injectObjects(new TestStaticSetterDefaultRestore());
        injectModule.restoreStaticInjectedObjects();
        assertEquals("original value", SetterInjectionTarget.stringProperty);
        assertEquals("original value", FieldInjectionTarget.stringProperty);
    }


    /**
     * Tests the no restore.
     */
    public void testRestore_restoreNothing() {
        injectModule.injectObjects(new TestStaticSetterRestoreNothing());
        injectModule.restoreStaticInjectedObjects();
        assertEquals("injected value", SetterInjectionTarget.stringProperty);
        assertEquals("injected value", FieldInjectionTarget.stringProperty);
    }


    /**
     * Tests restoring the old value.
     */
    public void testRestore_restoreOldValue() {
        injectModule.injectObjects(new TestStaticSetterRestoreOldValue());
        injectModule.restoreStaticInjectedObjects();
        assertEquals("original value", SetterInjectionTarget.stringProperty);
        assertEquals("original value", FieldInjectionTarget.stringProperty);
    }

    /**
     * Tests restoring a null object value.
     */
    public void testRestore_restoreNull() {
        injectModule.injectObjects(new TestStaticSetterRestoreNull());
        injectModule.restoreStaticInjectedObjects();
        assertNull(SetterInjectionTarget.stringProperty);
        assertNull(FieldInjectionTarget.stringProperty);
    }


    /**
     * Tests restoring a 0 primitive value.
     */
    public void testRestore_restore0() {
        injectModule.injectObjects(new TestStaticSetterRestore0());
        injectModule.restoreStaticInjectedObjects();
        assertEquals(0, SetterInjectionTarget.primitiveProperty);
        assertEquals(0, FieldInjectionTarget.primitiveProperty);
    }


    /**
     * Test class containing a static setter and field injection and default restore (= old value).
     */
    private class TestStaticSetterDefaultRestore {

        @InjectIntoStatic(target = SetterInjectionTarget.class, property = "stringProperty")
        private String setterInject = "injected value";

        @InjectIntoStatic(target = FieldInjectionTarget.class, property = "stringProperty")
        private String fieldInject = "injected value";
    }

    /**
     * Test class containing a static setter and field injection and no restore.
     */
    private class TestStaticSetterRestoreNothing {

        @InjectIntoStatic(target = SetterInjectionTarget.class, property = "stringProperty", restore = NO_RESTORE)
        private String setterInject = "injected value";

        @InjectIntoStatic(target = FieldInjectionTarget.class, property = "stringProperty", restore = NO_RESTORE)
        private String fieldInject = "injected value";
    }

    /**
     * Test class containing a static setter and field injection and old value restore.
     */
    private class TestStaticSetterRestoreOldValue {

        @InjectIntoStatic(target = SetterInjectionTarget.class, property = "stringProperty", restore = OLD_VALUE)
        private String setterInject = "injected value";

        @InjectIntoStatic(target = FieldInjectionTarget.class, property = "stringProperty", restore = OLD_VALUE)
        private String fieldInject = "injected value";
    }

    /**
     * Test class containing a static setter and field injection and a null value restore.
     */
    private class TestStaticSetterRestoreNull {

        @InjectIntoStatic(target = SetterInjectionTarget.class, property = "stringProperty", restore = NULL_OR_0_VALUE)
        private String setterInject = "injected value";

        @InjectIntoStatic(target = FieldInjectionTarget.class, property = "stringProperty", restore = NULL_OR_0_VALUE)
        private String fieldInject = "injected value";
    }


    /**
     * Test class containing a static setter and field injection and primitive 0 value restore.
     */
    private class TestStaticSetterRestore0 {

        @InjectIntoStatic(target = SetterInjectionTarget.class, property = "primitiveProperty", restore = NULL_OR_0_VALUE)
        private int setterInject = 111;

        @InjectIntoStatic(target = FieldInjectionTarget.class, property = "primitiveProperty", restore = NULL_OR_0_VALUE)
        private int fieldInject = 111;
    }


    /**
     * Target for setter injection in the tests.
     */
    private static class SetterInjectionTarget {

        private static String stringProperty;

        private static int primitiveProperty;


        public static String getStringProperty() {
            return stringProperty;
        }

        public static void setStringProperty(String stringProperty) {
            SetterInjectionTarget.stringProperty = stringProperty;
        }

        public static int getPrimitiveProperty() {
            return primitiveProperty;
        }

        public static void setPrimitiveProperty(int primitiveProperty) {
            SetterInjectionTarget.primitiveProperty = primitiveProperty;
        }
    }

    /**
     * Target for field injection in the tests.
     */
    private static class FieldInjectionTarget {

        private static String stringProperty;

        private static int primitiveProperty;

    }
}
