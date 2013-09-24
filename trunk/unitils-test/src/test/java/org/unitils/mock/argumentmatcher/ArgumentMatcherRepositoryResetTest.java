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
package org.unitils.mock.argumentmatcher;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherRepositoryResetTest extends UnitilsJUnit4 {

    private ArgumentMatcherRepository argumentMatcherRepository;

    @Dummy
    private ArgumentMatcher argumentMatcher;


    @Before
    public void initialize() {
        argumentMatcherRepository = new ArgumentMatcherRepository(null, null);
    }


    @Test
    public void reset() {
        argumentMatcherRepository.startMatchingInvocation(10);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);

        argumentMatcherRepository.reset();
        try {
            argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to register argument matcher. Argument matchers can only be used when defining behavior for a mock (e.g. returns) or when doing an assert on a mock. Argument matcher: " + argumentMatcher, e.getMessage());
        }
    }
}
