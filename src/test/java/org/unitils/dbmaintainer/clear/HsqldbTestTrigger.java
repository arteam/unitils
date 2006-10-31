package org.unitils.dbmaintainer.clear;

import org.hsqldb.Trigger;

/**
 * Test trigger for testing that the HsqldbDbClearer correctly removes trigger from the database.
 */
public class HsqldbTestTrigger implements Trigger {

    public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
    }
}
