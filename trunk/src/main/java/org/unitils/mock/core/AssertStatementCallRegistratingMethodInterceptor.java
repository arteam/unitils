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

import java.lang.reflect.Method;
import java.util.Arrays;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class AssertStatementCallRegistratingMethodInterceptor<T> implements MethodInterceptor {

	private Scenario scenario;
	
	private MockObject<T> mockObject;
	
	private boolean checkIfInvoked;
	
	private InvocationMatcherBuilder invocationMatcherBuilder = InvocationMatcherBuilder.getInstance();
	
	
	public AssertStatementCallRegistratingMethodInterceptor(Scenario scenario, MockObject<T> mockObject, boolean checkIfInvoked) {
		super();
		this.scenario = scenario;
		this.mockObject = mockObject;
		this.checkIfInvoked = checkIfInvoked;
	}


	public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Invocation invocation = new Invocation(mockObject, method, Arrays.asList(args), 
				ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace()));
		invocationMatcherBuilder.registerInvokedMethod(invocation);
		InvocationMatcher invocationMatcher = invocationMatcherBuilder.createInvocationMatcher();
		invocationMatcherBuilder.reset();
		if (checkIfInvoked) {
			scenario.assertInvoked(invocationMatcher);
		} else {
			scenario.assertNotInvoked(invocationMatcher);
		}
		return null;
	}

}
