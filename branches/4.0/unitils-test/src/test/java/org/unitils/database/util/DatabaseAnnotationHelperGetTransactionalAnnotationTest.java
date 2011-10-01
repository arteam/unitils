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

import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
        Method method = AnnotationOnClass.class.getMethod("noAnnotationOnMethod");
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnClass(), method);
        assertSame(ROLLBACK, transactional.value());
    }

    @Test
    public void annotationOnMethod() throws Exception {
        Method method = AnnotationOnClass.class.getMethod("annotationOnMethod");
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnClass(), method);
        assertSame(COMMIT, transactional.value());
    }

    @Test
    public void annotationOnParentClass() throws Exception {
        Method method = AnnotationOnParentClass.class.getMethod("noAnnotationOnMethod");
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnParentClass(), method);
        assertSame(ROLLBACK, transactional.value());
    }

    @Test
    public void annotationOnChildOverridesAnnotationOnParentClass() throws Exception {
        Method method = AnnotationOnParentAndChildClass.class.getMethod("noAnnotationOnMethod");
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new AnnotationOnParentAndChildClass(), method);
        assertSame(COMMIT, transactional.value());
    }

    @Test
    public void noAnnotation() throws Exception {
        Method method = NoAnnotation.class.getMethod("noAnnotationOnMethod");
        Transactional transactional = databaseAnnotationHelper.getTransactionalAnnotation(new NoAnnotation(), method);
        assertNull(transactional);
    }


    @Transactional(ROLLBACK)
    private static class AnnotationOnClass {

        @Transactional(COMMIT)
        public void annotationOnMethod() {
        }

        public void noAnnotationOnMethod() {
        }
    }

    private static class AnnotationOnParentClass extends AnnotationOnClass {

        public void noAnnotationOnMethod() {
        }
    }

    @Transactional(COMMIT)
    private static class AnnotationOnParentAndChildClass extends AnnotationOnClass {

        public void noAnnotationOnMethod() {
        }
    }

    private static class NoAnnotation {

        public void noAnnotationOnMethod() {
        }
    }
}
