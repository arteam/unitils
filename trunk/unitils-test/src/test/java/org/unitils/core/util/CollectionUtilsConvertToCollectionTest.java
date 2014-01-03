/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.core.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class CollectionUtilsConvertToCollectionTest {

    @Test
    public void array() {
        Collection<?> result = CollectionUtils.convertToCollection(new int[]{1, 2});
        assertEquals(asList(1, 2), result);
    }

    @Test
    public void collection() {
        List<String> list = new ArrayList<String>();
        Collection<?> result = CollectionUtils.convertToCollection(list);
        assertSame(list, result);
    }

    @Test
    public void classCastExceptionWhenNotAnArrayOrCollection() {
        try {
            CollectionUtils.convertToCollection("1");
            fail("ClassCastException expected");
        } catch (ClassCastException e) {
            assertEquals("java.lang.String cannot be cast to [Ljava.lang.Object;", e.getMessage());
        }
    }

    @Test
    public void nullWhenNull() {
        Collection<?> result = CollectionUtils.convertToCollection(null);
        assertNull(result);
    }

    @Test
    public void constructionForCoverage() {
        new CollectionUtils();
    }
}
