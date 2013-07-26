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
package org.unitils.easymock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.annotation.RegularMock;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.unitils.easymock.EasyMockUnitils.clearMocks;
import static org.unitils.easymock.EasyMockUnitils.replay;

/**
 * @author Tim Ducheyne
 */
public class EasyMockUnitilsReplayIntegrationTest extends UnitilsJUnit4 {

    @RegularMock
    private MyInterface mock;


    @Test
    public void replayMocks() {
        expect(mock.method("1")).andReturn("1");

        replay();
        String result = mock.method("1");
        assertEquals("1", result);
    }

    @Test
    public void noBehaviorDefinedWhenReplayNotCalled() {
        expect(mock.method("1")).andReturn("1");

        String result = mock.method("1");
        assertNull(result);
        clearMocks();
    }

    @Test
    public void exceptionWhenReplayIsCalledMoreThanOnce() {
        expect(mock.method("1")).andReturn("1");

        replay();
        try {
            replay();
            fail("IllegalStateException expected");
        } catch (IllegalStateException e) {
            assertEquals("This method must not be called in replay state.", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void ignoredWhenNoBehavior() {
        replay();
    }


    public static interface MyInterface {

        String method(String arg);
    }
}