package org.unitils.inject;

import junit.framework.TestCase;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.annotation.AutoInject;

public class InjectModuleAutoInjectTest extends TestCase {

    InjectModule injectModule = new InjectModule();

    private TestAutoInjectExplicitTarget testAutoInjectExplicitTarget = new TestAutoInjectExplicitTarget();
    private TestAutoInjectFieldAccess testAutoInjectFieldAccess = new TestAutoInjectFieldAccess();
    private TestAutoInjectAnnotatedTarget testAutoInjectAnnotatedTarget = new TestAutoInjectAnnotatedTarget();
    private TestAutoInject_targetFieldIsSuperType testAutoInject_targetFieldIsSuperType = new TestAutoInject_targetFieldIsSuperType();
    private TestAutoInject_targetFieldIsSuperType_fieldAccess testAutoInject_targetFieldIsSuperType_fieldAccess = new TestAutoInject_targetFieldIsSuperType_fieldAccess();
    private TestAutoInjectToMostSpecificallyTypedProperty testAutoInjectToMostSpecificallyTypedProperty = new TestAutoInjectToMostSpecificallyTypedProperty();
    private TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess testAutoInjectToMostSpecificallyTypedProperty_fieldAccess = new TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAutoInject_explicitTarget() {
        injectModule.injectObjects(testAutoInjectExplicitTarget);
        assertSame(testAutoInjectExplicitTarget.getToInject(), testAutoInjectExplicitTarget.getInjectOn().getToInject());
    }

    public void testAutoInject_fieldAccess() {
        injectModule.injectObjects(testAutoInjectFieldAccess);
        assertSame(testAutoInjectFieldAccess.getToInject(), testAutoInjectFieldAccess.getInjectOnField().getToInject());
    }

    public void testAutoInject_annotatedTargets() {
        injectModule.injectObjects(testAutoInjectAnnotatedTarget);
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn1().getToInject());
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn2().getToInject());
    }

    public void testAutoInject_targetFieldIsSuperType() {
        injectModule.injectObjects(testAutoInject_targetFieldIsSuperType);
        assertSame(testAutoInject_targetFieldIsSuperType.getToInject(), testAutoInject_targetFieldIsSuperType.getInjectOn().getToInject());
    }

    public void testAutoInject_targetFieldIsSuperType_fieldAccess() {
        injectModule.injectObjects(testAutoInject_targetFieldIsSuperType_fieldAccess);
        assertSame(testAutoInject_targetFieldIsSuperType_fieldAccess.getToInject(), testAutoInject_targetFieldIsSuperType_fieldAccess.getInjectOn().getToInject());
    }

    public void testAutoInject_injectToMostSpecificallyTypedField() {
        injectModule.injectObjects(testAutoInjectToMostSpecificallyTypedProperty);
        assertSame(testAutoInjectToMostSpecificallyTypedProperty.getToInjectSuper(), testAutoInjectToMostSpecificallyTypedProperty.getInjectOn().getToInjectSuper());
        assertSame(testAutoInjectToMostSpecificallyTypedProperty.getToInjectSub(), testAutoInjectToMostSpecificallyTypedProperty.getInjectOn().getToInjectSub());
    }

    public void testAutoInject_injectToMostSpecificallyTypedField_fieldAccess() {
        injectModule.injectObjects(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess);
        assertSame(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getToInjectSuper(), testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSuper());
        assertSame(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getToInjectSub(), testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSub());
    }

    public class TestAutoInjectExplicitTarget {

        @AutoInject(target = "injectOn", propertyAccessType = AutoInject.PropertyAccessType.SETTER)
        private ToInjectSuper toInject;
        private InjectOn injectOn;

        public TestAutoInjectExplicitTarget() {
            toInject = new ToInjectSuper();
            injectOn = new InjectOn();
        }

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public InjectOn getInjectOn() {
            return injectOn;
        }

    }

    public class TestAutoInjectFieldAccess {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.FIELD)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOnField injectOnField;

        public TestAutoInjectFieldAccess() {
            toInject = new ToInjectSuper();
            injectOnField = new InjectOnField();
        }

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public InjectOnField getInjectOnField() {
            return injectOnField;
        }

    }

    public class TestAutoInjectAnnotatedTarget {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.SETTER)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn injectOn1;
        @TestedObject
        private InjectOn injectOn2;

        public TestAutoInjectAnnotatedTarget() {
            toInject = new ToInjectSuper();
            injectOn1 = new InjectOn();
            injectOn2 = new InjectOn();
        }

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public InjectOn getInjectOn1() {
            return injectOn1;
        }

        public InjectOn getInjectOn2() {
            return injectOn2;
        }

    }

    public class TestAutoInject_targetFieldIsSuperType {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.SETTER)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOn injectOn;

        public TestAutoInject_targetFieldIsSuperType() {
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

    public class TestAutoInject_targetFieldIsSuperType_fieldAccess {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.FIELD)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOnField injectOn;

        public TestAutoInject_targetFieldIsSuperType_fieldAccess() {
            toInject = new ToInjectSub();
            injectOn = new InjectOnField();
        }

        public ToInjectSub getToInject() {
            return toInject;
        }

        public InjectOnField getInjectOn() {
            return injectOn;
        }
    }

    public class TestAutoInjectToMostSpecificallyTypedProperty {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.SETTER)
        private ToInjectSuper toInjectSuper;
        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.SETTER)
        private ToInjectSub toInjectSub;
        @TestedObject
        private InjectOnSuperSub injectOn;

        public TestAutoInjectToMostSpecificallyTypedProperty() {
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

    public class TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess {

        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.FIELD)
        private ToInjectSuper toInjectSuper;
        @AutoInject(propertyAccessType = AutoInject.PropertyAccessType.FIELD)
        private ToInjectSub toInjectSub;
        @TestedObject
        private InjectOnSuperSubFieldAccess injectOn;

        public TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess() {
            toInjectSuper = new ToInjectSuper();
            toInjectSub = new ToInjectSub();
            injectOn = new InjectOnSuperSubFieldAccess();
        }

        public ToInjectSuper getToInjectSuper() {
            return toInjectSuper;
        }

        public ToInjectSub getToInjectSub() {
            return toInjectSub;
        }

        public InjectOnSuperSubFieldAccess getInjectOn() {
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

    public class InjectOnField {

        private ToInjectSuper toInject;

        public ToInjectSuper getToInject() {
            return toInject;
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

    public class InjectOnSuperSubFieldAccess {

        private ToInjectSuper toInjectSuper;
        private ToInjectSub toInjectSub;

        public ToInjectSuper getToInjectSuper() {
            return toInjectSuper;
        }

        public ToInjectSub getToInjectSub() {
            return toInjectSub;
        }
    }

}
