package org.unitils.database.example5;

import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.Transactional;

import static org.unitils.database.util.TransactionMode.*;


public class MyDaoTest extends UnitilsJUnit4 {

    // START SNIPPET: transactional
    @Transactional(COMMIT)
    public void testThatWillCommit() {
    }

    @Transactional(ROLLBACK)
    public void testThatWillRollback() {
    }

    @Transactional(DISABLED)
    public void testWithoutTransaction() {
    }
    // END SNIPPET: transactional
}

