package org.unitils.inject.example3;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.MyDao;
import org.unitils.inject.MyService;
import org.unitils.inject.annotation.InjectInto;

// START SNIPPET: injectInto
public class MyServiceTest extends UnitilsJUnit4 {

    private MyService myService;

    @InjectInto(target = "myService", property = "myDaoField")
    private MyDao myDao;

}
// END SNIPPET: injectInto

