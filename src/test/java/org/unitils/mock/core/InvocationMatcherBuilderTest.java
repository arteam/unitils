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

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.MockUnitils;
import org.unitils.mock.core.argumentmatcher.NotNullArgumentMatcher;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class InvocationMatcherBuilderTest {

	InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();
	
	TestClass testClass = new TestClass();
	
	@Before
	public void setup() {
	}
	
	@Test
	public void testArgumentMatchers() {
		invocationMatcherBuilder.registerArgumentMatcher(new NotNullArgumentMatcher());
		//Invocation invocation = new Invocation(testClass, )
	}
	
	static class TestClass {
	
		public void testMethod() {
			doSomething(null, MockUnitils.notNull(String.class));
		}
		
		public void doSomething(String arg1, String arg2) {
			
		}
	}
}
