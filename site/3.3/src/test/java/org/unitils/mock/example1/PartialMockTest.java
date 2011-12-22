/*
 *
 *  Copyright 2010,  Unitils.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.unitils.mock.example1;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.PartialMock;
import org.unitils.mock.core.PartialMockObject;

// START SNIPPET: create
public class PartialMockTest extends UnitilsJUnit4 {

    private PartialMock<SomeService> someServiceMock;

    // END SNIPPET: create
// START SNIPPET: programmatic
    @Before
    public void initialize() {
        someServiceMock = new PartialMockObject<SomeService>(SomeService.class, this);
    }
// END SNIPPET: programmatic

    @Test
    public void overridingBehavior() {
        // START SNIPPET: partial
        someServiceMock = new PartialMockObject<SomeService>(SomeService.class, this);
        someServiceMock.returns(5).method2();
        // END SNIPPET: partial
        someServiceMock.getMock().method1();
    }

    @Test
    public void stubbingBehavior() {
        // START SNIPPET: stub
        someServiceMock.stub().method2();
        // END SNIPPET: stub

        // do the test
        someServiceMock.getMock().method1();

        someServiceMock.assertInvoked().method2();
    }
// START SNIPPET: create
}
// END SNIPPET: create
