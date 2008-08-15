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
package org.unitils.inject;

import static org.junit.Assert.assertSame;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.inject.annotation.InjectIntoStatic;

/**
 * Test for the static injection behavior of the {@link InjectModule}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoStaticTest {

    /* Tested object */
    InjectModule injectModule;

    TestInjectIntoStatic_simpleSetter testInjectIntoStatic_simpleSetter = new TestInjectIntoStatic_simpleSetter();
    TestInjectIntoStatic_simpleField testInjectIntoStatic_simpleField = new TestInjectIntoStatic_simpleField();
    TestInjectIntoStatic_compositeSetter testInjectIntoStatic_compositeSetter = new TestInjectIntoStatic_compositeSetter();
    TestInjectIntoStatic_compositeField testInjectIntoStatic_compositeField = new TestInjectIntoStatic_compositeField();


    /**
     * Initializes the test and test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        injectModule = new InjectModule();
        injectModule.init(configuration);
    }


    @Test
    public void testInject_simpleSetter() {
        injectModule.injectObjects(testInjectIntoStatic_simpleSetter);
        assertSame(testInjectIntoStatic_simpleSetter.toInject, InjectOnStatic.getToInject());
    }


    @Test
    public void testInject_simpleField() {
        injectModule.injectObjects(testInjectIntoStatic_simpleField);
        assertSame(testInjectIntoStatic_simpleField.toInject, InjectOnStatic.getToInjectField());
    }

    @Test
    public void testInject_compositeSetter() {
        injectModule.injectObjects(testInjectIntoStatic_compositeSetter);
        assertSame(testInjectIntoStatic_compositeSetter.toInject, InjectOnStatic.getTestObject().getToInject());
    }


    @Test
    public void testInject_compositeField() {
        injectModule.injectObjects(testInjectIntoStatic_compositeField);
        assertSame(testInjectIntoStatic_compositeField.toInject, InjectOnStatic.getTestObjectField().getToInject());
    }


    public class TestInjectIntoStatic_simpleSetter {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "toInject")
        private ToInject toInject;

        public TestInjectIntoStatic_simpleSetter() {
            toInject = new ToInject();
        }
    }


    public class TestInjectIntoStatic_simpleField {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "toInjectField")
        private ToInject toInject;

        public TestInjectIntoStatic_simpleField() {
            toInject = new ToInject();
        }
    }


    public class TestInjectIntoStatic_compositeSetter {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "testObject.toInject")
        private ToInject toInject;

        public TestInjectIntoStatic_compositeSetter() {
            toInject = new ToInject();
        }
    }


    public class TestInjectIntoStatic_compositeField {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "testObjectField.toInject")
        private ToInject toInject;

        public TestInjectIntoStatic_compositeField() {
            toInject = new ToInject();
        }
    }

    private static class InjectOnStatic {

        private static ToInject toInject;

        private static ToInject toInjectField;

        private static TestObject testObject;

        private static TestObject testObjectField;

        static {
            testObject = new TestObject();
            testObjectField = new TestObject();
        }

        public static void setToInject(ToInject toInject) {
            InjectOnStatic.toInject = toInject;
        }

        public static void setTestObject(TestObject testObject) {
            InjectOnStatic.testObject = testObject;
        }

        public static ToInject getToInject() {
            return toInject;
        }

        public static ToInject getToInjectField() {
            return toInjectField;
        }

        public static TestObject getTestObject() {
            return testObject;
        }

        public static TestObject getTestObjectField() {
            return testObjectField;
        }
    }

    public static class TestObject {

        private ToInject toInject;

        public void setToInject(ToInject toInject) {
            this.toInject = toInject;
        }

        public ToInject getToInject() {
            return toInject;
        }
    }

    public static class ToInject {
    }

}
