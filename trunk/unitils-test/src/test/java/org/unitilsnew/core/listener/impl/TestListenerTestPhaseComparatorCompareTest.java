/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core.listener.impl;

import org.junit.Test;
import org.unitilsnew.core.TestPhase;
import org.unitilsnew.core.listener.TestListener;

import static org.junit.Assert.assertTrue;
import static org.unitilsnew.core.TestPhase.CONSTRUCTION;
import static org.unitilsnew.core.TestPhase.EXECUTION;

/**
 * @author Tim Ducheyne
 */
public class TestListenerTestPhaseComparatorCompareTest {

    /* Tested object */
    private TestListenerTestPhaseComparator testListenerTestPhaseComparator = new TestListenerTestPhaseComparator();

    private TestListener constructionPhaseTestListener = new ConstructionPhaseTestListener();
    private TestListener executionPhaseTestListener = new ExecutionPhaseTestListener();


    @Test
    public void lesserThan() throws Throwable {
        int result = testListenerTestPhaseComparator.compare(constructionPhaseTestListener, executionPhaseTestListener);
        assertTrue(result < 0);
    }

    @Test
    public void greaterThan() throws Throwable {
        int result = testListenerTestPhaseComparator.compare(executionPhaseTestListener, constructionPhaseTestListener);
        assertTrue(result > 0);
    }

    @Test
    public void equal() throws Throwable {
        int result = testListenerTestPhaseComparator.compare(executionPhaseTestListener, executionPhaseTestListener);
        assertTrue(result == 0);
    }


    private static class ConstructionPhaseTestListener extends TestListener {
        @Override
        public TestPhase getTestPhase() {
            return CONSTRUCTION;
        }
    }

    private static class ExecutionPhaseTestListener extends TestListener {
        @Override
        public TestPhase getTestPhase() {
            return EXECUTION;
        }
    }
}
