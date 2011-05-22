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

package org.unitils.mock.example4;

import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.PartialMock;
import org.unitils.mock.core.PartialMockObject;

public class PrototypeTest extends UnitilsJUnit4 {

    private PartialMock<SomeService> someServiceMock;

    // START SNIPPET: prototype
    @Before
    public void initialize() {
        SomeService someServicePrototype = new SomeService(999, "default");
        someServiceMock = new PartialMockObject<SomeService>(someServicePrototype, this);
    }
    // END SNIPPET: prototype
}
