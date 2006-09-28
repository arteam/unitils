package be.ordina.unitils.inject;

import be.ordina.unitils.inject.annotation.InjectStatic;

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
