package org.unitils.dbmaintainer.maintainer.clear;

import org.hsqldb.Trigger;

/**
 */
public class HsqldbTestTrigger implements Trigger {

    private static boolean triggerExecuted;

    public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {

    }

    public static boolean isTriggerExecuted() {
        return triggerExecuted;
    }

    public static void setTriggerExecuted(boolean triggerExecuted) {
        HsqldbTestTrigger.triggerExecuted = triggerExecuted;
    }
}
