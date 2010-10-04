package org.unitils.tapestry;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.unitils.UnitilsJUnit4;

public abstract class DerivedInjectionTestBase extends UnitilsJUnit4 {

    @Inject
    protected Person testService;
    @Inject
    protected static Person testServiceStatic;


}
