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
package org.unitils.mock.argumentmatcher.impl;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;

/**
 * An {@link org.unitils.mock.argumentmatcher.ArgumentMatcher} implementation that will match if the given argument is null.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class NullArgumentMatcher implements ArgumentMatcher {

	/*
	 * @see ArgumentMatcher#matches(Object)
	 */
	public boolean matches(Object o) {
		return o == null;
	}
}
