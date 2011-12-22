/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.unitils.mock.example7;

import org.unitils.mock.annotation.AfterCreateMock;

public class AfterMockCreateTest {

    // START SNIPPET: afterMockCreate
    @AfterCreateMock
    void injectMock(Object mock, String name, Class type) {
        ServiceLocator.injectService(type, mock);
    }
    // END SNIPPET: afterMockCreate

    private static class ServiceLocator {

        public static void injectService(Class type, Object mock){
        }
    }
}

