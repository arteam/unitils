package org.unitils.database.example9;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.unitils.UnitilsTestExecutionListener;
import org.unitils.database.annotations.Transactional;

// START SNIPPET: test
@Transactional
@ContextConfiguration
@TestExecutionListeners(UnitilsTestExecutionListener.class)
public class MyDaoTest extends AbstractJUnit4SpringContextTests {

}
// END SNIPPET: test
