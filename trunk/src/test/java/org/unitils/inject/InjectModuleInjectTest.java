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
import org.unitils.inject.annotation.Inject;
import org.unitils.inject.annotation.TestedObject;

/**
 * @author Filip Neven
 */
public class InjectModuleInjectTest extends TestCase {

    private InjectModule injectModule;

    private TestInjectExplicitTarget testInjectExplicitTarget = new TestInjectExplicitTarget();
    private TestInjectAnnotatedTarget testInjectAnnotatedTarget = new TestInjectAnnotatedTarget();

    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        injectModule = new InjectModule();
        injectModule.init(configuration);
    }

    public void testInject_explicitTarget() {
        injectModule.injectObjects(testInjectExplicitTarget);
        assertSame(testInjectExplicitTarget.getToInject(), testInjectExplicitTarget.getInjectOn().getTestObject1());
    }

    public void testInject_annotatedTargets() {
        injectModule.injectObjects(testInjectAnnotatedTarget);
        assertSame(testInjectAnnotatedTarget.getToInject(), testInjectAnnotatedTarget.getInjectOn1().getTestObject1());
        assertSame(testInjectAnnotatedTarget.getToInject(), testInjectAnnotatedTarget.getInjectOn2().getTestObject1());
    }

    public class TestInjectExplicitTarget {

        @Inject(target = "injectOn", property = "toInject")
        private ToInject toInject;

        private InjectOn injectOn;

        public TestInjectExplicitTarget() {
            toInject = new ToInject();
            injectOn = new InjectOn();
        }

        public ToInject getToInject() {
            return toInject;
        }

        public InjectOn getInjectOn() {
            return injectOn;
        }

    }

    public class TestInjectAnnotatedTarget {

        @Inject(property = "toInject")
        private ToInject toInject;

        @TestedObject
        private InjectOn injectOn1;

        @TestedObject
        private InjectOn injectOn2;

        public TestInjectAnnotatedTarget() {
            toInject = new ToInject();
            injectOn1 = new InjectOn();
            injectOn2 = new InjectOn();
        }

        public ToInject getToInject() {
            return toInject;
        }

        public InjectOn getInjectOn1() {
            return injectOn1;
        }

        public InjectOn getInjectOn2() {
            return injectOn2;
        }

    }

    public class ToInject {

    }

    public class InjectOn {

        private ToInject toInject;

        public void setTestObject1(ToInject toInject) {
            this.toInject = toInject;
        }

        public ToInject getTestObject1() {
            return toInject;
        }

    }

}
