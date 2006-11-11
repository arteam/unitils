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
import org.unitils.inject.annotation.InjectStatic;

/**
 * @author Filip Neven
 */
public class InjectModuleInjectStaticTest extends TestCase {

    InjectModule injectModule;

    private TestInjectStatic_simpleSetter testInjectStatic_simpleSetter = new TestInjectStatic_simpleSetter();
    private TestInjectStatic_simpleField testInjectStatic_simpleField = new TestInjectStatic_simpleField();
    private TestInjectStatic_compositeSetter testInjectStatic_compositeSetter = new TestInjectStatic_compositeSetter();
    private TestInjectStatic_compositeField testInjectStatic_compositeField = new TestInjectStatic_compositeField();

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        injectModule = new InjectModule();
        injectModule.init(configuration);
    }

    public void testInject_simpleSetter() {
        injectModule.injectObjects(testInjectStatic_simpleSetter);
        assertSame(testInjectStatic_simpleSetter.toInject, InjectOnStatic.getToInject());
    }

    public void testInject_simpleField() {
        injectModule.injectObjects(testInjectStatic_simpleField);
        assertSame(testInjectStatic_simpleField.toInject, InjectOnStatic.getToInjectField());
    }

    public void testInject_compositeSetter() {
        injectModule.injectObjects(testInjectStatic_compositeSetter);
        assertSame(testInjectStatic_compositeSetter.toInject, InjectOnStatic.getTestObject().getToInject());
    }

    public void testInject_compositeField() {
        injectModule.injectObjects(testInjectStatic_compositeField);
        assertSame(testInjectStatic_compositeField.toInject, InjectOnStatic.getTestObjectField().getToInject());
    }

    public class TestInjectStatic_simpleSetter {

        @InjectStatic(target = InjectOnStatic.class, property = "toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_simpleSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_simpleField {

        @InjectStatic(target = InjectOnStatic.class, property = "toInjectField")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_simpleField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_compositeSetter {

        @InjectStatic(target = InjectOnStatic.class, property = "testObject.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_compositeSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_compositeField {

        @InjectStatic(target = InjectOnStatic.class, property = "testObjectField.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_compositeField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

}
