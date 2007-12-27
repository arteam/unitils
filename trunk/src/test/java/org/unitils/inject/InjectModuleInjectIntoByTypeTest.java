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
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.inject.util.PropertyAccess;

/**
 * Test for the auto injection behavior of the {@link InjectModule}
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class InjectModuleInjectIntoByTypeTest {

    /* Tested object */
    InjectModule injectModule;

    /* Objects that represent 'unit test classes', containing objects that should be injected on other objects. */
    TestInjectIntoByTypeExplicitTarget testInjectIntoByTypeExplicitTarget = new TestInjectIntoByTypeExplicitTarget();
    TestInjectIntoByTypeFieldAccess testInjectIntoByTypeFieldAccess = new TestInjectIntoByTypeFieldAccess();
    TestInjectIntoByTypeAnnotatedTarget testInjectIntoByTypeAnnotatedTarget = new TestInjectIntoByTypeAnnotatedTarget();
    TestInjectIntoByType_targetPropertyIsSuperType testInjectIntoByType_targetPropertyIsSuperType = new TestInjectIntoByType_targetPropertyIsSuperType();
    TestInjectIntoByType_targetPropertyIsSuperType_fieldAccess testInjectIntoByType_targetPropertyIsSuperType_fieldAccess = new TestInjectIntoByType_targetPropertyIsSuperType_fieldAccess();
    TestInjectIntoByTypeToMostSpecificallyTypedProperty testInjectIntoByTypeToMostSpecificallyTypedProperty = new TestInjectIntoByTypeToMostSpecificallyTypedProperty();
    TestInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess = new TestInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess();
    TestInjectIntoByType_targetPropertyOnSuperClass testInjectIntoByType_targetPropertyOnSuperClass = new TestInjectIntoByType_targetPropertyOnSuperClass();
    TestInjectIntoByType_targetPropertyOnSuperClass_fieldAccess testInjectIntoByType_targetPropertyOnSuperClass_fieldAccess = new TestInjectIntoByType_targetPropertyOnSuperClass_fieldAccess();


    /**
     * Initializes the test and test fixture.
     */
    @Before
    public void setUp() throws Exception {
        Properties configuration = new ConfigurationLoader().loadConfiguration();
        injectModule = new InjectModule();
        injectModule.init(configuration);
    }


    /**
     * Tests auto injection in case the target is explicitly specified
     */
    @Test
    public void testInjectIntoByType_explicitTarget() {
        injectModule.injectObjects(testInjectIntoByTypeExplicitTarget);
        assertSame(testInjectIntoByTypeExplicitTarget.getToInject(), testInjectIntoByTypeExplicitTarget.getInjectOn().getToInject());
    }


    /**
     * Tests auto injection in case of field access
     */
    @Test
    public void testInjectIntoByType_fieldAccess() {
        injectModule.injectObjects(testInjectIntoByTypeFieldAccess);
        assertSame(testInjectIntoByTypeFieldAccess.getToInject(), testInjectIntoByTypeFieldAccess.getInjectOnField().getToInject());
    }


    /**
     * Multiple fields are annotated with @TestedObject. Tests wether the objects are injected to all of these fields
     */
    @Test
    public void testInjectIntoByType_annotatedTargets() {
        injectModule.injectObjects(testInjectIntoByTypeAnnotatedTarget);
        assertSame(testInjectIntoByTypeAnnotatedTarget.getToInject(), testInjectIntoByTypeAnnotatedTarget.getInjectOn1().getToInject());
        assertSame(testInjectIntoByTypeAnnotatedTarget.getToInject(), testInjectIntoByTypeAnnotatedTarget.getInjectOn2().getToInject());
    }


    /**
     * Tests the case when the target field is a supertype of the injected object, and no more specific field exists.
     */
    @Test
    public void testInjectIntoByType_targetFieldIsSuperType() {
        injectModule.injectObjects(testInjectIntoByType_targetPropertyIsSuperType);
        assertSame(testInjectIntoByType_targetPropertyIsSuperType.getToInject(), testInjectIntoByType_targetPropertyIsSuperType.getInjectOn().getToInject());
    }


    /**
     * Tests the case when the target field is a supertype of the injected object, and no more specific field exists,
     * using field access
     */
    @Test
    public void testInjectIntoByType_targetFieldIsSuperType_fieldAccess() {
        injectModule.injectObjects(testInjectIntoByType_targetPropertyIsSuperType_fieldAccess);
        assertSame(testInjectIntoByType_targetPropertyIsSuperType_fieldAccess.getToInject(), testInjectIntoByType_targetPropertyIsSuperType_fieldAccess.getInjectOn().getToInject());
    }


    /**
     * Tests the case where target fields of both the object's type and a super type exists. The object should be injected
     * into the most specific type
     */
    @Test
    public void testInjectIntoByType_injectToMostSpecificallyTypedField() {
        injectModule.injectObjects(testInjectIntoByTypeToMostSpecificallyTypedProperty);
        assertSame(testInjectIntoByTypeToMostSpecificallyTypedProperty.getToInjectSuper(), testInjectIntoByTypeToMostSpecificallyTypedProperty.getInjectOn().getToInjectSuper());
        assertSame(testInjectIntoByTypeToMostSpecificallyTypedProperty.getToInjectSub(), testInjectIntoByTypeToMostSpecificallyTypedProperty.getInjectOn().getToInjectSub());
    }


    /**
     * Tests the case where target fields of both the object's type and a super type exists. The object should be injected
     * into the most specific type. Field access is used.
     */
    @Test
    public void testInjectIntoByType_injectToMostSpecificallyTypedField_fieldAccess() {
        injectModule.injectObjects(testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess);
        assertSame(testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess.getToInjectSuper(), testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSuper());
        assertSame(testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess.getToInjectSub(), testInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess.getInjectOn().getToInjectSub());
    }


    /**
     * Tests the case where the target property of the object to inject on belongs to the superclass
     */
    @Test
    public void testInjectIntoByType_targetPropertyOnSuperClass() {
        injectModule.injectObjects(testInjectIntoByType_targetPropertyOnSuperClass);
        assertSame(testInjectIntoByType_targetPropertyOnSuperClass.getToInject(), testInjectIntoByType_targetPropertyOnSuperClass.getInjectOn().getToInject());
    }


    /**
     * Tests the case where the target property of the object to inject on belongs to the superclass, using field access
     */
    @Test
    public void testInjectIntoByType_targetPropertyOnSuperClass_fieldAccess() {
        injectModule.injectObjects(testInjectIntoByType_targetPropertyOnSuperClass_fieldAccess);
        assertSame(testInjectIntoByType_targetPropertyOnSuperClass_fieldAccess.getToInject(), testInjectIntoByType_targetPropertyOnSuperClass_fieldAccess.getInjectOn().getToInject());
    }


    public class TestInjectIntoByTypeExplicitTarget {

        @InjectIntoByType(target = "injectOn", propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInject;
        private InjectOn injectOn;

        public TestInjectIntoByTypeExplicitTarget() {
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


    public class TestInjectIntoByTypeFieldAccess {

        @InjectIntoByType(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOnField injectOnField;

        public TestInjectIntoByTypeFieldAccess() {
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


    public class TestInjectIntoByTypeAnnotatedTarget {

        @InjectIntoByType(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn injectOn1;
        @TestedObject
        private InjectOn injectOn2;

        public TestInjectIntoByTypeAnnotatedTarget() {
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


    public class TestInjectIntoByType_targetPropertyIsSuperType {

        @InjectIntoByType(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOn injectOn;

        public TestInjectIntoByType_targetPropertyIsSuperType() {
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


    public class TestInjectIntoByType_targetPropertyIsSuperType_fieldAccess {

        @InjectIntoByType(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSub toInject;
        @TestedObject
        private InjectOnField injectOn;

        public TestInjectIntoByType_targetPropertyIsSuperType_fieldAccess() {
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


    public class TestInjectIntoByTypeToMostSpecificallyTypedProperty {

        @InjectIntoByType(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInjectSuper;
        @InjectIntoByType(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSub toInjectSub;
        @TestedObject
        private InjectOnSuperSub injectOn;

        public TestInjectIntoByTypeToMostSpecificallyTypedProperty() {
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


    public class TestInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess {

        @InjectIntoByType(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSuper toInjectSuper;
        @InjectIntoByType(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSub toInjectSub;
        @TestedObject
        private InjectOnSuperSubFieldAccess injectOn;

        public TestInjectIntoByTypeToMostSpecificallyTypedProperty_fieldAccess() {
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


    public class TestInjectIntoByType_targetPropertyOnSuperClass {

        @InjectIntoByType(propertyAccess = PropertyAccess.SETTER)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn_subClass injectOn;

        public TestInjectIntoByType_targetPropertyOnSuperClass() {
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


    public class TestInjectIntoByType_targetPropertyOnSuperClass_fieldAccess {

        @InjectIntoByType(propertyAccess = PropertyAccess.FIELD)
        private ToInjectSuper toInject;
        @TestedObject
        private InjectOn_subClass injectOn;

        public TestInjectIntoByType_targetPropertyOnSuperClass_fieldAccess() {
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
