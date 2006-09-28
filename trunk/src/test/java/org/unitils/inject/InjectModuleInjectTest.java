package org.unitils.inject;

import junit.framework.TestCase;
import org.unitils.inject.annotation.Inject;
import org.unitils.inject.annotation.TestedObject;

/**
 * @author Filip Neven
 */
public class InjectModuleInjectTest extends TestCase {

    InjectModule injectModule = new InjectModule();

    private TestInjectExplicitTarget testInjectExplicitTarget = new TestInjectExplicitTarget();
    private TestInjectAnnotatedTarget testInjectAnnotatedTarget = new TestInjectAnnotatedTarget();

    protected void setUp() throws Exception {
        super.setUp();
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

        @Inject(target = "injectOn", property="toInject")
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

        @Inject(property="toInject")
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
