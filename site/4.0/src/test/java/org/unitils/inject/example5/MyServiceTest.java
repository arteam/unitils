package org.unitils.inject.example5;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.MyDao;
import org.unitils.inject.MyService;
import org.unitils.inject.annotation.InjectIntoByType;

// START SNIPPET: injectIntoByType
public class MyServiceTest extends UnitilsJUnit4 {

    private MyService myService;

    @InjectIntoByType(target = "myService")
    private MyDao myDao;

}
// END SNIPPET: injectIntoByType

