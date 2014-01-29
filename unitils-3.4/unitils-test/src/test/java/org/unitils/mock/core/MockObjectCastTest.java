/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import static junit.framework.Assert.assertNotNull;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.PartialMock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Tests the casting of mock objects (UNI-168 and UNI-169).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@SuppressWarnings({"RedundantCast"})
public class MockObjectCastTest extends UnitilsJUnit4 {

    private PartialMock<MyTimerTask> myTimerTask;


    @Test
    public void testCasting() throws Exception {
        assertNotNull((TimerTask) myTimerTask.getMock());

        Timer timer = new Timer();
        timer.schedule(myTimerTask.getMock(), 0);
    }


    public static class MyTimerTask extends TimerTask {
        public void run() {
        }
    }

}