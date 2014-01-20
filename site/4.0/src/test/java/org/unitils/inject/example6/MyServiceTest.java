package org.unitils.inject.example6;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.MyDao;
import org.unitils.inject.MyService;
import org.unitils.inject.annotation.InjectIntoStatic;

// START SNIPPET: injectIntoStatic
public class MyServiceTest extends UnitilsJUnit4 {

    @InjectIntoStatic(target = MyService.class, property = "myDaoSingleton")
    private MyDao myDao;

}
// END SNIPPET: injectIntoStatic

