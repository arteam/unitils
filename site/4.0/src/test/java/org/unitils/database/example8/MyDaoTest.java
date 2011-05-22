package org.unitils.database.example8;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.unitils.UnitilsTestExecutionListener;

// START SNIPPET: test
@Transactional
@ContextConfiguration
public class MyDaoTest extends AbstractTransactionalJUnit4SpringContextTests {

}
// END SNIPPET: test
