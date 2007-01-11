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

/**
 * Test for the static injection behavior of the {@link InjectModule}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoStaticTest extends TestCase {

    /* Tested object */
    private InjectModule injectModule;

    private TestInjectIntoStatic_simpleSetter testInjectIntoStatic_simpleSetter = new TestInjectIntoStatic_simpleSetter();
    private TestInjectIntoStatic_simpleField testInjectIntoStatic_simpleField = new TestInjectIntoStatic_simpleField();
    private TestInjectIntoStatic_compositeSetter testInjectIntoStatic_compositeSetter = new TestInjectIntoStatic_compositeSetter();
    private TestInjectIntoStatic_compositeField testInjectIntoStatic_compositeField = new TestInjectIntoStatic_compositeField();


    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        injectModule = new InjectModule();
        injectModule.init(configuration);
    }


    public void testInject_simpleSetter() {
        injectModule.injectObjects(testInjectIntoStatic_simpleSetter);
        assertSame(testInjectIntoStatic_simpleSetter.toInject, InjectOnStatic.getToInject());
    }


    public void testInject_simpleField() {
        injectModule.injectObjects(testInjectIntoStatic_simpleField);
        assertSame(testInjectIntoStatic_simpleField.toInject, InjectOnStatic.getToInjectField());
    }

    public void testInject_compositeSetter() {
        injectModule.injectObjects(testInjectIntoStatic_compositeSetter);
        assertSame(testInjectIntoStatic_compositeSetter.toInject, InjectOnStatic.getTestObject().getToInject());
    }


    public void testInject_compositeField() {
        injectModule.injectObjects(testInjectIntoStatic_compositeField);
        assertSame(testInjectIntoStatic_compositeField.toInject, InjectOnStatic.getTestObjectField().getToInject());
    }


    public class TestInjectIntoStatic_simpleSetter {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectIntoStatic_simpleSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }


    public class TestInjectIntoStatic_simpleField {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "toInjectField")
        private InjectOnStatic.ToInject toInject;

        public TestInjectIntoStatic_simpleField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }


    public class TestInjectIntoStatic_compositeSetter {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "testObject.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectIntoStatic_compositeSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }


    public class TestInjectIntoStatic_compositeField {

        @InjectIntoStatic(target = InjectOnStatic.class, property = "testObjectField.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectIntoStatic_compositeField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

}
