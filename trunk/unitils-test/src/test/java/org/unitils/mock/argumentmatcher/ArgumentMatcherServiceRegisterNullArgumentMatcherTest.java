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
import org.unitils.mock.Mock;
import org.unitils.mock.argumentmatcher.impl.NullArgumentMatcher;

import static org.unitils.mock.ArgumentMatchers.any;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherServiceRegisterNullArgumentMatcherTest extends UnitilsJUnit4 {

    private ArgumentMatcherService argumentMatcherService;

    private Mock<ArgumentMatcherRepository> argumentMatcherRepositoryMock;


    @Before
    public void initialize() {
        argumentMatcherService = new ArgumentMatcherService(argumentMatcherRepositoryMock.getMock(), null);
    }


    @Test
    public void registerNullArgumentMatcher() {
        argumentMatcherService.registerNullArgumentMatcher();
        argumentMatcherRepositoryMock.assertInvoked().registerArgumentMatcher(any(NullArgumentMatcher.class));
    }
}
