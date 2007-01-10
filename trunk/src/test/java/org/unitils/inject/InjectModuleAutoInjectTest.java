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
import org.unitils.inject.annotation.AutoInject;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.util.PropertyAccess;

/**
 * Test for the auto injection behavior of the {@link InjectModule}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
@SuppressWarnings({"UnusedDeclaration"})
public class InjectModuleAutoInjectTest extends TestCase {

    /* Tested object */
    private InjectModule injectModule;

    /* Objects that represent 'unit test classes', containing objects that should be injected on other objects. */
    private TestAutoInjectExplicitTarget testAutoInjectExplicitTarget = new TestAutoInjectExplicitTarget();
    private TestAutoInjectFieldAccess testAutoInjectFieldAccess = new TestAutoInjectFieldAccess();
    private TestAutoInjectAnnotatedTarget testAutoInjectAnnotatedTarget = new TestAutoInjectAnnotatedTarget();
    private TestAutoInject_targetPropertyIsSuperType testAutoInject_targetPropertyIsSuperType = new TestAutoInject_targetPropertyIsSuperType();
    private TestAutoInject_targetPropertyIsSuperType_fieldAccess testAutoInject_targetPropertyIsSuperType_fieldAccess = new TestAutoInject_targetPropertyIsSuperType_fieldAccess();
    private TestAutoInjectToMostSpecificallyTypedProperty testAutoInjectToMostSpecificallyTypedProperty = new TestAutoInjectToMostSpecificallyTypedProperty();
    private TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess testAutoInjectToMostSpecificallyTypedProperty_fieldAccess = new TestAutoInjectToMostSpecificallyTypedProperty_fieldAccess();
    private TestAutoInject_targetPropertyOnSuperClass testAutoInject_targetPropertyOnSuperClass = new TestAutoInject_targetPropertyOnSuperClass();
    private TestAutoInject_targetPropertyOnSuperClass_fieldAccess testAutoInject_targetPropertyOnSuperClass_fieldAccess = new TestAutoInject_targetPropertyOnSuperClass_fieldAccess();


    /**
     * Initializes the test and test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        Configuration configuration = new ConfigurationLoader().loadConfiguration();

        injectModule = new InjectModule();
        injectModule.init(configuration);
    }


    /**
     * Tests auto injection in case the target is explicitly specified
     */
    public void testAutoInject_explicitTarget() {
        injectModule.injectObjects(testAutoInjectExplicitTarget);
        assertSame(testAutoInjectExplicitTarget.getToInject(), testAutoInjectExplicitTarget.getInjectOn().getToInject());
    }


    /**
     * Tests auto injection in case of field access
     */
    public void testAutoInject_fieldAccess() {
        injectModule.injectObjects(testAutoInjectFieldAccess);
        assertSame(testAutoInjectFieldAccess.getToInject(), testAutoInjectFieldAccess.getInjectOnField().getToInject());
    }


    /**
     * Multiple fields are annotated with @TestedObject. Tests wether the objects are injected to all of these fields
     */
    public void testAutoInject_annotatedTargets() {
        injectModule.injectObjects(testAutoInjectAnnotatedTarget);
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn1().getToInject());
        assertSame(testAutoInjectAnnotatedTarget.getToInject(), testAutoInjectAnnotatedTarget.getInjectOn2().getToInject());
    }


    /**
     * Tests the case when the target field is a supertype of the injected object, and no more specific field exists.
     */
    public void testAutoInject_targetFieldIsSuperType() {
        injectModule.injectObjects(testAutoInject_targetPropertyIsSuperType);
        assertSame(testAutoInject_targetPropertyIsSuperType.getToInject(), testAutoInject_targetPropertyIsSuperType.getInjectOn().getToInject());
    }


    /**
     * Tests the case when the target field is a supertype of the injected object, and no more specific field exists,
     * using field access
     */
    public void testAutoInject_targetFieldIsSuperType_fieldAccess() {
        injectModule.injectObjects(testAutoInject_targetPropertyIsSuperType_fieldAccess);
        assertSame(testAutoInject_targetPropertyIsSuperType_fieldAccess.getToInject(), testAutoInject_targetPropertyIsSuperType_fieldAccess.getInjectOn().getToInject());
    }


    /**
     * Tests the case where target fields of both the object's type and a super type exists. The object should be injected
     * into the most specific type
     */
    public void testAutoInject_injectToMostSpecificallyTypedField() {
        injectModule.injectObjects(testAutoInjectToMostSpecificallyTypedProperty);
        assertSame(testAutoInjectToMostSpecificallyTypedProperty.getToInjectSuper(), testAutoInjectToMostSpecificallyTypedProperty.getInjectOn().getToInjectSuper());
        assertSame(testAutoInjectToMostSpecificallyTypedProperty.getToInjectSub(), testAutoInjectToMostSpecificallyTypedProperty.getInjectOn().getToInjectSub());
    }


    /**
     * Tests the case where target fields of both the object's type and a super type exists. The object should be injected
     * into the most specific type. Field access is used.
     */
    public void testAutoInject_injectToMostSpecificallyTypedField_fieldAccess() {
        injectModule.injectObjects(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess);
        assertSame(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getToInjectSuper(), testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSuper());
        assertSame(testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getToInjectSub(), testAutoInjectToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSub());
    }


    /**
     * Tests the case where the target property of the object to inject on belongs to the superclass
     */
    public void testAutoInject_targetPropertyOnSuperClass() {
        injectModule.injectObjects(testAutoInject_targetPropertyOnSuperClass);
        assertSame(testAutoInject_targetPropertyOnSuperClass.getToInject(), testAutoInject_targetPropertyOnSuperClass.getInjectOn().getToInject());
    }


    /**
     * Tests the case where the target property of the object to inject on belongs to the superclass, using field access
     */
    public void testAutoInject_targetPropertyOnSuperClass_fieldAccess() {
        injectModule.injectObjects(testAutoInject_targetPropertyOnSuperClass_fieldAccess);
        assertSame(testAutoInject_targetPropertyOnSuperClass_fieldAccess.getToInject(), testAutoInject_targetPropertyOnSuperClass_fieldAccess.getInjectOn().getToInject());
    }


    public class TestAutoInjectExplicitTarget {

        @AutoInject(target = "injectOn", propertyAccess = PropertyAccess.SETTER)
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

        @AutoInject(propertyAccess = PropertyAccess.FIELD)
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

        @AutoInject(propertyAccess = PropertyAccess.SETTER)
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


    public class TestAutoInject_targetPropertyIsSuperType {

        @AutoInject(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOn injectOn;

        public TestAutoInject_targetPropertyIsSuperType() {
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


    public class TestAutoInject_targetPropertyIsSuperType_fieldAccess {

        @AutoInject(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOnField injectOn;

        public TestAutoInject_targetPropertyIsSuperType_fieldAccess() {
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

        @AutoInject(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInjectSuper;
        @AutoInject(propertyAccess = PropertyAccess.SETTER)
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

        @AutoInject(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSuper toInjectSuper;
        @AutoInject(propertyAccess = PropertyAccess.FIELD)
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


    public class TestAutoInject_targetPropertyOnSuperClass {

        @AutoInject(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn_subClass injectOn;

        public TestAutoInject_targetPropertyOnSuperClass() {
            toInject = new ToInjectSuper();
            injectOn = new InjectOn_subClass();
        }

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public InjectOn_subClass getInjectOn() {
            return injectOn;
        }
    }


    public class TestAutoInject_targetPropertyOnSuperClass_fieldAccess {

        @AutoInject(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn_subClass injectOn;

        public TestAutoInject_targetPropertyOnSuperClass_fieldAccess() {
            toInject = new ToInjectSuper();
            injectOn = new InjectOn_subClass();
        }

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public InjectOn_subClass getInjectOn() {
            return injectOn;
        }
    }


    /**
     * Object to inject, superclass
     */
    public class ToInjectSuper {
    }


    /**
     * Object to inject, subclass
     */
    public class ToInjectSub extends ToInjectSuper {
    }


    /**
     * Object to inject into
     */
    public class InjectOn {

        private ToInjectSuper toInject;

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public void setToInject(ToInjectSuper toInject) {
            this.toInject = toInject;
        }
    }


    /**
     * Object to inject using field access
     */
    public class InjectOnField {

        private ToInjectSuper toInject;

        public ToInjectSuper getToInject() {
            return toInject;
        }
    }

    /**
     * Object to inject on. Contains properties of a super- and a subtype.
     */
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


    /**
     * Object to inject on using field access. Contains properties of a super- and a subtype.
     */
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


    /**
     * Superclass of object to inject into
     */
    public class InjectOn_superClass {

        private ToInjectSuper toInject;

        public ToInjectSuper getToInject() {
            return toInject;
        }

        public void setToInject(ToInjectSuper toInject) {
            this.toInject = toInject;
        }

    }


    /**
     * Superclass of object to inject into
     */
    public class InjectOn_subClass extends InjectOn_superClass {
    }

}
