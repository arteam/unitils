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
package org.unitils.core.spring;

import org.junit.Test;
import org.springframework.test.context.TestContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class SpringTestManagerIsSpringTestTest extends UnitilsJUnit4 {

    /* Tested object */
    private SpringTestManager springTestManager = new SpringTestManager();

    @Dummy
    private TestContext testContext;


    @Test
    public void trueWhenTestContextSet() throws Exception {
        springTestManager.setSpringTestContext(testContext);

        boolean result = springTestManager.isSpringTest();
        assertTrue(result);
    }

    @Test
    public void falseWhenTestContextNotSet() throws Exception {
        boolean result = springTestManager.isSpringTest();
        assertFalse(result);
    }
}
