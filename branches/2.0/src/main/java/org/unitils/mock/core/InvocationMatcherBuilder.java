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
package org.unitils.mock.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 *
 */
public class InvocationMatcherBuilder {

	private Method method;
	
	private Object[] args;
	
	private List<ArgumentMatcher> argumentMatchers;
	
	private static InvocationMatcherBuilder instance;
	
	public static InvocationMatcherBuilder getInstance() {
		if (instance == null) {
			instance = new InvocationMatcherBuilder();
		}
		return instance;
	}
	
	
	private InvocationMatcherBuilder() {
		super();
		reset();
	}



	public void registerInvokedMethod(Method method, Object[] args) {
		this.method = method;
		this.args = args;
	}
	
	
	public void registerArgumentMatcher(ArgumentMatcher argumentMatcher) {
		argumentMatchers.add(argumentMatcher);
	}
	
	
	public InvocationMatcher createInvocationMatcher() {
		return new InvocationMatcher(method, argumentMatchers);
	}

	
	protected void reset() {
		this.method = null;
		this.args = null;
		this.argumentMatchers = new ArrayList<ArgumentMatcher>();
	}

}
