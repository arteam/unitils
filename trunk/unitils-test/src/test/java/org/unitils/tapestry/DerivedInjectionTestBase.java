package org.unitils.tapestry;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public abstract class DerivedInjectionTestBase {

	@Inject
	protected Person testService;
	@Inject
	protected static Person testServiceStatic;
	
	
}
