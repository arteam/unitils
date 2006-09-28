package org.unitils.inject;

import org.unitils.inject.annotation.InjectStatic;
import junit.framework.TestCase;

/**
 * @author Filip Neven
 */
public class InjectModuleInjectStaticTest extends TestCase {

    InjectModule injectModule = new InjectModule();

    private TestInjectStatic_simpleSetter testInjectStatic_simpleSetter = new TestInjectStatic_simpleSetter();
    private TestInjectStatic_simpleField testInjectStatic_simpleField = new TestInjectStatic_simpleField();
    private TestInjectStatic_compositeSetter testInjectStatic_compositeSetter = new TestInjectStatic_compositeSetter();
    private TestInjectStatic_compositeField testInjectStatic_compositeField = new TestInjectStatic_compositeField();

    protected void setUp() throws Exception {
        super.setUp();
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

        @InjectStatic(target = InjectOnStatic.class, property="toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_simpleSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_simpleField {

        @InjectStatic(target = InjectOnStatic.class, property="toInjectField")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_simpleField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_compositeSetter {

        @InjectStatic(target = InjectOnStatic.class, property="testObject.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_compositeSetter() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

    public class TestInjectStatic_compositeField {

        @InjectStatic(target = InjectOnStatic.class, property="testObjectField.toInject")
        private InjectOnStatic.ToInject toInject;

        public TestInjectStatic_compositeField() {
            toInject = new InjectOnStatic.ToInject();
        }
    }

}
