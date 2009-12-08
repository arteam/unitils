package org.unitils.tapestry;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;

public class Module {

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add("testSymbol", "testSymbolValue");
	}
	
	@SuppressWarnings("unchecked")
	public static void bind(ServiceBinder binder) {
		binder.bind(Person.class, Peter.class).withId("TestService");
		binder.bind(Animal.class, Dog.class).withId("dog").withMarker(DogMarker.class);
		binder.bind(Animal.class, Cat.class).withId("cat").withMarker(CatMarker.class);
	}

}
