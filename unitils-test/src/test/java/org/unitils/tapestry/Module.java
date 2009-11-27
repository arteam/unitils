package org.unitils.tapestry;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;

public class Module {

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add("testSymbol", "testSymbolValue");
	}
	
	@SuppressWarnings("unchecked")
	public static void bind(ServiceBinder binder) {
		binder.bind(Service.class, ServiceImpl.class).withId("TestService");
		binder.bind(Service2.class, Service2Impl.class).withId("marker").withMarker(ServiceMarker.class);
		binder.bind(Service2.class, Service2Impl.class).withId("marker2").withMarker(ServiceMarker2.class);
	}

}
