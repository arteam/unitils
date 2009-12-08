package org.unitils.tapestry;

import static org.junit.Assert.assertNotNull;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.unitils.tapestry.annotation.TapestryRegistry;

@TapestryRegistry(Module.class)
public class DerivedInjectionTest extends DerivedInjectionTestBase {

	@Inject
	private Person myTestService;
	@Inject
	private static Person myTestServiceStatic;

	@Test
	public void testAlsoSuperClassFieldsAreInjected() {
		assertNotNull(myTestService);
		assertNotNull(myTestServiceStatic);
		assertNotNull(testService);
		assertNotNull(testServiceStatic);
	}

}
