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

import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DatabaseAnnotationHelperGetTransactionManagerBeanNamesTest {

    /* Tested object */
    private DatabaseAnnotationHelper databaseAnnotationHelper;


    @Before
    public void initialize() {
        databaseAnnotationHelper = new DatabaseAnnotationHelper(new Properties());
    }

    @Test
    public void oneBeanName() throws Exception {
        Transactional transactional = OneBeanName.class.getAnnotation(Transactional.class);

        List<String> result = databaseAnnotationHelper.getTransactionManagerBeanNames(transactional);
        assertReflectionEquals(asList("tx1"), result);
    }

    @Test
    public void multipleBeanNames() throws Exception {
        Transactional transactional = MultipleBeanNames.class.getAnnotation(Transactional.class);

        List<String> result = databaseAnnotationHelper.getTransactionManagerBeanNames(transactional);
        assertReflectionEquals(asList("tx1", "tx2"), result);
    }

    @Test
    public void noBeanNames() throws Exception {
        Transactional transactional = NoBeanNames.class.getAnnotation(Transactional.class);

        List<String> result = databaseAnnotationHelper.getTransactionManagerBeanNames(transactional);
        assertTrue(result.isEmpty());
    }

    @Test
    public void nullAnnotation() throws Exception {
        List<String> result = databaseAnnotationHelper.getTransactionManagerBeanNames(null);
        assertTrue(result.isEmpty());
    }


    @Transactional(transactionManagerBeanNames = "tx1")
    private static class OneBeanName {
    }

    @Transactional(transactionManagerBeanNames = {"tx1", "tx2"})
    private static class MultipleBeanNames {
    }

    @Transactional
    private static class NoBeanNames {
    }
}
