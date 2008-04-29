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
package org.unitils.mock.core.argumentmatcher;

import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import org.unitils.mock.core.ArgumentMatcher;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionComparatorChainFactory;

/**
 * An {@link ArgumentMatcher} implementation that will match if the given argument equals the <code>Object</code> passed on construction.
 * Reflection is used to compare all fields of these values, but order of collections is ignored and only fields that have a non default value will be compared. 
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class LenEqArgumentMatcher implements ArgumentMatcher {
	private final Object o;
	
	public LenEqArgumentMatcher(Object o) {
		this.o = o;
	}
	
	/*
	 * @see ArgumentMatcher#matches(Object)
	 */
	public boolean matches(Object o) {
        ReflectionComparator reflectionComparator = ReflectionComparatorChainFactory.getComparatorChainForModes(LENIENT_ORDER, IGNORE_DEFAULTS);
        return reflectionComparator.getDifference(this.o, o) == null;
	}

}
