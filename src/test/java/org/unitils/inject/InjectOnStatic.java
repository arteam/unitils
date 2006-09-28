package org.unitils.inject;

/**
 * @author Filip Neven
 */
public class InjectOnStatic {

    private static InjectModuleInjectStaticTest.ToInject toInject;

    public static void setToInject(InjectModuleInjectStaticTest.ToInject toInject) {
        InjectOnStatic.toInject = toInject;
    }

    public static InjectModuleInjectStaticTest.ToInject getToInject() {
        return toInject;
    }
}
