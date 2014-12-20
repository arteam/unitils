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
import org.unitils.mock.core.util.CloneService;

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


    public <T> void registerNotNullArgumentMatcher(Class<T> argumentClass) {
        ArgumentMatcher<T> argumentMatcher = new NotNullArgumentMatcher<T>();
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public <T> void registerNullArgumentMatcher(Class<T> argumentClass) {
        ArgumentMatcher<T> argumentMatcher = new NullArgumentMatcher<T>();
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public <T> void registerSameArgumentMatcher(T sameAs) {
        ArgumentMatcher<T> argumentMatcher = new SameArgumentMatcher<T>(sameAs);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public <T> void registerEqualsArgumentMatcher(T equalTo) {
        ArgumentMatcher<T> argumentMatcher = new EqualsArgumentMatcher<T>(equalTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    /**
     * A copy of the value is taken so that it can be compared even when the value itself was modified later-on.
     *
     * @param equalTo The value to compare to
     */
    public <T> void registerRefEqArgumentMatcher(T equalTo) {
        T clonedEqualTo = cloneService.createDeepClone(equalTo);
        ArgumentMatcher<T> argumentMatcher = new RefEqArgumentMatcher<T>(clonedEqualTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    /**
     * A copy of the value is taken so that it can be compared even when the value itself was modified later-on.
     *
     * @param equalTo The value to compare to
     */
    public <T> void registerLenEqArgumentMatcher(T equalTo) {
        T clonedEqualTo = cloneService.createDeepClone(equalTo);
        ArgumentMatcher<T> argumentMatcher = new LenEqArgumentMatcher<T>(clonedEqualTo);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public <T> void registerAnyArgumentMatcher(Class<T> argumentClass) {
        ArgumentMatcher<T> argumentMatcher = new AnyArgumentMatcher<T>(argumentClass);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }

    public <T> void registerCaptureArgumentMatcher(Capture<T> capture) {
        ArgumentMatcher<T> argumentMatcher = new CaptureArgumentMatcher<T>(capture);
        argumentMatcherRepository.registerArgumentMatcher(argumentMatcher);
    }
}