package org.unitils.tapestry;

import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.tapestry.annotation.TapestryService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public abstract class DerivedInjectionTestBase {

	@TapestryService
	protected Service testService;
	@TapestryService
	protected static Service testServiceStatic;
	
	
}
