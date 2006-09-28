package org.unitils.inject;

import org.unitils.inject.annotation.InjectStatic;
import junit.framework.TestCase;

/**
 * @author Filip Neven
 */
public class InjectModuleInjectStaticTest extends TestCase {

    InjectModule injectModule = new InjectModule();

    private TestInjectStatic testInjectStatic = new TestInjectStatic();

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testInject_explicitTarget() {
        injectModule.injectObjects(testInjectStatic);
        assertSame(testInjectStatic.toInject, InjectOnStatic.getToInject());
    }

    public class TestInjectStatic {

        @InjectStatic(target = InjectOnStatic.class, property="toInject")
        private ToInject toInject;

        public TestInjectStatic() {
            toInject = new ToInject();
        }
    }

    public class ToInject {

    }


}
