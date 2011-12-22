package org.unitils.inject.example7;

import org.unitils.UnitilsJUnit4;
import org.unitils.inject.MyDao;
import org.unitils.inject.MyService;
import org.unitils.inject.annotation.InjectIntoStatic;

import static org.unitils.inject.util.Restore.*;

public class MyServiceTest extends UnitilsJUnit4 {

    // START SNIPPET: restore
    @InjectIntoStatic(target = MyService.class, property = "myDaoSingleton", restore = OLD_VALUE)
    private MyDao myDao1;

    @InjectIntoStatic(target = MyService.class, property = "myDaoSingleton", restore = NO_RESTORE)
    private MyDao myDao2;

    @InjectIntoStatic(target = MyService.class, property = "myDaoSingleton", restore = NULL_OR_0_VALUE)
    private MyDao myDao3;
// END SNIPPET: restore

}

