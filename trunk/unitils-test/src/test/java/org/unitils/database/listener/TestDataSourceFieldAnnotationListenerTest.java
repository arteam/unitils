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

package org.unitils.database.listener;

import org.junit.Test;
import org.unitils.core.TestPhase;

import static org.junit.Assert.assertSame;
import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class TestDataSourceFieldAnnotationListenerTest {

    /* Tested object */
    private TestDataSourceFieldAnnotationListener testDataSourceFieldAnnotationListener = new TestDataSourceFieldAnnotationListener(false, null);


    @Test
    public void getTestPhase() {
        TestPhase result = testDataSourceFieldAnnotationListener.getTestPhase();
        assertSame(CONSTRUCTION, result);
    }
}
