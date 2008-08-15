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
package org.unitils.mock.core;

/**
 * An interface to be used to check is a given argument matches a predefined value.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public interface ArgumentMatcher {
	
	/**
	 * Returns true if the given object matches this object's expected argument, false otherwise.
	 * 
	 * @param o the <code>Object</code> to match.
	 * @return true when passed object matches, false otherwise.
	 */
	public boolean matches(Object o);
}
