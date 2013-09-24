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

import org.unitils.mock.argumentmatcher.impl.*;
import org.unitils.mock.core.proxy.CloneService;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatcherService {

    protected ArgumentMatcherRepository argumentMatcherRepository;
    protected CloneService cloneService;


    public ArgumentMatcherService(ArgumentMatcherRepository argumentMatcherRepository, CloneService cloneService) {
        this.argumentMatcherRepository = argumentMatcherRepository;
        this.cloneService = cloneService;
    }


    public void registerNotNullArgumentMatcher() {
        ArgumentMatcher argumentMatcher = new NotNullArgumentMatcher();
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public void registerNullArgumentMatcher() {
        ArgumentMatcher argumentMatcher = new NullArgumentMatcher();
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public void registerSameArgumentMatcher(Object sameAs) {
        ArgumentMatcher argumentMatcher = new SameArgumentMatcher(sameAs);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public void registerEqualsArgumentMatcher(Object equalTo) {
        ArgumentMatcher argumentMatcher = new EqualsArgumentMatcher(equalTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    /**
     * A copy of the value is taken so that it can be compared even when the value itself was modified later-on.
     *
     * @param equalTo The value to compare to
     */
    public void registerRefEqArgumentMatcher(Object equalTo) {
        Object clonedEqualTo = cloneService.createDeepClone(equalTo);
        ArgumentMatcher argumentMatcher = new RefEqArgumentMatcher(clonedEqualTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    /**
     * A copy of the value is taken so that it can be compared even when the value itself was modified later-on.
     *
     * @param equalTo The value to compare to
     */
    public void registerLenEqArgumentMatcher(Object equalTo) {
        Object clonedEqualTo = cloneService.createDeepClone(equalTo);
        ArgumentMatcher argumentMatcher = new LenEqArgumentMatcher(clonedEqualTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public void registerAnyArgumentMatcher(Class<?> argumentClass) {
        ArgumentMatcher argumentMatcher = new AnyArgumentMatcher(argumentClass);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }
}