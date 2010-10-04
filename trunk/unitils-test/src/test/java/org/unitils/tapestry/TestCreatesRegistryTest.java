package org.unitils.tapestry;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.tapestry.annotation.TapestryRegistry;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

@TapestryRegistry(value = {Module.class}, registryFactoryMethodName = "createRegistry", registryShutdownMethodName = "shutdownRegistry")
public class TestCreatesRegistryTest extends UnitilsJUnit4 {

    private Registry registryCreatedByFactoryMethod;
    private Class<?>[] modulesPassedToCreateRegistry;
    @Inject
    private Registry injectedRegistry;

    @Test
    public void useClassFactoryMethodToCreateRegistry() {
        assertArrayEquals(new Class<?>[]{Module.class}, modulesPassedToCreateRegistry);
        assertNotNull(registryCreatedByFactoryMethod);
        assertSame(registryCreatedByFactoryMethod, injectedRegistry);
    }

    @TapestryRegistry(value = {}, registryFactoryMethodName = "createMethodRegistry")
    @Test
    public void useMethodFactoryMethodToCreateRegistry() {
        assertArrayEquals(new Class<?>[]{}, modulesPassedToCreateRegistry);
        assertNotNull(registryCreatedByFactoryMethod);
        assertSame(registryCreatedByFactoryMethod, injectedRegistry);
    }

    public Registry createMethodRegistry(Class<?>[] modules) {
        modulesPassedToCreateRegistry = modules;
        RegistryBuilder builder = new RegistryBuilder();
        for (Class<?> module : modules) {
            builder.add(module);
        }
        builder.add(Module.class);
        registryCreatedByFactoryMethod = builder.build();
        return registryCreatedByFactoryMethod;
    }

    public Registry createRegistry(Class<?>[] modules) {
        modulesPassedToCreateRegistry = modules;
        RegistryBuilder builder = new RegistryBuilder();
        for (Class<?> module : modules) {
            builder.add(module);
        }
        registryCreatedByFactoryMethod = builder.build();
        return registryCreatedByFactoryMethod;
    }

    public void shutdownRegistry(Registry registry) {
        registry.shutdown();
    }


}
