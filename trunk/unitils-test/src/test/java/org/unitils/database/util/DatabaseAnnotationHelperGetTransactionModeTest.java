/*
 * Copyright Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.database.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.database.annotations.Transactional;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.unitils.database.util.TransactionMode.*;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAnnotationHelperGetTransactionModeTest {

    /* Tested object */
    private DatabaseAnnotationHelper databaseAnnotationHelper;


    @Before
    public void initialize() {
        databaseAnnotationHelper = new DatabaseAnnotationHelper(COMMIT);
    }

    @Test
    public void disabled() throws Exception {
        Transactional transactional = Disabled.class.getAnnotation(Transactional.class);

        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        assertSame(DISABLED, transactionMode);
    }

    @Test
    public void rollback() throws Exception {
        Transactional transactional = Rollback.class.getAnnotation(Transactional.class);

        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        assertSame(ROLLBACK, transactionMode);
    }

    @Test
    public void commit() throws Exception {
        Transactional transactional = Commit.class.getAnnotation(Transactional.class);

        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        assertSame(COMMIT, transactionMode);
    }

    @Test
    public void defaultMode() throws Exception {
        Transactional transactional = Default.class.getAnnotation(Transactional.class);

        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        assertSame(COMMIT, transactionMode);
    }

    @Test
    public void noModeSameAsDefaultMode() throws Exception {
        Transactional transactional = NoMode.class.getAnnotation(Transactional.class);

        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(transactional);
        assertSame(COMMIT, transactionMode);
    }

    @Test
    public void nullAnnotation() throws Exception {
        TransactionMode transactionMode = databaseAnnotationHelper.getTransactionMode(null);
        assertNull(transactionMode);
    }


    @Transactional(DISABLED)
    private static class Disabled {
    }

    @Transactional(ROLLBACK)
    private static class Rollback {
    }

    @Transactional(COMMIT)
    private static class Commit {
    }

    @Transactional(DEFAULT)
    private static class Default {
    }

    @Transactional
    private static class NoMode {
    }
}
