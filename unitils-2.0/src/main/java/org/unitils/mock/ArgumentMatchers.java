/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.mock;

import org.unitils.mock.annotation.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.argumentmatcher.impl.*;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class ArgumentMatchers {


    @ArgumentMatcher
    public static <T> T notNull(Class<T> argumentClass) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new NotNullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    public static <T> T isNull(Class<T> argumentClass) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new NullArgumentMatcher());
        return null;
    }


    @ArgumentMatcher
    public static <T> T same(T sameAs) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new SameArgumentMatcher(sameAs));
        return null;
    }


    @ArgumentMatcher
    public static <T> T eq(T equalTo) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new EqualsArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public static <T> T refEq(T equalTo) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new RefEqArgumentMatcher(equalTo));
        return null;
    }


    @ArgumentMatcher
    public static <T> T lenEq(T equalTo) {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new LenEqArgumentMatcher(equalTo));
        return null;
    }


}