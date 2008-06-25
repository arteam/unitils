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
package org.unitils.inject;

import static org.junit.Assert.assertSame;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.ConfigurationLoader;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoTest {

    InjectModule injectModule;

    TestInjectIntoExplicitTarget testInjectIntoExplicitTarget = new TestInjectIntoExplicitTarget();
    TestInjectIntoAnnotatedTarget testInjectIntoAnnotatedTarget = new TestInjectIntoAnnotatedTarget();

    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();

        injectModule = new InjectModule();
        injectModule.init(configuration);
    }

    @Test
    public void testInjectInto_explicitTarget() {
        injectModule.injectObjects(testInjectIntoExplicitTarget);
        assertSame(testInjectIntoExplicitTarget.getToInject(), testInjectIntoExplicitTarget.getInjectOn().getTestObject1());
    }

    @Test
    public void testInjectInto_annotatedTargets() {
        injectModule.injectObjects(testInjectIntoAnnotatedTarget);
        assertSame(testInjectIntoAnnotatedTarget.getToInject(), testInjectIntoAnnotatedTarget.getInjectOn1().getTestObject1());
        assertSame(testInjectIntoAnnotatedTarget.getToInject(), testInjectIntoAnnotatedTarget.getInjectOn2().getTestObject1());
    }

    public class TestInjectIntoExplicitTarget {

        @InjectInto(target = "injectOn", property = "toInject")
        private ToInject toInject;

        private InjectOn injectOn;

        public TestInjectIntoExplicitTarget() {
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

    public class TestInjectIntoAnnotatedTarget {

        @InjectInto(property = "toInject")
        private ToInject toInject;

        @TestedObject
        private InjectOn injectOn1;

        @TestedObject
        private InjectOn injectOn2;

        public TestInjectIntoAnnotatedTarget() {
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
