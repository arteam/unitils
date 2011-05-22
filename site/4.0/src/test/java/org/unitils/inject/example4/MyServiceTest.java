package org.unitils.inject.example4;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.MyDao;
import org.unitils.inject.MyService;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

// START SNIPPET: injectIntoByType
public class MyServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private MyService myService;

    @InjectIntoByType
    private MyDao myDao;

}
// END SNIPPET: injectIntoByType

