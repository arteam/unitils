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

import org.junit.Test;
import org.unitils.database.annotations.Transactional;

import static org.junit.Assert.*;
import static org.unitils.database.util.TransactionMode.COMMIT;
import static org.unitils.database.util.TransactionMode.ROLLBACK;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAnnotationHelperGetTransactionalAnnotationTest {

    /* Tested object */
    private DatabaseAnnotationHelper databaseAnnotationHelper = new DatabaseAnnotationHelper(COMMIT);


    @Test
    public void annotationOnClass() throws Exception {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnClass());
        assertNotNull(transactional);
    }

    @Test
    public void annotationOnParentClass() throws Exception {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnParentClass());
        assertNotNull(transactional);
    }

    @Test
    public void annotationOnChildOverridesAnnotationOnParentClass() throws Exception {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnParentAndChildClass());
        assertSame(ROLLBACK, transactional.value());
    }

    @Test
    public void noAnnotation() throws Exception {
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new NoAnnotation());
        assertNull(transactional);
    }


    @Transactional
    private static class AnnotationOnClass {
    }

    private static class AnnotationOnParentClass extends AnnotationOnClass {
    }

    @Transactional(ROLLBACK)
    private static class AnnotationOnParentAndChildClass extends AnnotationOnClass {
    }

    private static class NoAnnotation {
    }
}
