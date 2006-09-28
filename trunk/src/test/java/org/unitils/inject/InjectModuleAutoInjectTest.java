package org.unitils.inject;

import junit.framework.TestCase;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.annotation.AutoInject;

/**
 * TODO Test both field and setter injection
 */
public class InjectModuleAutoInjectTest extends TestCase {

    InjectModule injectModule = new InjectModule();

    private TestAutoInjectExplicitTarget testAutoInjectExplicitTarget = new TestAutoInjectExplicitTarget();

    private TestAutoInjectAnnotatedTarget testAutoInjectAnnotatedTarget = new TestAutoInjectAnnotatedTarget();

    private TestAutoInjectTargetFieldIsSuperType testAutoInjectTargetFieldIsSuperType = new TestAutoInjectTargetFieldIsSuperType();

    private TestAutoInjectToMostSpecificallyTypedField testAutoInjectToMostSpecificallyTypedField = new TestAutoInjectToMostSpecificallyTypedField();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAutoInject_explicitTarget() {
        injectModule.injectObjects(testAutoInjectExplicitTarget);
        assertSame(testAutoInjectExplicitTarget.getToInject(), testAutoInjectExplicitTarget.getInjectOn().getToInject());
    }

    public void testAutoInject_annotatedTargets() {
        injectModule.injectObjects(testAutoInjectAnnotatedTarget);
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn1().getToInject());
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn2().getToInject());
    }

    public void testAutoInject_targetFieldIsSuperType() {
        injectModule.injectObjects(testAutoInjectTargetFieldIsSuperType);
        assertSame(testAutoInjectTargetFieldIsSuperType.getToInject(), testAutoInjectTargetFieldIsSuperType.getInjectOn().getToInject());
    }

    public void testAutoInject_injectToMostSpecificallyTypedField() {
        injectModule.injectObjects(testAutoInjectToMostSpecificallyTypedField);
        assertSame(testAutoInjectToMostSpecificallyTypedField.getToInjectSuper(), testAutoInjectToMostSpecificallyTypedField.getInjectOn().getToInjectSuper());
        assertSame(testAutoInjectToMostSpecificallyTypedField.getToInjectSub(), testAutoInjectToMostSpecificallyTypedField.getInjectOn().getToInjectSub());
    }

    public class TestAutoInjectExplicitTarget {

        @AutoInject(target = "injectOn")
        private ToInjectSuper toInjectSuper;

        private InjectOn injectOn;

        public TestAutoInjectExplicitTarget() {
            toInjectSuper = new ToInjectSuper();
            injectOn = new InjectOn();
        }

        public ToInjectSuper getToInject() {
            return toInjectSuper;
        }

        public InjectOn getInjectOn() {
            return injectOn;
        }

    }

    public class TestAutoInjectAnnotatedTarget {

        @AutoInject
        private ToInjectSuper toInjectSuper;

        @TestedObject
        private InjectOn injectOn1;

        @TestedObject
        private InjectOn injectOn2;

        public TestAutoInjectAnnotatedTarget() {
            toInjectSuper = new ToInjectSuper();
            injectOn1 = new InjectOn();
            injectOn2 = new InjectOn();
        }

        public ToInjectSuper getToInject() {
            return toInjectSuper;
        }

        public InjectOn getInjectOn1() {
            return injectOn1;
        }

        public InjectOn getInjectOn2() {
            return injectOn2;
        }

    }

    public class TestAutoInjectTargetFieldIsSuperType {

        @AutoInject
        private ToInjectSub toInject;

        @TestedObject
        private InjectOn injectOn;

        public TestAutoInjectTargetFieldIsSuperType() {
            toInject = new ToInjectSub();
            injectOn = new InjectOn();
        }

        public ToInjectSub getToInject() {
            return toInject;
        }

        public InjectOn getInjectOn() {
            return injectOn;
        }
    }

    public class TestAutoInjectToMostSpecificallyTypedField {

        @AutoInject
        private ToInjectSuper toInjectSuper;

        @AutoInject
        private ToInjectSub toInjectSub;

        @TestedObject
        private InjectOnSuperSub injectOn;

        public TestAutoInjectToMostSpecificallyTypedField() {
            toInjectSuper = new ToInjectSuper();
            toInjectSub = new ToInjectSub();
            injectOn = new InjectOnSuperSub();
        }

        public ToInjectSuper getToInjectSuper() {
            return toInjectSuper;
        }

        public ToInjectSub getToInjectSub() {
            return toInjectSub;
        }

        public InjectOnSuperSub getInjectOn() {
            return injectOn;
        }
    }

    public class ToInjectSuper {

    }

    public class ToInjectSub extends ToInjectSuper {

    }

    public class InjectOn {

        private ToInjectSuper toInject;

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public void setToInject(ToInjectSuper toInject) {
            this.toInject = toInject;
        }

    }

    public class InjectOnSuperSub {

        private ToInjectSuper toInjectSuper;

        private ToInjectSub toInjectSub;

        public ToInjectSuper getToInjectSuper() {
            return toInjectSuper;
        }

        public void setToInjectSuper(ToInjectSuper toInjectSuper) {
            this.toInjectSuper = toInjectSuper;
        }

        public ToInjectSub getToInjectSub() {
            return toInjectSub;
        }

        public void setToInjectSub(ToInjectSub toInjectSub) {
            this.toInjectSub = toInjectSub;
        }

    }

}
