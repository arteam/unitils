package org.unitils.tapestry;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4BlockTestClassRunner;
import org.unitils.tapestry.annotation.TapestryRegistry;

@RunWith(UnitilsJUnit4BlockTestClassRunner.class)
@TapestryRegistry(value = {Module.class}, registryFactoryMethodName = "createRegistry")
public class TestCreatesStaticRegistryTest {

	private static Registry registryCreatedByFactoryMethod;
	private static Class<?>[] modulesPassedToCreateRegistry;
	@Inject
	private static Registry injectedRegistry;
	
	@Test
	public void useClassFactoryMethodToCreateRegistry() {
		assertArrayEquals(new Class<?>[] { Module.class }, modulesPassedToCreateRegistry);
		assertNotNull(registryCreatedByFactoryMethod);
		assertSame(registryCreatedByFactoryMethod, injectedRegistry);
	}
	
	@TapestryRegistry(value = {}, registryFactoryMethodName = "createMethodRegistry")
	@Test
	public void useMethodFactoryMethodToCreateRegistry() {
		assertArrayEquals(new Class<?>[] { }, modulesPassedToCreateRegistry);
		assertNotNull(registryCreatedByFactoryMethod);
		assertSame(registryCreatedByFactoryMethod, injectedRegistry);
	}
	
	public static Registry createMethodRegistry(Class<?>[] modules) {
		modulesPassedToCreateRegistry = modules;
		RegistryBuilder builder = new RegistryBuilder();
		for (Class<?> module : modules) {
			builder.add(module);
		}
		builder.add(Module.class);
		registryCreatedByFactoryMethod = builder.build();
		return registryCreatedByFactoryMethod;
	}
	
	public static Registry createRegistry(Class<?>[] modules) {
		modulesPassedToCreateRegistry = modules;
		RegistryBuilder builder = new RegistryBuilder();
		for (Class<?> module : modules) {
			builder.add(module);
		}
		registryCreatedByFactoryMethod = builder.build();
		return registryCreatedByFactoryMethod;
	}
	
}
