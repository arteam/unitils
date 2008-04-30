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
import java.util.Arrays;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockObjectProxyMethodInterceptor<T> implements MethodInterceptor {

	private MockObject<T> mockObject;

	
	public MockObjectProxyMethodInterceptor(MockObject<T> mockObject) {
		super();
		this.mockObject = mockObject;
	}


	public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (method.getDeclaringClass().equals(MockObjectProxy.class)) {
			return mockObject;
		}
		Invocation invocation = new Invocation(object, method, ProxyUtils.getOriginalMethod(method, mockObject.getMockedClass()), Arrays.asList(args), ProxyUtils.getProxiedMethodInvokedAt(Thread.currentThread().getStackTrace()));
		return mockObject.handleInvocation(invocation);
	}
}
