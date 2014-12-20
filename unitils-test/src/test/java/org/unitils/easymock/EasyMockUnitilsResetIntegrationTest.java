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
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.RegularMock;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.*;

/**
 * @author Tim Ducheyne
 */
public class EasyMockUnitilsResetIntegrationTest extends UnitilsJUnit4 {

    @RegularMock
    private MyInterface mock;

    @Test
    public void resetMockBehavior() {
        expect(mock.method("1")).andReturn("1");
        replay();

        reset();
        expect(mock.method("1")).andReturn("2");
        replay();
        String value = mock.method("1");
        assertEquals("2", value);
    }

    @Test
    public void resetMockState() {
        replay();

        reset();
        try {
            verify();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to verify mocks control. Be sure to call replay before using the mock.\n" +
                    "Reason: IllegalStateException: calling verify is not allowed in record state", e.getMessage());
            clearMocks();
        }
    }


    public static interface MyInterface {

        String method(String arg);
    }
}