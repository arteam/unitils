package org.unitils.tapestry;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.unitils.tapestry.annotation.TapestryModule;
import org.unitils.tapestry.annotation.TapestryService;

@TapestryModule(Module.class)
public class DerivedInjectionTest extends DerivedInjectionTestBase {

	@TapestryService
	private Service myTestService;
	@TapestryService 
	private static Service myTestServiceStatic;
	
	@Test
	public void testAlsoSuperClassFieldsAreInjected() {
		assertNotNull(myTestService);
		assertNotNull(myTestServiceStatic);
		assertNotNull(testService);
		assertNotNull(testServiceStatic);
	}
	
}
